package net.chauvedev.woodencog.datagen.recipe;

import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.HeatedIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.HeatedProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.chauvedev.woodencog.utils.ItemAccess;
import net.dries007.tfc.common.component.heat.Heat;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;

public class WoodenCogHeatedPressingRecipeGen extends WoodenCogProcessinRecipeGen{

    private static final String LOCATION = "heated_pressing";

    public WoodenCogHeatedPressingRecipeGen(RecipeOutput recipeOutput) {
        super(recipeOutput);

        this.sheets(recipeOutput);
        this.ironBloom(recipeOutput);
    }

    private void sheets(RecipeOutput recipeOutput){
        this.forEachMetal((metal, ingot, doubleIngot, sheet, metalFluid) -> {
            //Sheet recipes
            if(!metal.hasDoubleIngot()) return;
            this.builder()
                    .withItemIngredients(HeatedIngredient.of(Ingredient.of(doubleIngot), metal.getForginTemperature(),3000))
                    .withItemOutputs(HeatedProcessingOutput.of(sheet,1,1,0,true,0))
                    .build(recipeOutput, WoodenCog.asResource(LOCATION+"/sheet_"+metal.id()));
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

    @Override
    protected HeatedProcessingRecipeBuilder<?> builder() {
        return new HeatedProcessingRecipeBuilder<>(HeatedPressingRecipe::new);
    }
}
