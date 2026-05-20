package net.chauvedev.woodencog.compat.jei;

import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.DynamicProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.chauvedev.woodencog.utils.Color;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

public class HeatedPressingCategory extends WoodenCogRecipeCategory<HeatedPressingRecipe> {

    private final AnimatedPress press = new AnimatedPress(false);

    public HeatedPressingCategory(WoodenCogRecipeCategory.Info<HeatedPressingRecipe> info) {
        super(info);
    }


    /**
     * Sets all the recipe's ingredients by filling out an instance of {@link IRecipeLayoutBuilder}.
     * This is used by JEI for lookups, to figure out what ingredients are inputs and outputs for a recipe.
     * @since 9.4.0
     */
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<HeatedPressingRecipe> recipe, IFocusGroup focuses) {
        super.setRecipe(builder, recipe, focuses);
        builder.addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.value().getIngredients().get(0));

        List<DynamicProcessingOutput<?>> results = recipe.value().getRollableResults();

        int i = 0;
        for (DynamicProcessingOutput<?> output : results) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131 + 19 * i, 50)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(output));
            i++;
        }
    }

    @Override
    public void draw(RecipeHolder<HeatedPressingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        AllGuiTextures.JEI_SHADOW.render(guiGraphics, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(guiGraphics, 52, 54);

        press.draw(guiGraphics, getWidth() / 2 - 17, 22);

        Color.drawCopyHeatBoxPress(recipe.value(),recipeSlotsView,guiGraphics);
    }
}
