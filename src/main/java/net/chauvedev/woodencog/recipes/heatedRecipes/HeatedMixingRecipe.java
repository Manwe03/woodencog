package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class HeatedMixingRecipe extends BasinRecipe {

    protected HeatedMixingRecipe(ProcessingRecipeBuilder.ProcessingRecipeParams params) {
        super(Type.INSTANCE, params);
    }

    private static class Type implements IRecipeTypeInfo {
        private static final Type INSTANCE = new Type();
        @Override
        public ResourceLocation getId() {
            return new ResourceLocation(WoodenCog.MOD_ID, "heated_mixing");
        }

        @Override
        public <T extends RecipeSerializer<?>> T getSerializer() {
            return (T) AllHeatedProcessingRecipes.HEATED_MIXING_SERIALIZER.get();
        }

        @Override
        public <T extends RecipeType<?>> T getType() {
            return (T) HEATED_PRESSING_TYPE;
        }

        // Define el RecipeType para HeatedPressingRecipe
        private static final RecipeType<HeatedPressingRecipe> HEATED_PRESSING_TYPE = new RecipeType<>() {
            @Override
            public String toString() {
                return WoodenCog.MOD_ID + ":heated_mixing";
            }
        };
    }
}
