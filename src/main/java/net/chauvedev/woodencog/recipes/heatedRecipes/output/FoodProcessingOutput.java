package net.chauvedev.woodencog.recipes.heatedRecipes.output;

import com.google.gson.*;
import com.ibm.icu.impl.Pair;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.Create;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.WoodenCogFoodPortion;
import net.chauvedev.woodencog.utils.CogUtil;
import net.chauvedev.woodencog.utils.ModTags;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodData;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.component.item.ItemListComponent;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.*;

public class FoodProcessingOutput extends DynamicProcessingOutput<List<ItemStack>> {

    public static final MapCodec<FoodProcessingOutput> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ProcessingOutput.CODEC_NEW.fieldOf("internal").forGetter(DynamicProcessingOutput::getInternal),
            FoodData.CODEC.fieldOf("baseFoodData").forGetter(FoodProcessingOutput::getBaseFoodData),
            WoodenCogFoodPortion.CODEC.listOf().fieldOf("portions").forGetter(FoodProcessingOutput::getPortions)
    ).apply(instance, FoodProcessingOutput::new));


    public static final StreamCodec<RegistryFriendlyByteBuf, FoodProcessingOutput> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    private final FoodData baseFoodData;
    private final List<WoodenCogFoodPortion> portions;


    public FoodData getBaseFoodData() {
        return baseFoodData;
    }

    public List<WoodenCogFoodPortion> getPortions() {
        return portions;
    }

    public FoodProcessingOutput(ProcessingOutput processingOutput, FoodData baseFoodData, List<WoodenCogFoodPortion> portions) {
        super(processingOutput);
        this.baseFoodData = baseFoodData;
        this.portions = portions;
    }

    public static FoodProcessingOutput of(Item item, int count , float chance, FoodData baseFoodData, List<WoodenCogFoodPortion> portions){
        return new FoodProcessingOutput(new ProcessingOutput(new ItemStack(item,count),chance), baseFoodData, portions);
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

        FoodCapability.setFoodForDynamicItemOnCreate(outputStack, new FoodData(this.baseFoodData.hunger(), water, saturation, 0, nutrition, this.baseFoodData.decayModifier()));
        outputStack.set(TFCComponents.INGREDIENTS, ItemListComponent.of(usedItems));
        FoodCapability.setCreationDate(outputStack,FoodCapability.getRoundedCreationDate());
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

            return FoodProcessingOutput.of(item,count,chance, new FoodData(hunger,water,saturation,0, nutrients ,decayModifier), portions);
        }
    }
}

