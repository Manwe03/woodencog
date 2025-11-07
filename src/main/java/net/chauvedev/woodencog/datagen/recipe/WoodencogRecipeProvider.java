package net.chauvedev.woodencog.datagen.recipe;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.datagen.DataGenStaticData;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.util.Metal;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class WoodencogRecipeProvider extends RecipeProvider {

    public WoodencogRecipeProvider(DataGenerator generator, PackOutput pOutput) {
        super(pOutput);
        registerAllProcessing(generator,pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        alloyingRecipes(consumer);
        oreMeltingRecipes(consumer);
        metalRecipes(consumer);
        ironBloom(consumer);
    }

    private void alloyingRecipes(Consumer<FinishedRecipe> consumer){

        new HeatedRecipeBuilder()
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.BISMUTH), 20)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.ZINC), 30)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.COPPER), 50)
                .addFluidResult(TFCMetalResourceLocation(Metal.Default.BISMUTH_BRONZE), 100)
                .heatRequirement(600)
                .processingTime(400)
                .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, alloyingRecipeResourceLocation(Metal.Default.BISMUTH_BRONZE));

        new HeatedRecipeBuilder()
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.SILVER), 25)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.GOLD), 25)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.COPPER), 50)
                .addFluidResult(TFCMetalResourceLocation(Metal.Default.BLACK_BRONZE), 100)
                .heatRequirement(600)
                .processingTime(400)
                .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, alloyingRecipeResourceLocation(Metal.Default.BLACK_BRONZE));

        new HeatedRecipeBuilder()
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.COPPER), 90)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.ZINC), 10)
                .addFluidResult(TFCMetalResourceLocation(Metal.Default.BRASS), 100)
                .heatRequirement(600)
                .processingTime(400)
                .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, alloyingRecipeResourceLocation(Metal.Default.BRASS));

        new HeatedRecipeBuilder()
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.COPPER), 90)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.TIN), 10)
                .addFluidResult(TFCMetalResourceLocation(Metal.Default.BRONZE), 100)
                .heatRequirement(600)
                .processingTime(400)
                .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, alloyingRecipeResourceLocation(Metal.Default.BRONZE));

        new HeatedRecipeBuilder()
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.GOLD), 70)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.COPPER), 30)
                .addFluidResult(TFCMetalResourceLocation(Metal.Default.ROSE_GOLD), 100)
                .heatRequirement(600)
                .processingTime(400)
                .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, alloyingRecipeResourceLocation(Metal.Default.ROSE_GOLD));

        new HeatedRecipeBuilder()
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.SILVER), 60)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.COPPER), 40)
                .addFluidResult(TFCMetalResourceLocation(Metal.Default.STERLING_SILVER), 100)
                .heatRequirement(600)
                .processingTime(400)
                .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, alloyingRecipeResourceLocation(Metal.Default.STERLING_SILVER));

        new HeatedRecipeBuilder()
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.BLACK_STEEL), 50)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.STEEL), 20)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.BISMUTH_BRONZE), 15)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.STERLING_SILVER), 15)
                .addFluidResult(TFCMetalResourceLocation(Metal.Default.WEAK_BLUE_STEEL), 100)
                .heatRequirement(600)
                .processingTime(400)
                .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, alloyingRecipeResourceLocation(Metal.Default.WEAK_BLUE_STEEL));

        new HeatedRecipeBuilder()
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.BLACK_STEEL), 50)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.STEEL), 20)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.BRASS), 15)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.ROSE_GOLD), 15)
                .addFluidResult(TFCMetalResourceLocation(Metal.Default.WEAK_RED_STEEL), 100)
                .heatRequirement(600)
                .processingTime(400)
                .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, alloyingRecipeResourceLocation(Metal.Default.WEAK_RED_STEEL));

        new HeatedRecipeBuilder()
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.STEEL), 50)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.NICKEL), 25)
                .addFluidIngredient(TFCMetalResourceLocation(Metal.Default.BLACK_BRONZE), 25)
                .addFluidResult(TFCMetalResourceLocation(Metal.Default.WEAK_STEEL), 100)
                .heatRequirement(600)
                .processingTime(400)
                .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, alloyingRecipeResourceLocation(Metal.Default.WEAK_STEEL));

    }

    private void oreMeltingRecipes(Consumer<FinishedRecipe> consumer){
        //Ore melting recipes
        DataGenStaticData.ORE_REGISTRY.forEach(ore -> {
            new HeatedRecipeBuilder()
                   .addItemIngredient(TFCOreResourceLocation("poor_"+ore.oreId()),ore.meltTemperature(),3000)
                   .addFluidResult(TFCMetalResourceLocation(ore.metalId()),15)
                   .heatRequirement(ore.meltTemperature())
                   .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, oreMeltingRecipeResourceLocation("poor_"+ore.oreId()));

            new HeatedRecipeBuilder()
                    .addItemIngredient(TFCOreResourceLocation("normal_"+ore.oreId()),ore.meltTemperature(),3000)
                    .addFluidResult(TFCMetalResourceLocation(ore.metalId()),25)
                    .heatRequirement(ore.meltTemperature())
                    .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, oreMeltingRecipeResourceLocation("normal_"+ore.oreId()));

            new HeatedRecipeBuilder()
                    .addItemIngredient(TFCOreResourceLocation("rich_"+ore.oreId()),ore.meltTemperature(),3000)
                    .addFluidResult(TFCMetalResourceLocation(ore.metalId()),35)
                    .heatRequirement(ore.meltTemperature())
                    .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, oreMeltingRecipeResourceLocation("rich_"+ore.oreId()));

            new HeatedRecipeBuilder()
                    .addItemIngredient(TFCOreResourceLocation("small_"+ore.oreId()),ore.meltTemperature(),3000)
                    .addFluidResult(TFCMetalResourceLocation(ore.metalId()),10)
                    .heatRequirement(ore.meltTemperature())
                    .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, oreMeltingRecipeResourceLocation("small_"+ore.oreId()));

        });
    }

    private void metalRecipes(Consumer<FinishedRecipe> consumer){
        DataGenStaticData.METAL_REGISTRY.values().forEach(metal -> {
            //Ingot melting recipes
            new HeatedRecipeBuilder()
                    .addItemIngredient(TFCIngotResourceLocation(metal.id()), metal.getMeltTemperature(),3000)
                    .addFluidResult(TFCMetalResourceLocation(metal.id()),100)
                    .heatRequirement(metal.getMeltTemperature())
                    .save(consumer, AllHeatedRecipeTypes.HEATED_MIXING, WoodenCog.asResource("heated_mixing/ingot_to_liquid_"+metal.id()));
            //Sheet recipes
            new HeatedRecipeBuilder()
                    .addItemIngredient(TFCDoubleIngotResourceLocation(metal.id()), metal.getForginTemperature(),3000)
                    .addItemResult(TFCSheetIngotResourceLocation(metal.id()),1,0,0,true)
                    .save(consumer, AllHeatedRecipeTypes.HEATED_PRESSING, WoodenCog.asResource("heated_pressing/sheet_"+metal.id()));
            if(metal.hasDoubleIngot()){
                new HeatedRecipeBuilder()
                        .addItemIngredient(TFCIngotResourceLocation(metal.id()), metal.getWeldingTemperature(),3000)
                        .addItemIngredient(TFCIngotResourceLocation(metal.id()), metal.getWeldingTemperature(),3000)
                        .addItemIngredient(ResourceLocation.tryBuild("tfc","powder/flux"), 0,3000)
                        .addItemResult(TFCDoubleIngotResourceLocation(metal.id()),1,0,0,true)
                        .save(consumer, AllHeatedRecipeTypes.HEATED_COMPACTING, WoodenCog.asResource("heated_compacting/double_"+metal.id()));
            }
        });
    }

    private void ironBloom(Consumer<FinishedRecipe> consumer){
        new HeatedRecipeBuilder()
                .addItemIngredient(ResourceLocation.tryBuild("tfc","raw_iron_bloom"), Heat.ORANGE.getMin(),3000)
                .addItemResult(ResourceLocation.tryBuild("tfc","refined_iron_bloom"),1,0,0,true)
                .save(consumer, AllHeatedRecipeTypes.HEATED_PRESSING, WoodenCog.asResource("heated_pressing/refined_iron_bloom"));

        new HeatedRecipeBuilder()
                .addItemIngredient(ResourceLocation.tryBuild("tfc","refined_iron_bloom"), Heat.ORANGE.getMin(),3000)
                .addItemResult(TFCIngotResourceLocation("wrought_iron"),1,0,0,true)
                .save(consumer, AllHeatedRecipeTypes.HEATED_PRESSING, WoodenCog.asResource("heated_pressing/wrought_iron"));
    }

    private static ResourceLocation dyeRecipeResourceLocation(DyeColor dyeColor) {
        return WoodenCog.asResource("heated_mixing/dyeing_"+dyeColor.getSerializedName());
    }

    private static ResourceLocation alloyingRecipeResourceLocation(Metal.Default metalEnum) {
        return WoodenCog.asResource("heated_mixing/alloying_"+metalEnum.getSerializedName());
    }

    private static ResourceLocation oreMeltingRecipeResourceLocation(String oreId) {
        return WoodenCog.asResource("heated_mixing/"+oreId+"_to_liquid");
    }

    private static ResourceLocation TFCMetalResourceLocation(Metal.Default metalEnum) {
        return ResourceLocation.tryBuild("tfc","metal/"+metalEnum.getSerializedName());
    }

    private static ResourceLocation TFCMetalResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/"+metalId);
    }

    private static ResourceLocation TFCIngotResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/ingot/"+metalId);
    }

    private static ResourceLocation TFCDoubleIngotResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/double_ingot/"+metalId);
    }

    private static ResourceLocation TFCSheetIngotResourceLocation(String metalId) {
        return ResourceLocation.tryBuild("tfc","metal/sheet/"+metalId);
    }

    private static ResourceLocation TFCOreResourceLocation(String oreId) {
        return ResourceLocation.tryBuild("tfc","ore/"+oreId);
    }

    static final List<ProcessingRecipeGen> GENERATORS = new ArrayList<>();

    //Create processing recipes
    public static void registerAllProcessing(DataGenerator gen, PackOutput output) {

        GENERATORS.add(new WoodenCogCompactingRecipeGen(output));
        GENERATORS.add(new WoodenCogCrushingRecipeGen(output));
        GENERATORS.add(new WoodenCogMixingRecipeGen(output));

        gen.addProvider(true, new DataProvider() {

            @Override
            public String getName() {
                return "Create's Processing Recipes";
            }

            @Override
            public CompletableFuture<?> run(CachedOutput dc) {
                return CompletableFuture.allOf(GENERATORS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }
}
