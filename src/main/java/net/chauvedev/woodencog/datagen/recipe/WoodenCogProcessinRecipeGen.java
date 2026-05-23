package net.chauvedev.woodencog.datagen.recipe;

import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.datagen.DataGenStaticData;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Consumer;

public abstract class WoodenCogProcessinRecipeGen {

    RecipeOutput recipeOutput;

    public WoodenCogProcessinRecipeGen(RecipeOutput recipeOutput){
        this.recipeOutput = recipeOutput;
    }

    public void forEachMetal(MetalAction action){
        DataGenStaticData.METAL_REGISTRY.forEach((name, metal) -> {
            Item ingot = BuiltInRegistries.ITEM.get(TFCIngotResourceLocation(metal.id()));
            Item doubleIngot = BuiltInRegistries.ITEM.get(TFCDoubleIngotResourceLocation(metal.id()));
            Item sheet = BuiltInRegistries.ITEM.get(TFCSheetIngotResourceLocation(metal.id()));
            Fluid metalFluid = BuiltInRegistries.FLUID.get(TFCMetalResourceLocation(metal.id()));
            action.accept(metal, ingot, doubleIngot, sheet, metalFluid);
        });
    }

    //public void forEachOre(OreAction action){
    //    DataGenStaticData.ORE_REGISTRY.forEach(ore -> {
    //        Item smallOre = BuiltInRegistries.ITEM.get(TFCOreResourceLocation("small_"+ore.oreId())); //TODO small
    //        Item poorOre = BuiltInRegistries.ITEM.get(TFCOreResourceLocation("poor_"+ore.oreId()));
    //        Item normalOre = BuiltInRegistries.ITEM.get(TFCOreResourceLocation("normal_"+ore.oreId()));
    //        Item richOre = BuiltInRegistries.ITEM.get(TFCOreResourceLocation("rich_"+ore.oreId()));
//
    //        Fluid metalFluid = BuiltInRegistries.FLUID.get(TFCMetalResourceLocation(ore.metalId()));
//
    //        action.accept(ore,smallOre,poorOre,normalOre,richOre, metalFluid);
    //    });
    //}

    public static void forEachSmallOre(SmallOreAction action){
        DataGenStaticData.SMALl_ORES.forEach((ore, smallOreItem) -> {
            Item orePowder = TFCItems.ORE_POWDERS.get(ore).asItem();
            Fluid oreMetalFluid = TFCFluids.METALS.get(ore.metal()).getSource().getSource();
            DataGenStaticData.WoodencogHeatDefinition heatDefinition = DataGenStaticData.ORE_HEAT_DEFINITION.get(ore);

            action.accept(ore, smallOreItem, oreMetalFluid, orePowder, heatDefinition);
        });
    }

    public static void forEachOreWithDust(OreWithDustAction action){
        TFCItems.ORE_POWDERS.forEach((ore, itemId) -> {
            Item orePowder = itemId.get();
            if(TFCItems.ORES.get(ore) != null){
                Item oreItem = TFCItems.ORES.get(ore).asItem();
                action.accept(ore, oreItem, orePowder, 4);
            }

            if(TFCItems.GRADED_ORES.get(ore) == null) return;

            TFCItems.GRADED_ORES.get(ore).forEach((grade, gradedItemId) -> {
                Item oreItem = gradedItemId.get();
                int amount = grade == Ore.Grade.POOR ? 3 : (grade == Ore.Grade.NORMAL ? 5 : 7);

                action.accept(ore, oreItem, orePowder, amount);
            });
        });
    }

    public static void forEachGradedOre(GradedOreAction action){
        TFCItems.GRADED_ORES.forEach((ore, gradeItemIdMap) -> {
            if(!ore.isGraded()) return;
            Item oreDust = TFCItems.ORE_POWDERS.get(ore).asItem();
            Fluid oreMetalFluid = TFCFluids.METALS.get(ore.metal()).getSource().getSource();

            DataGenStaticData.WoodencogHeatDefinition heatDefinition = DataGenStaticData.ORE_HEAT_DEFINITION.get(ore);
            if(heatDefinition == null) WoodenCog.LOGGER.warn(ore.name() + " has no heat definition");

            gradeItemIdMap.forEach((grade, itemId) -> {
                Item oreItem = itemId.asItem();
                int oreDustAmount = grade == Ore.Grade.POOR ? 3 : (grade == Ore.Grade.NORMAL ? 5 : 7);
                action.accept(ore, oreItem, oreMetalFluid, oreDust, oreDustAmount, heatDefinition);
            });
        });
    }

    public void forEachFruit(FruitAction action){
        TFCItems.FRUIT_PRESERVES.forEach((food, itemRegistryObject) -> {
            Item jam = itemRegistryObject.get();
            Item fruit = TFCItems.FOOD.get(food).get();
            action.accept(food,jam,fruit);
        });
    }

    /**
     * Implement creation of builder for the recipe type
     */
    protected abstract HeatedProcessingRecipeBuilder<?> builder();

    protected ResourceLocation dyeRecipeResourceLocation(DyeColor dyeColor) {
        return WoodenCog.asResource("heated_mixing/dyeing_"+dyeColor.getSerializedName());
    }

    protected ResourceLocation alloyingRecipeResourceLocation(Metal metalEnum) {
        return WoodenCog.asResource("alloying_"+metalEnum.getSerializedName());
    }

    protected ResourceLocation oreMeltingRecipeResourceLocation(String oreId) {
        return WoodenCog.asResource(oreId+"_to_liquid");
    }

    protected ResourceLocation TFCMetalResourceLocation(Metal metalEnum) {
        return ResourceLocation.tryBuild("tfc","metal/"+metalEnum.getSerializedName());
    }

    protected ResourceLocation TFCMetalResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/"+metalId);
    }

    protected ResourceLocation TFCIngotResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/ingot/"+metalId);
    }

    protected ResourceLocation TFCDoubleIngotResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/double_ingot/"+metalId);
    }

    protected ResourceLocation TFCSheetIngotResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/sheet/"+metalId);
    }

    protected ResourceLocation TFCOreResourceLocation(String oreId) {
        return ResourceLocation.tryBuild("tfc","ore/"+oreId);
    }

    @FunctionalInterface
    public interface MetalAction {
        void accept(DataGenStaticData.Metal metal, Item ingot, Item doubleIngot, Item sheet, Fluid fluidMetal);
    }

    @FunctionalInterface
    public interface GradedOreAction {
        void accept(Ore ore, Item oreItem, Fluid oreMetalFluid, Item oreDust, int oreDustAmount, DataGenStaticData.WoodencogHeatDefinition heatDefinition);
    }

    @FunctionalInterface
    public interface OreWithDustAction {
        void accept(Ore ore, Item oreItem, Item orePowder, int amount);
    }

    @FunctionalInterface
    public interface SmallOreAction {
        void accept(Ore ore, Item smallOreItem, Fluid oreMetalFluid, Item orePowder, DataGenStaticData.WoodencogHeatDefinition heatDefinition);
    }

    @FunctionalInterface
    public interface FruitAction {
        void accept(Food food, Item jam, Item fruit);
    }
}
