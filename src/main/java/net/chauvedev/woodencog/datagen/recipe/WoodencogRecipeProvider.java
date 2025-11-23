package net.chauvedev.woodencog.datagen.recipe;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.datagen.DataGenStaticData;
import net.chauvedev.woodencog.recipes.heatedRecipes.*;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.FoodIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.HeatedIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.*;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedCompactingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedMixingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.dries007.tfc.common.capabilities.food.FoodData;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

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
        this.alloyingRecipes(consumer);
        this.oreMeltingRecipes(consumer);
        this.metalRecipes(consumer);
        this.ironBloom(consumer);
        this.sandwiches(consumer);
        this.foods(consumer);
        this.jams(consumer);
    }

    private void alloyingRecipes(Consumer<FinishedRecipe> consumer){

        Fluid copper = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.COPPER));
        Fluid bismuth = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.BISMUTH));
        Fluid zinc = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.ZINC));
        Fluid bismuthBronze = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.BISMUTH_BRONZE));
        Fluid silver = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.SILVER));
        Fluid gold = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.GOLD));
        Fluid blackBronze = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.BLACK_BRONZE));
        Fluid brass = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.BRASS));
        Fluid tin = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.TIN));
        Fluid bronze = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.BRONZE));
        Fluid roseGold = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.ROSE_GOLD));
        Fluid sterlingSilver = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.STERLING_SILVER));
        Fluid blackSteel = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.BLACK_STEEL));
        Fluid steel = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.STEEL));
        Fluid weakBlueSteel = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.WEAK_BLUE_STEEL));
        Fluid weakRedSteel = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.WEAK_RED_STEEL));
        Fluid nickel = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.NICKEL));
        Fluid weakSteel = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(Metal.Default.WEAK_STEEL));

        // BISMUTH BRONZE (ya convertido)
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        FluidIngredient.fromFluid(bismuth, 20),
                        FluidIngredient.fromFluid(zinc, 30),
                        FluidIngredient.fromFluid(copper, 50))
                .withFluidOutputs(new FluidStack(bismuthBronze, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(consumer, alloyingRecipeResourceLocation(Metal.Default.BISMUTH_BRONZE));

        // BLACK BRONZE
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        FluidIngredient.fromFluid(silver, 25),
                        FluidIngredient.fromFluid(gold, 25),
                        FluidIngredient.fromFluid(copper, 50))
                .withFluidOutputs(new FluidStack(blackBronze, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(consumer, alloyingRecipeResourceLocation(Metal.Default.BLACK_BRONZE));

        // BRASS
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        FluidIngredient.fromFluid(copper, 90),
                        FluidIngredient.fromFluid(zinc, 10))
                .withFluidOutputs(
                        new FluidStack(brass, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(consumer, alloyingRecipeResourceLocation(Metal.Default.BRASS));

        // BRONZE
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        FluidIngredient.fromFluid(copper, 90),
                        FluidIngredient.fromFluid(tin, 10))
                .withFluidOutputs(
                        new FluidStack(bronze, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(consumer, alloyingRecipeResourceLocation(Metal.Default.BRONZE));

        // ROSE GOLD
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        FluidIngredient.fromFluid(gold, 70),
                        FluidIngredient.fromFluid(copper, 30))
                .withFluidOutputs(new FluidStack(roseGold, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(consumer, alloyingRecipeResourceLocation(Metal.Default.ROSE_GOLD));

        // STERLING SILVER
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        FluidIngredient.fromFluid(silver, 60),
                        FluidIngredient.fromFluid(copper, 40))
                .withFluidOutputs(new FluidStack(sterlingSilver, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(consumer, alloyingRecipeResourceLocation(Metal.Default.STERLING_SILVER));

        // WEAK BLUE STEEL
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        FluidIngredient.fromFluid(blackSteel, 50),
                        FluidIngredient.fromFluid(steel, 20),
                        FluidIngredient.fromFluid(bismuthBronze, 15),
                        FluidIngredient.fromFluid(sterlingSilver, 15))
                .withFluidOutputs(
                        new FluidStack(weakBlueSteel, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(consumer, alloyingRecipeResourceLocation(Metal.Default.WEAK_BLUE_STEEL));

        // WEAK RED STEEL
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        FluidIngredient.fromFluid(blackSteel, 50),
                        FluidIngredient.fromFluid(steel, 20),
                        FluidIngredient.fromFluid(brass, 15),
                        FluidIngredient.fromFluid(roseGold, 15))
                .withFluidOutputs(
                        new FluidStack(weakRedSteel, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(consumer, alloyingRecipeResourceLocation(Metal.Default.WEAK_RED_STEEL));

        // WEAK STEEL
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        FluidIngredient.fromFluid(steel, 50),
                        FluidIngredient.fromFluid(nickel, 25),
                        FluidIngredient.fromFluid(blackBronze, 25))
                .withFluidOutputs(
                        new FluidStack(weakSteel, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(consumer, alloyingRecipeResourceLocation(Metal.Default.WEAK_STEEL));
    }

    private void oreMeltingRecipes(Consumer<FinishedRecipe> consumer){

        DataGenStaticData.ORE_REGISTRY.forEach(ore -> {
            Item smallOre = ForgeRegistries.ITEMS.getValue(TFCOreResourceLocation("small_"+ore.oreId()));
            Item poorOre = ForgeRegistries.ITEMS.getValue(TFCOreResourceLocation("poor_"+ore.oreId()));
            Item normalOre = ForgeRegistries.ITEMS.getValue(TFCOreResourceLocation("normal_"+ore.oreId()));
            Item richOre = ForgeRegistries.ITEMS.getValue(TFCOreResourceLocation("rich_"+ore.oreId()));

            Fluid metal = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(ore.metalId()));

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(smallOre),ore.meltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(metal,10))
                    .requiresHeat(WoodenCogHeatCondition.of(ore.meltTemperature()))
                    .build(consumer, oreMeltingRecipeResourceLocation("small_"+ore.oreId()));

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(poorOre),ore.meltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(metal,15))
                    .requiresHeat(WoodenCogHeatCondition.of(ore.meltTemperature()))
                    .build(consumer, oreMeltingRecipeResourceLocation("poor_"+ore.oreId()));

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(normalOre),ore.meltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(metal,25))
                    .requiresHeat(WoodenCogHeatCondition.of(ore.meltTemperature()))
                    .build(consumer, oreMeltingRecipeResourceLocation("normal_"+ore.oreId()));

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(richOre),ore.meltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(metal,35))
                    .requiresHeat(WoodenCogHeatCondition.of(ore.meltTemperature()))
                    .build(consumer, oreMeltingRecipeResourceLocation("rich_"+ore.oreId()));
        });
    }

    private void metalRecipes(Consumer<FinishedRecipe> consumer){
        DataGenStaticData.METAL_REGISTRY.values().forEach(metal -> {
            Item ingot = ForgeRegistries.ITEMS.getValue(TFCIngotResourceLocation(metal.id()));
            Fluid fluidMetal = ForgeRegistries.FLUIDS.getValue(TFCMetalResourceLocation(metal.id()));
            Item sheet = ForgeRegistries.ITEMS.getValue(TFCSheetIngotResourceLocation(metal.id()));
            Item doubleIngot = ForgeRegistries.ITEMS.getValue(TFCDoubleIngotResourceLocation(metal.id()));
            Item flux = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("tfc","powder/flux"));

            //Ingot melting recipes
            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(ingot),metal.getMeltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(fluidMetal,100))
                    .requiresHeat(WoodenCogHeatCondition.of(metal.getMeltTemperature()))
                    .build(consumer, WoodenCog.asResource("ingot_to_liquid_"+metal.id()));

            if(metal.hasDoubleIngot()){
                //Sheet recipes
                new HeatedProcessingRecipeBuilder<>(HeatedPressingRecipe::new)
                        .withItemIngredients(HeatedIngredient.of(Ingredient.of(doubleIngot),metal.getMeltTemperature(),3000))
                        .withItemOutputs(HeatedProcessingOutput.of(sheet,1,1,0,true,0))
                        .build(consumer, WoodenCog.asResource("sheet_"+metal.id()));

                //Double Ingots recipes
                new HeatedProcessingRecipeBuilder<>(HeatedCompactingRecipe::new)
                        .withItemIngredients(
                                HeatedIngredient.of(Ingredient.of(ingot),metal.getWeldingTemperature(),3000),
                                HeatedIngredient.of(Ingredient.of(ingot),metal.getWeldingTemperature(),3000),
                                HeatedIngredient.of(Ingredient.of(flux),0,3000))
                        .withItemOutputs(HeatedProcessingOutput.of(doubleIngot,1,1,0,true,0))
                        .build(consumer, WoodenCog.asResource("double_"+metal.id()));
            }
        });
    }

    private void ironBloom(Consumer<FinishedRecipe> consumer){
        Item rawIron = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("tfc","raw_iron_bloom"));
        Item refinedIron = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("tfc","refined_iron_bloom"));
        Item wroughtIron = ForgeRegistries.ITEMS.getValue(TFCIngotResourceLocation("wrought_iron"));

        new HeatedProcessingRecipeBuilder<>(HeatedPressingRecipe::new)
                .withItemIngredients(HeatedIngredient.of(Ingredient.of(rawIron), (int) Heat.ORANGE.getMin(),3000))
                .withItemOutputs(HeatedProcessingOutput.of(refinedIron,1,1,0,true,0))
                .build(consumer,WoodenCog.asResource("refined_iron_bloom"));

        new HeatedProcessingRecipeBuilder<>(HeatedPressingRecipe::new)
                .withItemIngredients(HeatedIngredient.of(Ingredient.of(refinedIron), (int) Heat.ORANGE.getMin(),3000))
                .withItemOutputs(HeatedProcessingOutput.of(wroughtIron,1,1,0,true,0))
                .build(consumer, WoodenCog.asResource("wrought_iron"));
    }

    private void sandwiches(Consumer<FinishedRecipe> consumer){

        TagKey<Item> usableInSandwich = TagKey.create(Registries.ITEM, ResourceLocation.tryBuild("tfc", "foods/usable_in_sandwich"));
        TagKey<Item> usableInJamSandwich = TagKey.create(Registries.ITEM, ResourceLocation.tryBuild("tfc", "foods/usable_in_jam_sandwich"));
        TagKey<Item> preserves = TagKey.create(Registries.ITEM, ResourceLocation.tryBuild("tfc", "foods/preserves"));

        buildSandwich(consumer, TFCItems.FOOD.get(Food.BARLEY_BREAD).get(), TFCItems.FOOD.get(Food.BARLEY_BREAD_SANDWICH).get(), usableInSandwich, usableInSandwich);
        buildSandwich(consumer, TFCItems.FOOD.get(Food.BARLEY_BREAD).get(), TFCItems.FOOD.get(Food.BARLEY_BREAD_JAM_SANDWICH).get(), usableInJamSandwich, preserves);

        buildSandwich(consumer, TFCItems.FOOD.get(Food.MAIZE_BREAD).get(), TFCItems.FOOD.get(Food.MAIZE_BREAD_SANDWICH).get(), usableInSandwich, usableInSandwich);
        buildSandwich(consumer, TFCItems.FOOD.get(Food.MAIZE_BREAD).get(), TFCItems.FOOD.get(Food.MAIZE_BREAD_JAM_SANDWICH).get(), usableInJamSandwich, preserves);

        buildSandwich(consumer, TFCItems.FOOD.get(Food.OAT_BREAD).get(), TFCItems.FOOD.get(Food.OAT_BREAD_SANDWICH).get(), usableInSandwich, usableInSandwich);
        buildSandwich(consumer, TFCItems.FOOD.get(Food.OAT_BREAD).get(), TFCItems.FOOD.get(Food.OAT_BREAD_JAM_SANDWICH).get(), usableInJamSandwich, preserves);

        buildSandwich(consumer, TFCItems.FOOD.get(Food.RICE_BREAD).get(), TFCItems.FOOD.get(Food.RICE_BREAD_SANDWICH).get(), usableInSandwich, usableInSandwich);
        buildSandwich(consumer, TFCItems.FOOD.get(Food.RICE_BREAD).get(), TFCItems.FOOD.get(Food.RICE_BREAD_JAM_SANDWICH).get(), usableInJamSandwich, preserves);

        buildSandwich(consumer, TFCItems.FOOD.get(Food.RYE_BREAD).get(), TFCItems.FOOD.get(Food.RYE_BREAD_SANDWICH).get(), usableInSandwich, usableInSandwich);
        buildSandwich(consumer, TFCItems.FOOD.get(Food.RYE_BREAD).get(), TFCItems.FOOD.get(Food.RYE_BREAD_JAM_SANDWICH).get(), usableInJamSandwich, preserves);

        buildSandwich(consumer, TFCItems.FOOD.get(Food.WHEAT_BREAD).get(), TFCItems.FOOD.get(Food.WHEAT_BREAD_SANDWICH).get(), usableInSandwich, usableInSandwich);
        buildSandwich(consumer, TFCItems.FOOD.get(Food.WHEAT_BREAD).get(), TFCItems.FOOD.get(Food.WHEAT_BREAD_JAM_SANDWICH).get(), usableInJamSandwich, preserves);
    }

    private static void buildSandwich(Consumer<FinishedRecipe> consumer, Item bread, Item sandwich, TagKey<Item> tag1, TagKey<Item> tag2){
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(new ItemStack(bread))),
                        FoodIngredient.of(Ingredient.of(new ItemStack(bread))),
                        FoodIngredient.of(Ingredient.of(tag1)),
                        FoodIngredient.of(Ingredient.of(tag1)),
                        FoodIngredient.of(Ingredient.of(tag2)))
                .withItemOutputs(
                        FoodProcessingOutput.Builder.create()
                                .withItem(sandwich, 2)
                                .withFoodData(4,0.5F,1,4.5F)
                                .withEmptyNutrients()
                                .withPortions(List.of(
                                        WoodenCogFoodPortion.flat(0.8F),
                                        WoodenCogFoodPortion.flat(0.5F)))
                                .build())
                .build(consumer, WoodenCog.asResource(sandwich.toString()));
    }

    private void foods(Consumer<FinishedRecipe> consumer){
        Item egg = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("minecraft","egg"));
        Item boiledEgg = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("tfc","food/boiled_egg"));

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(new ItemStack(egg))))
                .withFluidIngredients(
                        FluidIngredient.fromFluid(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(
                        FoodProcessingOutput.Builder.create().withItem(boiledEgg, 1).build())
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .build(consumer, WoodenCog.asResource(boiledEgg.toString()));

        Item riceGrain = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("tfc","food/rice_grain"));
        Item cookedRice = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("tfc","food/cooked_rice"));

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(new ItemStack(riceGrain))))
                .withFluidIngredients(
                        FluidIngredient.fromFluid(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(
                        FoodProcessingOutput.Builder.create().withItem(cookedRice, 1).build())
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .build(consumer, WoodenCog.asResource(cookedRice.toString()));

        //Wood bowl
        TagKey<Item> usableInSalad = TagKey.create(Registries.ITEM, ResourceLocation.tryBuild("tfc", "foods/usable_in_salad"));
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(Items.BOWL)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)))
                .withItemOutputs(new SaladProcessingOutput(Items.BOWL,1,1))
                .duration(500)
                .build(consumer, WoodenCog.asResource("food/salads"));

        TagKey<Item> usableInSoup = TagKey.create(Registries.ITEM, ResourceLocation.tryBuild("tfc", "foods/usable_in_soup"));
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(Items.BOWL)),
                        FoodIngredient.of(Ingredient.of(Items.BOWL)),
                        FoodIngredient.of(Ingredient.of(Items.BOWL)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)))
                .withFluidIngredients(FluidIngredient.fromFluid(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(new SoupProcessingOutput(Items.BOWL,3,1))
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .duration(500)
                .build(consumer, WoodenCog.asResource("food/soups"));

        //Ceramic bowl
        Item ceramicBowl = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild("tfc","ceramic/bowl"));

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)))
                .withItemOutputs(new SaladProcessingOutput(ceramicBowl, 1,1))
                .duration(500)
                .build(consumer, WoodenCog.asResource("food/salads_ceramic"));

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)))
                .withFluidIngredients(FluidIngredient.fromFluid(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(new SoupProcessingOutput(ceramicBowl,3,1))
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .duration(500)
                .build(consumer, WoodenCog.asResource("food/soups_ceramic"));
    }

    private void jams(Consumer<FinishedRecipe> consumer){

        TFCItems.FRUIT_PRESERVES.forEach((food, itemRegistryObject) -> {
            Item jam = itemRegistryObject.get();
            Item fruit = TFCItems.FOOD.get(food).get();

            FoodProcessingOutput output2 = FoodProcessingOutput.Builder.create().withItem(jam, 2)
                    .withEmptyNutrients().withPortions(List.of(WoodenCogFoodPortion.flat(0.8F))).build();

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(
                            FoodIngredient.of(Ingredient.of(Items.SUGAR)),
                            FoodIngredient.of(Ingredient.of(TFCItems.EMPTY_JAR_WITH_LID.get())),
                            FoodIngredient.of(Ingredient.of(TFCItems.EMPTY_JAR_WITH_LID.get())),
                            FoodIngredient.of(Ingredient.of(fruit)),
                            FoodIngredient.of(Ingredient.of(fruit)))
                    .withFluidIngredients(FluidIngredient.fromFluid(Fluids.WATER.getFlowing(),100))
                    .withItemOutputs(output2)
                    .requiresHeat(WoodenCogHeatCondition.of(104))
                    .duration(4000)
                    .build(consumer, WoodenCog.asResource("food/jams/"+food.name().toLowerCase()+"_2"));

            FoodProcessingOutput output3 = FoodProcessingOutput.Builder.create().withItem(jam, 3)
                    .withEmptyNutrients().withPortions(List.of(WoodenCogFoodPortion.flat(0.8F))).build();

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(
                            FoodIngredient.of(Ingredient.of(Items.SUGAR)),
                            FoodIngredient.of(Ingredient.of(TFCItems.EMPTY_JAR_WITH_LID.get())),
                            FoodIngredient.of(Ingredient.of(TFCItems.EMPTY_JAR_WITH_LID.get())),
                            FoodIngredient.of(Ingredient.of(TFCItems.EMPTY_JAR_WITH_LID.get())),
                            FoodIngredient.of(Ingredient.of(fruit)),
                            FoodIngredient.of(Ingredient.of(fruit)),
                            FoodIngredient.of(Ingredient.of(fruit)))
                    .withFluidIngredients(FluidIngredient.fromFluid(Fluids.WATER.getFlowing(),100))
                    .withItemOutputs(output3)
                    .requiresHeat(WoodenCogHeatCondition.of(104))
                    .duration(4000)
                    .build(consumer, WoodenCog.asResource("food/jams/"+food.name().toLowerCase()+"_3"));

            FoodProcessingOutput output4 = FoodProcessingOutput.Builder.create().withItem(jam, 4)
                    .withEmptyNutrients().withPortions(List.of(WoodenCogFoodPortion.flat(0.8F))).build();

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(
                            FoodIngredient.of(Ingredient.of(Items.SUGAR)),
                            FoodIngredient.of(Ingredient.of(TFCItems.EMPTY_JAR_WITH_LID.get())),
                            FoodIngredient.of(Ingredient.of(TFCItems.EMPTY_JAR_WITH_LID.get())),
                            FoodIngredient.of(Ingredient.of(TFCItems.EMPTY_JAR_WITH_LID.get())),
                            FoodIngredient.of(Ingredient.of(TFCItems.EMPTY_JAR_WITH_LID.get())),
                            FoodIngredient.of(Ingredient.of(fruit)),
                            FoodIngredient.of(Ingredient.of(fruit)),
                            FoodIngredient.of(Ingredient.of(fruit)),
                            FoodIngredient.of(Ingredient.of(fruit)))
                    .withFluidIngredients(FluidIngredient.fromFluid(Fluids.WATER.getFlowing(),100))
                    .withItemOutputs(output4)
                    .requiresHeat(WoodenCogHeatCondition.of(104))
                    .duration(4000)
                    .build(consumer, WoodenCog.asResource("food/jams/"+food.name().toLowerCase()+"_4"));
        });
    }

    private static ResourceLocation dyeRecipeResourceLocation(DyeColor dyeColor) {
        return WoodenCog.asResource("heated_mixing/dyeing_"+dyeColor.getSerializedName());
    }

    private static ResourceLocation alloyingRecipeResourceLocation(Metal.Default metalEnum) {
        return WoodenCog.asResource("alloying_"+metalEnum.getSerializedName());
    }

    private static ResourceLocation oreMeltingRecipeResourceLocation(String oreId) {
        return WoodenCog.asResource(oreId+"_to_liquid");
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
