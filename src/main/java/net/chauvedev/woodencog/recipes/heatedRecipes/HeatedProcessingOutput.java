package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.*;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.utils.CogUtil;
import net.chauvedev.woodencog.utils.ModTags;
import net.createmod.catnip.data.Pair;
import net.createmod.catnip.platform.CatnipServices;
import net.dries007.tfc.common.capabilities.food.*;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HeatedProcessingOutput extends ProcessingOutput {

    private static final Logger log = LoggerFactory.getLogger(HeatedProcessingOutput.class);
    private Pair<ResourceLocation, Integer> childCompatDatagenOutput;
    private final int temperature;
    private final boolean copyHeat;
    private final int cooling;
    private final FoodData baseFoodData;
    private List<WoodenCogFoodPortion> portions;

    private float dynamicOutputTemp;
    private List<ItemStack> dynamicUsedFoodItems;

    public HeatedProcessingOutput(ItemStack stack, float chance, int temperature, boolean copyHeat, int cooling) {
        this(stack, chance,temperature,copyHeat,cooling,null,null);
    }

    public HeatedProcessingOutput(ItemStack stack, float chance, FoodData baseFoodData, List<WoodenCogFoodPortion> portions) {
        this(stack, chance,0,false,0, baseFoodData, portions);
    }

    public HeatedProcessingOutput(ItemStack stack, float chance, int temperature, boolean copyHeat, int cooling, FoodData baseFoodData, List<WoodenCogFoodPortion> portions) {
        super(stack, chance);
        this.temperature = temperature;
        this.copyHeat = copyHeat;
        this.cooling = cooling;
        this.baseFoodData = baseFoodData;
        this.portions = portions;
    }

    public HeatedProcessingOutput(ItemStack stack, float chance, HeatedProcessingRecipeBuilder.HeatedIngridientParams params) {
        this(stack, chance, params.temperature, params.copyHeat, params.cooling);
    }

    public HeatedProcessingOutput(Pair<ResourceLocation, Integer> item, float chance, HeatedProcessingRecipeBuilder.HeatedIngridientParams params) {
        super(item,chance);
        this.childCompatDatagenOutput = item;
        this.temperature = params.temperature;
        this.copyHeat = params.copyHeat;
        this.cooling = params.cooling;
        this.baseFoodData = null;
    }

    public int getTemperature() {
        return temperature;
    }
    public boolean getCopyHeat(){
        return copyHeat;
    }
    public int getCooling(){
        return cooling;
    }
    public FoodData getFoodData() { return baseFoodData; }

    /**
     * This method has a side effect should be called before rollOutput()
     * @param temp
     */
    public void setDynamicOutputTemp(float temp){
        this.dynamicOutputTemp = temp;
    }

    public void setDynamicUsedFoodItems(List<ItemStack> usedFoodItems){
        this.dynamicUsedFoodItems = usedFoodItems;
        //Collections.reverse(dynamicUsedFoodItems);
    }

    /**
     * Returns the itemStack with the applied capability
     */
    @Override
    public ItemStack getStack() {
        ItemStack itemStack = super.getStack();
        if(!this.copyHeat){
            HeatCapability.setTemperature(itemStack,this.temperature);
        }
        return itemStack;
    }

    @Override
    public ItemStack rollOutput() {
        ItemStack outputStack = super.rollOutput();
        if(WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
            HeatCapability.setTemperature(outputStack,this.getTemperature());
            if(this.getCopyHeat()) { //If copy input item heat - cooling
                HeatCapability.setTemperature(outputStack, this.dynamicOutputTemp - this.getCooling());
            }
        }
        if(hasFoodData()){
            IFood inputFood = FoodCapability.get(outputStack);
            this.setFoodData(inputFood);
        }
        return outputStack;
    }

    private void setFoodData(IFood inputFood){
        if (inputFood instanceof FoodHandler.Dynamic handler) {
            float water = baseFoodData.water();
            float saturation = baseFoodData.saturation();
            float[] nutrition = Arrays.copyOf(baseFoodData.nutrients(), Nutrient.VALUES.length);

            //Sort list to be able to stack results
            dynamicUsedFoodItems.sort(Comparator.comparing(ItemStack::getCount)
                    .thenComparing((itemx) -> BuiltInRegistries.ITEM.getKey(itemx.getItem())));

            if(portions != null) {
                for (ItemStack usedItem : dynamicUsedFoodItems) {

                    WoodenCogFoodPortion portion = CogUtil.getOrDefault(portions,1, WoodenCogFoodPortion.empty());
                    if (usedItem.copy().is(ModTags.Compat.BREADS)) {
                        portion = CogUtil.getOrDefault(portions,0, WoodenCogFoodPortion.empty());
                    }

                    FoodData food = FoodCapability.get(usedItem).getData();

                    for (Nutrient nutrient : Nutrient.VALUES) {
                        nutrition[nutrient.ordinal()] += food.nutrient(nutrient) * portion.nutrientModifier * usedItem.getCount();
                    }
                    water += food.water() * portion.waterModifier * (float) usedItem.getCount();
                    saturation += food.saturation() * portion.saturationModifier * (float) usedItem.getCount();
                }
            }

            FoodData newFoodData = FoodData.create(this.baseFoodData.hunger(), water, saturation, nutrition, this.baseFoodData.decayModifier());

            handler.setFood(newFoodData);
            handler.setIngredients(dynamicUsedFoodItems);
            handler.setCreationDate(FoodCapability.getRoundedCreationDate());
        }
    }

    private static void logFoodData(FoodData foodData){
        WoodenCog.LOGGER.info(
            "\nHunger "+foodData.hunger() +
            "\nWater "+foodData.water() +
            "\nSaturation "+foodData.saturation() +
            "\nNutrients"+ Arrays.toString(foodData.nutrients()) +
            "\nDecay"+foodData.decayModifier());
    }

    private static void logPortions(WoodenCogFoodPortion portion){
        WoodenCog.LOGGER.info(
                "\nNutrient Modifier "+portion.nutrientModifier +
                "\nWater Modifier"+portion.waterModifier +
                "\nSaturation Modifier"+portion.saturationModifier);
    }

    @Override
    public JsonElement serialize() {
        JsonObject json = new JsonObject();
        ResourceLocation resourceLocation = this.childCompatDatagenOutput == null ? CatnipServices.REGISTRIES.getKeyOrThrow(this.getStack().getItem()) : this.childCompatDatagenOutput.getFirst();
        json.addProperty("item", resourceLocation.toString());
        int count = this.childCompatDatagenOutput == null ? this.getStack().getCount() : this.childCompatDatagenOutput.getSecond();
        if (count != 1) {
            json.addProperty("count", count);
        }

        if (this.getStack().hasTag()) {
            json.add("nbt", JsonParser.parseString(this.getStack().getTag().toString()));
        }

        if (this.getChance() != 1.0F) {
            json.addProperty("chance", this.getChance());
        }

        if (WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
            json.addProperty("temperature", this.getTemperature());
            json.addProperty("copy_heat",this.getCopyHeat());
            json.addProperty("cooling",this.getCooling());
        }

        if(this.hasFoodData()){
            json.add("food_data", CogUtil.foodDataNbtToJson(this.baseFoodData.write()));
            JsonArray portionsArray = new JsonArray();
            for (WoodenCogFoodPortion portion : portions){
                portionsArray.add(portion.write());
            }
            json.add("portions",portionsArray);
        }
        return json;
    }

    public static HeatedProcessingOutput deserialize(JsonElement je) {

        if (!je.isJsonObject()) {
            throw new JsonSyntaxException("ProcessingOutput must be a json object");
        } else {
            JsonObject json = je.getAsJsonObject();
            String itemId = GsonHelper.getAsString(json, "item");
            int count = GsonHelper.getAsInt(json, "count", 1);
            float chance = GsonHelper.isValidNode(json, "chance") ? GsonHelper.getAsFloat(json, "chance") : 1.0F;

            WoodenCog.LOGGER.info("[WoodenCog] Create Resource Location from: " + itemId);
            try {
                ResourceLocation rl = ResourceLocation.tryParse(itemId);
            } catch (Exception e) {
                WoodenCog.LOGGER.error("[WoodenCog] Invalid Resource Location: " + itemId, e);
            }

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
            int temperature = 0;
            boolean copyHeat = false;
            int cooling = 0;
            try {
                temperature = GsonHelper.getAsInt(json, "temperature");
            }catch (JsonSyntaxException ignored){}
            try {
                copyHeat = GsonHelper.getAsBoolean(json, "copy_heat");
            }catch (JsonSyntaxException ignored){}
            try {
                cooling = GsonHelper.getAsInt(json, "cooling");
            }catch (JsonSyntaxException ignored){}

            try {
                FoodData baseFoodData = FoodData.read(GsonHelper.getAsJsonObject(json,"food_data"));
                List<WoodenCogFoodPortion> portions = WoodenCogFoodPortion.readArray(GsonHelper.getAsJsonArray(json,"portions"));
                return new HeatedProcessingOutput(itemstack, chance, temperature, copyHeat, cooling, baseFoodData, portions);
            } catch (JsonSyntaxException ignored){}

            return new HeatedProcessingOutput(itemstack, chance, temperature, copyHeat, cooling);
        }
    }

    @Override
    public void write(FriendlyByteBuf buf) {

        //super.write(buf);
        buf.writeItem(getStack());
        buf.writeFloat(getChance());

        buf.writeInt(getTemperature());
        buf.writeBoolean(getCopyHeat());
        buf.writeInt(getCooling());

        if(hasFoodData()) {
            getFoodData().encode(buf);
            buf.writeInt(portions.size());
            for(WoodenCogFoodPortion portion : portions){
                portion.encode(buf);
            }
        }
    }

    public static HeatedProcessingOutput read(FriendlyByteBuf buf) {

        ItemStack stack = buf.readItem();
        float chance = buf.readFloat();
        int temperature = buf.readInt();
        boolean copyHeat = buf.readBoolean();
        int cooling = buf.readInt();

        try{
            FoodData baseFoodData = FoodData.decode(buf);
            List<WoodenCogFoodPortion> portions = WoodenCogFoodPortion.decodeArray(buf);
            return new HeatedProcessingOutput(stack, chance, temperature, copyHeat, cooling, baseFoodData, portions);
        } catch (Exception ignored){}

        return new HeatedProcessingOutput(stack, chance, temperature, copyHeat, cooling);
    }

    public boolean hasFoodData(){
        return baseFoodData != null;
    }
}
