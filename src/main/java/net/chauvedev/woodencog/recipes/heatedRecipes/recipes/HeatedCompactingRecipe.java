package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;

public class HeatedCompactingRecipe extends HeatedBasinRecipe{
    public HeatedCompactingRecipe(HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        super(AllHeatedRecipeTypes.HEATED_COMPACTING, params);
    }
}
