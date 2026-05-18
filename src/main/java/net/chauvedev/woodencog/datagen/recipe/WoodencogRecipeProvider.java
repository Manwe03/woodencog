package net.chauvedev.woodencog.datagen.recipe;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.datagen.DataGenStaticData;
import net.chauvedev.woodencog.recipes.heatedRecipes.*;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.FoodIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.HeatedIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.*;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedCompactingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedMixingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.chauvedev.woodencog.utils.CogUtil;
import net.chauvedev.woodencog.utils.ItemAccess;
import net.chauvedev.woodencog.utils.CogFluidUtil;
import net.chauvedev.woodencog.utils.ModTags;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ingredients.HeatIngredient;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WoodencogRecipeProvider extends RecipeProvider {

    public WoodencogRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        this.alloyingRecipes(recipeOutput);
        this.oreMeltingRecipes(recipeOutput);
        this.metalRecipes(recipeOutput);
        this.ironBloom(recipeOutput);
        this.sandwiches(recipeOutput);
        this.foods(recipeOutput);
        this.jams(recipeOutput);
    }

    private void alloyingRecipes(RecipeOutput recipeOutput){
        // BISMUTH BRONZE (ya convertido)
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.BISMUTH, 20),
                        CogFluidUtil.ingredient(Metal.ZINC, 30),
                        CogFluidUtil.ingredient(Metal.COPPER, 50))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.BISMUTH_BRONZE, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, alloyingRecipeResourceLocation(Metal.BISMUTH_BRONZE));

        // BLACK BRONZE
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.SILVER, 25),
                        CogFluidUtil.ingredient(Metal.GOLD, 25),
                        CogFluidUtil.ingredient(Metal.COPPER, 50))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.BLACK_BRONZE, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, alloyingRecipeResourceLocation(Metal.BLACK_BRONZE));

        // BRASS
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.COPPER, 90),
                        CogFluidUtil.ingredient(Metal.ZINC, 10))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.BRASS, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, alloyingRecipeResourceLocation(Metal.BRASS));

        // BRONZE
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.COPPER, 90),
                        CogFluidUtil.ingredient(Metal.TIN, 10))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.BRONZE, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, alloyingRecipeResourceLocation(Metal.BRONZE));

        // ROSE GOLD
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.GOLD, 70),
                        CogFluidUtil.ingredient(Metal.COPPER, 30))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.ROSE_GOLD, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, alloyingRecipeResourceLocation(Metal.ROSE_GOLD));

        // STERLING SILVER
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.SILVER, 60),
                        CogFluidUtil.ingredient(Metal.COPPER, 40))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.STERLING_SILVER, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, alloyingRecipeResourceLocation(Metal.STERLING_SILVER));

        // WEAK BLUE STEEL
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.BLACK_STEEL, 50),
                        CogFluidUtil.ingredient(Metal.STEEL, 20),
                        CogFluidUtil.ingredient(Metal.BISMUTH_BRONZE, 15),
                        CogFluidUtil.ingredient(Metal.STERLING_SILVER, 15))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.WEAK_BLUE_STEEL,100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, alloyingRecipeResourceLocation(Metal.WEAK_BLUE_STEEL));

        // WEAK RED STEEL
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.BLACK_STEEL, 50),
                        CogFluidUtil.ingredient(Metal.STEEL, 20),
                        CogFluidUtil.ingredient(Metal.BRASS, 15),
                        CogFluidUtil.ingredient(Metal.ROSE_GOLD, 15))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.WEAK_STEEL,100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, alloyingRecipeResourceLocation(Metal.WEAK_RED_STEEL));

        // WEAK STEEL
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.STEEL, 50),
                        CogFluidUtil.ingredient(Metal.NICKEL, 25),
                        CogFluidUtil.ingredient(Metal.BLACK_BRONZE, 25))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.WEAK_STEEL,100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, alloyingRecipeResourceLocation(Metal.WEAK_STEEL));
    }

    private void oreMeltingRecipes(RecipeOutput recipeOutput){

        DataGenStaticData.ORE_REGISTRY.forEach(ore -> {
            Item smallOre = BuiltInRegistries.ITEM.get(TFCOreResourceLocation("small_"+ore.oreId()));
            Item poorOre = BuiltInRegistries.ITEM.get(TFCOreResourceLocation("poor_"+ore.oreId()));
            Item normalOre = BuiltInRegistries.ITEM.get(TFCOreResourceLocation("normal_"+ore.oreId()));
            Item richOre = BuiltInRegistries.ITEM.get(TFCOreResourceLocation("rich_"+ore.oreId()));

            Fluid metal = BuiltInRegistries.FLUID.get(TFCMetalResourceLocation(ore.metalId()));

            if(CogUtil.logConditional(metal == null,this.getClass(),"DataGen, metal is null can't generate melting recipes")) return;

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(smallOre),ore.meltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(metal,10))
                    .requiresHeat(WoodenCogHeatCondition.of(ore.meltTemperature()))
                    .build(recipeOutput, oreMeltingRecipeResourceLocation("small_"+ore.oreId()));

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(poorOre),ore.meltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(metal,15))
                    .requiresHeat(WoodenCogHeatCondition.of(ore.meltTemperature()))
                    .build(recipeOutput, oreMeltingRecipeResourceLocation("poor_"+ore.oreId()));

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(normalOre),ore.meltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(metal,25))
                    .requiresHeat(WoodenCogHeatCondition.of(ore.meltTemperature()))
                    .build(recipeOutput, oreMeltingRecipeResourceLocation("normal_"+ore.oreId()));

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(richOre),ore.meltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(metal,35))
                    .requiresHeat(WoodenCogHeatCondition.of(ore.meltTemperature()))
                    .build(recipeOutput, oreMeltingRecipeResourceLocation("rich_"+ore.oreId()));

        });
    }

    private void metalRecipes(RecipeOutput recipeOutput){
        DataGenStaticData.METAL_REGISTRY.values().forEach(metal -> {
            Item ingot = BuiltInRegistries.ITEM.get(TFCIngotResourceLocation(metal.id()));
            Fluid fluidMetal = BuiltInRegistries.FLUID.get(TFCMetalResourceLocation(metal.id()));
            Item sheet = BuiltInRegistries.ITEM.get(TFCSheetIngotResourceLocation(metal.id()));
            Item doubleIngot = BuiltInRegistries.ITEM.get(TFCDoubleIngotResourceLocation(metal.id()));

            //Ingot melting recipes
            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(ingot),metal.getMeltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(fluidMetal,100))
                    .requiresHeat(WoodenCogHeatCondition.of(metal.getMeltTemperature()))
                    .build(recipeOutput, WoodenCog.asResource("ingot_to_liquid_"+metal.id()));

            if(metal.hasDoubleIngot()){
                //Sheet recipes
                new HeatedProcessingRecipeBuilder<>(HeatedPressingRecipe::new)
                        .withItemIngredients(HeatedIngredient.of(Ingredient.of(doubleIngot), metal.getForginTemperature(),3000))
                        .withItemOutputs(HeatedProcessingOutput.of(sheet,1,1,0,true,0))
                        .build(recipeOutput, WoodenCog.asResource("sheet_"+metal.id()));

                //Double Ingots recipes
                new HeatedProcessingRecipeBuilder<>(HeatedCompactingRecipe::new)
                        .withItemIngredients(
                                HeatedIngredient.of(Ingredient.of(ingot),metal.getWeldingTemperature(),3000),
                                HeatedIngredient.of(Ingredient.of(ingot),metal.getWeldingTemperature(),3000),
                                HeatedIngredient.of(Ingredient.of(ItemAccess.flux),0,3000))
                        .withItemOutputs(HeatedProcessingOutput.of(doubleIngot,1,1,0,true,0))
                        .build(recipeOutput, WoodenCog.asResource("double_"+metal.id()));
            }
        });
    }

    private void ironBloom(RecipeOutput recipeOutput){

        new HeatedProcessingRecipeBuilder<>(HeatedPressingRecipe::new)
                .withItemIngredients(HeatedIngredient.of(Ingredient.of(ItemAccess.rawIron), (int) Heat.ORANGE.getMin(),3000))
                .withItemOutputs(HeatedProcessingOutput.of(ItemAccess.refinedIron,1,1,0,true,0))
                .build(recipeOutput,WoodenCog.asResource("refined_iron_bloom"));

        new HeatedProcessingRecipeBuilder<>(HeatedPressingRecipe::new)
                .withItemIngredients(HeatedIngredient.of(Ingredient.of(ItemAccess.refinedIron), (int) Heat.ORANGE.getMin(),3000))
                .withItemOutputs(HeatedProcessingOutput.of(ItemAccess.wroughtIron,1,1,0,true,0))
                .build(recipeOutput, WoodenCog.asResource("wrought_iron"));
    }

    private void sandwiches(RecipeOutput recipeOutput){

        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.BARLEY_BREAD).get(), TFCItems.FOOD.get(Food.BARLEY_BREAD_SANDWICH).get(), ModTags.Compat.USABLE_IN_SANDWICH, ModTags.Compat.USABLE_IN_SANDWICH);
        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.BARLEY_BREAD).get(), TFCItems.FOOD.get(Food.BARLEY_BREAD_JAM_SANDWICH).get(), ModTags.Compat.USABLE_IN_JAM_SANDWICH, ModTags.Compat.PRESERVES);

        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.MAIZE_BREAD).get(), TFCItems.FOOD.get(Food.MAIZE_BREAD_SANDWICH).get(), ModTags.Compat.USABLE_IN_SANDWICH, ModTags.Compat.USABLE_IN_SANDWICH);
        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.MAIZE_BREAD).get(), TFCItems.FOOD.get(Food.MAIZE_BREAD_JAM_SANDWICH).get(), ModTags.Compat.USABLE_IN_JAM_SANDWICH, ModTags.Compat.PRESERVES);

        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.OAT_BREAD).get(), TFCItems.FOOD.get(Food.OAT_BREAD_SANDWICH).get(), ModTags.Compat.USABLE_IN_SANDWICH, ModTags.Compat.USABLE_IN_SANDWICH);
        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.OAT_BREAD).get(), TFCItems.FOOD.get(Food.OAT_BREAD_JAM_SANDWICH).get(), ModTags.Compat.USABLE_IN_JAM_SANDWICH, ModTags.Compat.PRESERVES);

        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.RICE_BREAD).get(), TFCItems.FOOD.get(Food.RICE_BREAD_SANDWICH).get(), ModTags.Compat.USABLE_IN_SANDWICH, ModTags.Compat.USABLE_IN_SANDWICH);
        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.RICE_BREAD).get(), TFCItems.FOOD.get(Food.RICE_BREAD_JAM_SANDWICH).get(), ModTags.Compat.USABLE_IN_JAM_SANDWICH, ModTags.Compat.PRESERVES);

        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.RYE_BREAD).get(), TFCItems.FOOD.get(Food.RYE_BREAD_SANDWICH).get(), ModTags.Compat.USABLE_IN_SANDWICH, ModTags.Compat.USABLE_IN_SANDWICH);
        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.RYE_BREAD).get(), TFCItems.FOOD.get(Food.RYE_BREAD_JAM_SANDWICH).get(), ModTags.Compat.USABLE_IN_JAM_SANDWICH, ModTags.Compat.PRESERVES);

        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.WHEAT_BREAD).get(), TFCItems.FOOD.get(Food.WHEAT_BREAD_SANDWICH).get(), ModTags.Compat.USABLE_IN_SANDWICH, ModTags.Compat.USABLE_IN_SANDWICH);
        buildSandwich(recipeOutput, TFCItems.FOOD.get(Food.WHEAT_BREAD).get(), TFCItems.FOOD.get(Food.WHEAT_BREAD_JAM_SANDWICH).get(), ModTags.Compat.USABLE_IN_JAM_SANDWICH, ModTags.Compat.PRESERVES);
    }

    private static void buildSandwich(RecipeOutput recipeOutput, Item bread, Item sandwich, TagKey<Item> tag1, TagKey<Item> tag2){
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
                .build(recipeOutput, WoodenCog.asResource(sandwich.toString()));
    }

    private void foods(RecipeOutput recipeOutput){

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(new ItemStack(ItemAccess.egg))))
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(
                        FoodProcessingOutput.Builder.create().withItem(ItemAccess.boiledEgg, 1).build())
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .build(recipeOutput, WoodenCog.asResource(ItemAccess.boiledEgg.toString()));

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(new ItemStack(ItemAccess.riceGrain))))
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(
                        FoodProcessingOutput.Builder.create().withItem(ItemAccess.cookedRice, 1).build())
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .build(recipeOutput, WoodenCog.asResource(ItemAccess.cookedRice.toString()));

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
                .build(recipeOutput, WoodenCog.asResource("food/salads"));

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
                .withFluidIngredients(CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(new SoupProcessingOutput(Items.BOWL,3,1))
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .duration(500)
                .build(recipeOutput, WoodenCog.asResource("food/soups"));

        //Ceramic bowl
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(ItemAccess.ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)),
                        FoodIngredient.of(Ingredient.of(usableInSalad)))
                .withItemOutputs(new SaladProcessingOutput(ItemAccess.ceramicBowl, 1,1))
                .duration(500)
                .build(recipeOutput, WoodenCog.asResource("food/salads_ceramic"));

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(ItemAccess.ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(ItemAccess.ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(ItemAccess.ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)),
                        FoodIngredient.of(Ingredient.of(usableInSoup)))
                .withFluidIngredients(CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(new SoupProcessingOutput(ItemAccess.ceramicBowl,3,1))
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .duration(500)
                .build(recipeOutput, WoodenCog.asResource("food/soups_ceramic"));
    }

    private void jams(RecipeOutput recipeOutput){

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
                    .withFluidIngredients(CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                    .withItemOutputs(output2)
                    .requiresHeat(WoodenCogHeatCondition.of(104))
                    .duration(4000)
                    .build(recipeOutput, WoodenCog.asResource("food/jams/"+food.name().toLowerCase()+"_2"));

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
                    .withFluidIngredients(CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                    .withItemOutputs(output3)
                    .requiresHeat(WoodenCogHeatCondition.of(104))
                    .duration(4000)
                    .build(recipeOutput, WoodenCog.asResource("food/jams/"+food.name().toLowerCase()+"_3"));

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
                    .withFluidIngredients(CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                    .withItemOutputs(output4)
                    .requiresHeat(WoodenCogHeatCondition.of(104))
                    .duration(4000)
                    .build(recipeOutput, WoodenCog.asResource("food/jams/"+food.name().toLowerCase()+"_4"));
        });
    }

    private static ResourceLocation dyeRecipeResourceLocation(DyeColor dyeColor) {
        return WoodenCog.asResource("heated_mixing/dyeing_"+dyeColor.getSerializedName());
    }

    private static ResourceLocation alloyingRecipeResourceLocation(Metal metalEnum) {
        return WoodenCog.asResource("alloying_"+metalEnum.getSerializedName());
    }

    private static ResourceLocation oreMeltingRecipeResourceLocation(String oreId) {
        return WoodenCog.asResource(oreId+"_to_liquid");
    }

    private static ResourceLocation TFCMetalResourceLocation(Metal metalEnum) {
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
    public static void registerAllProcessing(DataGenerator gen, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        GENERATORS.clear();

        GENERATORS.add(new WoodenCogCompactingRecipeGen(output, registries));
        GENERATORS.add(new WoodenCogCrushingRecipeGen(output, registries));
        GENERATORS.add(new WoodenCogMixingRecipeGen(output, registries));

        gen.addProvider(true, new DataProvider() {

            @Override
            public String getName() {
                return "Woodencog's Processing Recipes";
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
