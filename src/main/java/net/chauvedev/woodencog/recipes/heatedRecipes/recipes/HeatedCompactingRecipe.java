package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeParams;

public class HeatedCompactingRecipe extends HeatedBasinRecipe{
    public HeatedCompactingRecipe(HeatedProcessingRecipeParams params) {
        super(AllHeatedRecipeTypes.HEATED_COMPACTING, params);
    }
}
