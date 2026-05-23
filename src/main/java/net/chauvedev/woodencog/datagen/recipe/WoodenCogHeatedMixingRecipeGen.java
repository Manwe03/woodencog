package net.chauvedev.woodencog.datagen.recipe;

import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.datagen.DataGenStaticData;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.chauvedev.woodencog.recipes.heatedRecipes.WoodenCogFoodPortion;
import net.chauvedev.woodencog.recipes.heatedRecipes.WoodenCogHeatCondition;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.FoodIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.HeatedIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.FoodProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.SaladProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.SoupProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedMixingRecipe;
import net.chauvedev.woodencog.utils.CogFluidUtil;
import net.chauvedev.woodencog.utils.CogUtil;
import net.chauvedev.woodencog.utils.ItemAccess;
import net.chauvedev.woodencog.utils.ModTags;
import net.dries007.tfc.common.items.Food;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

public class WoodenCogHeatedMixingRecipeGen extends WoodenCogProcessinRecipeGen{

    private static final String LOCATION = "heated_mixing/";

    public WoodenCogHeatedMixingRecipeGen(RecipeOutput recipeOutput){
        super(recipeOutput);
        this.ingotMelting(recipeOutput);
        this.oreMeltingRecipes(recipeOutput);
        this.alloyingRecipes(recipeOutput);
        this.sandwiches(recipeOutput);
        this.foods(recipeOutput);
        this.jams(recipeOutput);
    }

