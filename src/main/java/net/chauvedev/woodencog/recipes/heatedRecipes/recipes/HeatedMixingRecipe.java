package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeParams;

public class HeatedMixingRecipe extends HeatedBasinRecipe {
    public HeatedMixingRecipe(HeatedProcessingRecipeParams params) {
        super(AllHeatedRecipeTypes.HEATED_MIXING, params);
    }
}
