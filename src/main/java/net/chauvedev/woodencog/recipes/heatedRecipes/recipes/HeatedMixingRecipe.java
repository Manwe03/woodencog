package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;

public class HeatedMixingRecipe extends HeatedBasinRecipe {
    public HeatedMixingRecipe(HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams<HeatableIngredient> params) {
        super(AllHeatedRecipeTypes.HEATED_MIXING, params);
    }
}
