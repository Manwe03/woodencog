package net.chauvedev.woodencog.datagen.recipe;

import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.HeatedIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.HeatedProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedCompactingRecipe;
import net.chauvedev.woodencog.utils.ItemAccess;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.crafting.Ingredient;

public class WoodenCogHeatedCompactingRecipeGen extends WoodenCogProcessinRecipeGen {

    private static final String LOCATION = "heated_compacting";

    public WoodenCogHeatedCompactingRecipeGen(RecipeOutput recipeOutput){
        super(recipeOutput);

        this.forEachMetal((metal, ingot, doubleIngot, sheet, metalFluid) -> {

            //Double Ingots recipes
            if(!metal.hasDoubleIngot()) return;
            this.builder()
                .withItemIngredients(
                    HeatedIngredient.of(Ingredient.of(ingot),metal.getWeldingTemperature(),3000),
                    HeatedIngredient.of(Ingredient.of(ingot),metal.getWeldingTemperature(),3000),
                    HeatedIngredient.of(Ingredient.of(ItemAccess.flux),0,3000))
                .withItemOutputs(HeatedProcessingOutput.of(doubleIngot,1,1,0,true,0))
                .build(recipeOutput, WoodenCog.asResource(LOCATION+"/double_"+metal.id()));
        });
    }

    @Override
    protected HeatedProcessingRecipeBuilder<?> builder() {
        return new HeatedProcessingRecipeBuilder<>(HeatedCompactingRecipe::new);
    }
}
