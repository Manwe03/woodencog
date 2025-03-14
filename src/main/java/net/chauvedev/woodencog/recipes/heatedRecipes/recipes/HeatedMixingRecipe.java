package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;

public class HeatedMixingRecipe extends HeatedBasinRecipe {
    public HeatedMixingRecipe(HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        super(AllHeatedRecipeTypes.HEATED_MIXING, params);
    }
}
