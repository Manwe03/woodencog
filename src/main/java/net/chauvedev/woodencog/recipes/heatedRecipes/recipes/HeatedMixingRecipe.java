package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class HeatedMixingRecipe extends HeatedBasinRecipe {
    public HeatedMixingRecipe(HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        super(AllHeatedRecipeTypes.HEATED_MIXING, params);
    }
}
