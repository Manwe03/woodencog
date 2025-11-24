package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.WoodenCogFoodPortion;
import net.chauvedev.woodencog.utils.CogUtil;
import net.chauvedev.woodencog.utils.ModTags;
import net.dries007.tfc.common.capabilities.food.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class FoodProcessingOutput extends DynamicProcessingOutput<List<ItemStack>> {

    private final FoodData baseFoodData;
    private final List<WoodenCogFoodPortion> portions;

    public FoodProcessingOutput(ItemStack stack, float chance, FoodData baseFoodData, List<WoodenCogFoodPortion> portions) {
        super(stack, chance);
        this.baseFoodData = baseFoodData;
        this.portions = portions;
    }

    public static FoodProcessingOutput of(Item item, int count , float chance, FoodData baseFoodData, List<WoodenCogFoodPortion> portions){
        return new FoodProcessingOutput(new ItemStack(item,count),chance,baseFoodData,portions);
    }

    @Override
    public ProcessingOutputTypes getType() {
        return ProcessingOutputTypes.FOOD;
    }

    @Override
    public ItemStack rollOutput() {
        ItemStack outputStack = super.rollOutput();
        if(baseFoodData == FoodData.EMPTY) return outputStack;
        //Sort list to be able to stack results
        this.getDynamicData().sort(Comparator.comparing(ItemStack::getCount)
                .thenComparing((itemx) -> BuiltInRegistries.ITEM.getKey(itemx.getItem())));

        this.setFoodData(outputStack, this.getDynamicData());
        return outputStack;
    }

    private void setFoodData(ItemStack outputStack, List<ItemStack> usedItems){
        IFood inputFood = FoodCapability.get(outputStack);
        if (inputFood instanceof FoodHandler.Dynamic handler) {
            float water = baseFoodData.water();
            float saturation = baseFoodData.saturation();
            float[] nutrition = Arrays.copyOf(baseFoodData.nutrients(), Nutrient.VALUES.length);

            if(portions != null) {
                for (ItemStack usedItem : usedItems) {

                    WoodenCogFoodPortion portion = CogUtil.getOrDefault(portions,0, WoodenCogFoodPortion.empty());
                    if (usedItem.copy().is(ModTags.Compat.BREADS)) {
                        portion = CogUtil.getOrDefault(portions,1, WoodenCogFoodPortion.empty());
                    }

                    IFood cap = FoodCapability.get(usedItem);
                    if(CogUtil.logConditional(cap == null,this.getClass(),usedItem.getItem()+" : was used in recipe but has no food capability")) continue;
                    FoodData food = cap.getData();

                    for (Nutrient nutrient : Nutrient.VALUES) {
                        nutrition[nutrient.ordinal()] += food.nutrient(nutrient) * portion.nutrientModifier() * usedItem.getCount();
                    }
                    water += food.water() * portion.waterModifier() * (float) usedItem.getCount();
                    saturation += food.saturation() * portion.saturationModifier() * (float) usedItem.getCount();
                }
            }

            FoodData newFoodData = FoodData.create(this.baseFoodData.hunger(), water, saturation, nutrition, this.baseFoodData.decayModifier());

            handler.setFood(newFoodData);
            handler.setIngredients(usedItems);
            handler.setCreationDate(FoodCapability.getRoundedCreationDate());
        }
    }

    @Override
    public JsonElement serialize() {
        JsonArray portionsArray = new JsonArray();
        for (WoodenCogFoodPortion portion : portions){
            portionsArray.add(portion.write());
        }

        JsonObject json = (JsonObject) super.serialize();
        if(baseFoodData != FoodData.EMPTY) json.add("food_data", CogUtil.foodDataNbtToJson(this.baseFoodData.write()));
        if(!portions.isEmpty()) json.add("portions",portionsArray);
        return json;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        super.write(buf);
        baseFoodData.encode(buf);
        buf.writeInt(portions.size());
        for(WoodenCogFoodPortion portion : portions){
            portion.encode(buf);
        }
    }

    public static FoodProcessingOutput deserialize(JsonElement je) {
        if (!je.isJsonObject()) {
            throw new JsonSyntaxException("ProcessingOutput must be a json object");
        } else {
            JsonObject json = je.getAsJsonObject();
            String itemId = GsonHelper.getAsString(json, "item");
            int count = GsonHelper.getAsInt(json, "count", 1);
            float chance = GsonHelper.isValidNode(json, "chance") ? GsonHelper.getAsFloat(json, "chance") : 1.0F;

            ItemLike item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(itemId));
            if (item == null) {
                WoodenCog.LOGGER.error("[WoodenCog] Unknown item in registry: " + ResourceLocation.tryParse(itemId));
                return null;
            }
            ItemStack itemstack = new ItemStack(item, count);

            if (GsonHelper.isValidNode(json, "nbt")) {
                try {
                    JsonElement element = json.get("nbt");
                    itemstack.setTag(TagParser.parseTag(element.isJsonObject() ? Create.GSON.toJson(element) : GsonHelper.convertToString(element, "nbt")));
                } catch (CommandSyntaxException var7) {
                    WoodenCog.LOGGER.error(Arrays.toString(var7.getStackTrace()));
                }
            }

            FoodData baseFoodData = json.has("food_data") ? FoodData.read(GsonHelper.getAsJsonObject(json,"food_data")) : FoodData.EMPTY;

            List<WoodenCogFoodPortion> portions = json.has("portions") ? WoodenCogFoodPortion.readArray(GsonHelper.getAsJsonArray(json,"portions")) : List.of();

            return new FoodProcessingOutput(itemstack, chance, baseFoodData, portions);
        }
    }

    public static FoodProcessingOutput read(FriendlyByteBuf buf) {
        ItemStack itemstack = buf.readItem();
        float chance = buf.readFloat();
        FoodData baseFoodData = FoodData.decode(buf);
        List<WoodenCogFoodPortion> portions = WoodenCogFoodPortion.decodeArray(buf);
        return new FoodProcessingOutput(itemstack, chance, baseFoodData, portions);
    }

    public static class Builder{

        Item item = null;
        int count = 1;
        float chance = 1;

        int hunger;
        float water;
        float saturation;
        float[] nutrients = new float[Nutrient.TOTAL];
        float decayModifier;

        List<WoodenCogFoodPortion> portions = List.of();

        public static Builder create(){
            return new Builder();
        }

        public Builder withItem(Item item){
            this.item = item;
            return this;
        }

        public Builder withItem(Item item, int count){
            this.item = item;
            this.count = count;
            return this;
        }

        public Builder withItem(Item item, int count, float chance){
            this.item = item;
            this.count = count;
            this.chance = chance;
            return this;
        }

        public Builder withFoodData(int hunger, float water, float saturation, float decayModifier){
            this.hunger = hunger;
            this.water = water;
            this.saturation = saturation;
            this.decayModifier = decayModifier;
            return this;
        }

        public Builder withEmptyNutrients(){
            return this;
        }

        public Builder withNutrients(float[] nutrients){
            this.nutrients = nutrients;
            return this;
        }

        public Builder withPortions(List<WoodenCogFoodPortion> portions){
            this.portions = portions;
            return this;
        }

        public FoodProcessingOutput build(){
            if(item == null) throw new RuntimeException("Item is null, can not build a FoodProcessingOutput with null item");
            return FoodProcessingOutput.of(item,count,chance, FoodData.create(hunger,water,saturation, nutrients ,decayModifier), portions);
        }
    }
}