    @Override
    protected HeatedProcessingRecipeBuilder<?> builder() {
        return new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new);
    }

    private void ingotMelting(RecipeOutput recipeOutput){
        this.forEachMetal((metal, ingot, doubleIngot, sheet, metalFluid) -> {
            this.builder()
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(ingot),metal.getMeltTemperature(),3000))
                    .withFluidOutputs(new FluidStack(metalFluid,100))
                    .requiresHeat(WoodenCogHeatCondition.of(metal.getMeltTemperature()))
                    .build(recipeOutput, WoodenCog.asResource(LOCATION+"/ingot_to_liquid_"+metal.id()));
        });
    }

    private void oreMeltingRecipes(RecipeOutput recipeOutput){

        forEachGradedOre((ore, oreItem, oreMetalFluid, oreDust, oreDustAmount, metalFluidAmount, heatDefinition) -> {
            if(oreMetalFluid == null) return;

            this.builder()
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(oreItem),heatDefinition.weldingTemperature(),3000))
                    .withFluidOutputs(new FluidStack(oreMetalFluid,metalFluidAmount))
                    .requiresHeat(WoodenCogHeatCondition.of(heatDefinition.meltingTemperature()))
                    .build(recipeOutput, WoodenCog.asWoodencogResource(LOCATION,oreItem,"_to_liquid"));
        });

        forEachSmallOre((ore, smallOreItem, oreMetalFluid, orePowder, heatDefinition) -> {
            if(oreMetalFluid == null) return;

            new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(smallOreItem),heatDefinition.weldingTemperature(),3000))
                    .withFluidOutputs(new FluidStack(oreMetalFluid,10))
                    .requiresHeat(WoodenCogHeatCondition.of(heatDefinition.meltingTemperature()))
                    .build(recipeOutput, WoodenCog.asWoodencogResource(LOCATION,smallOreItem,"_to_liquid"));
        });
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
                .build(recipeOutput, WoodenCog.asWoodencogResource(sandwich));
    }

    private void alloyingRecipes(RecipeOutput recipeOutput){
        // BISMUTH BRONZE (ya convertido)
        this.builder()
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.BISMUTH, 20),
                        CogFluidUtil.ingredient(Metal.ZINC, 30),
                        CogFluidUtil.ingredient(Metal.COPPER, 50))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.BISMUTH_BRONZE, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, WoodenCog.asResource(LOCATION + "alloying_" + Metal.BISMUTH_BRONZE.getSerializedName()));

        // BLACK BRONZE
        this.builder()
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.SILVER, 25),
                        CogFluidUtil.ingredient(Metal.GOLD, 25),
                        CogFluidUtil.ingredient(Metal.COPPER, 50))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.BLACK_BRONZE, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, WoodenCog.asResource(LOCATION + "alloying_" + Metal.BLACK_BRONZE.getSerializedName()));

        // BRASS
        this.builder()
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.COPPER, 90),
                        CogFluidUtil.ingredient(Metal.ZINC, 10))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.BRASS, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, WoodenCog.asResource(LOCATION + "alloying_" + Metal.BRASS.getSerializedName()));

        // BRONZE
        this.builder()
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.COPPER, 90),
                        CogFluidUtil.ingredient(Metal.TIN, 10))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.BRONZE, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, WoodenCog.asResource(LOCATION + "alloying_" + Metal.BRONZE.getSerializedName()));

        // ROSE GOLD
        this.builder()
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.GOLD, 70),
                        CogFluidUtil.ingredient(Metal.COPPER, 30))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.ROSE_GOLD, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, WoodenCog.asResource(LOCATION + "alloying/" + Metal.ROSE_GOLD.getSerializedName()));

        // STERLING SILVER
        this.builder()
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.SILVER, 60),
                        CogFluidUtil.ingredient(Metal.COPPER, 40))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.STERLING_SILVER, 100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, WoodenCog.asResource(LOCATION + "alloying_" + Metal.STERLING_SILVER.getSerializedName()));

        // WEAK BLUE STEEL
        this.builder()
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.BLACK_STEEL, 50),
                        CogFluidUtil.ingredient(Metal.STEEL, 20),
                        CogFluidUtil.ingredient(Metal.BISMUTH_BRONZE, 15),
                        CogFluidUtil.ingredient(Metal.STERLING_SILVER, 15))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.WEAK_BLUE_STEEL,100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, WoodenCog.asResource(LOCATION + "alloying_" + Metal.WEAK_BLUE_STEEL.getSerializedName()));

        // WEAK RED STEEL
        this.builder()
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.BLACK_STEEL, 50),
                        CogFluidUtil.ingredient(Metal.STEEL, 20),
                        CogFluidUtil.ingredient(Metal.BRASS, 15),
                        CogFluidUtil.ingredient(Metal.ROSE_GOLD, 15))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.WEAK_RED_STEEL,100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, WoodenCog.asResource(LOCATION + "alloying_" + Metal.WEAK_RED_STEEL.getSerializedName()));

        // WEAK STEEL
        this.builder()
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Metal.STEEL, 50),
                        CogFluidUtil.ingredient(Metal.NICKEL, 25),
                        CogFluidUtil.ingredient(Metal.BLACK_BRONZE, 25))
                .withFluidOutputs(CogFluidUtil.fluidStack(Metal.WEAK_STEEL,100))
                .requiresHeat(WoodenCogHeatCondition.of(600))
                .duration(400)
                .build(recipeOutput, WoodenCog.asResource(LOCATION + "alloying_" + Metal.WEAK_STEEL.getSerializedName()));
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
                .build(recipeOutput, WoodenCog.asWoodencogResource(ItemAccess.boiledEgg));

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(new ItemStack(ItemAccess.riceGrain))))
                .withFluidIngredients(
                        CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(
                        FoodProcessingOutput.Builder.create().withItem(ItemAccess.cookedRice, 1).build())
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .build(recipeOutput, WoodenCog.asWoodencogResource(ItemAccess.cookedRice));

        //Wood bowl
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(Items.BOWL)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)))
                .withItemOutputs(new SaladProcessingOutput(Items.BOWL,1,1))
                .duration(500)
                .build(recipeOutput, WoodenCog.asResource("food/salads"));

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(Items.BOWL)),
                        FoodIngredient.of(Ingredient.of(Items.BOWL)),
                        FoodIngredient.of(Ingredient.of(Items.BOWL)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)))
                .withFluidIngredients(CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(new SoupProcessingOutput(Items.BOWL,3,1))
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .duration(500)
                .build(recipeOutput, WoodenCog.asResource("food/soups"));

        //Ceramic bowl
        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(ItemAccess.ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SALAD)))
                .withItemOutputs(new SaladProcessingOutput(ItemAccess.ceramicBowl, 1,1))
                .duration(500)
                .build(recipeOutput, WoodenCog.asResource("food/salads_ceramic"));

        new HeatedProcessingRecipeBuilder<>(HeatedMixingRecipe::new)
                .withItemIngredients(
                        FoodIngredient.of(Ingredient.of(ItemAccess.ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(ItemAccess.ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(ItemAccess.ceramicBowl)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)),
                        FoodIngredient.of(Ingredient.of(ModTags.Compat.USABLE_IN_SOUP)))
                .withFluidIngredients(CogFluidUtil.ingredient(Fluids.WATER.getFlowing(),100))
                .withItemOutputs(new SoupProcessingOutput(ItemAccess.ceramicBowl,3,1))
                .requiresHeat(WoodenCogHeatCondition.of(150))
                .duration(500)
                .build(recipeOutput, WoodenCog.asResource("food/soups_ceramic"));
    }

    private void jams(RecipeOutput recipeOutput){
        this.forEachFruit((food, jam, fruit) -> {
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
}
