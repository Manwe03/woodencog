package net.chauvedev.woodencog.compat.jei;

import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.chauvedev.woodencog.utils.Color;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class HeatedPressingCategory extends WoodenCogRecipeCategory<HeatedPressingRecipe> {

    private final AnimatedPress press = new AnimatedPress(false);

    public HeatedPressingCategory(WoodenCogRecipeCategory.Info<HeatedPressingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HeatedPressingRecipe recipe, IFocusGroup iFocusGroup) {
        //WoodenCog.LOGGER.info("SET RECIPE FOR PRESSING RECIPES");
        builder
                .addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getHeatedIngredients().get(0));

        List<HeatedProcessingOutput> results = recipe.getRollableResults();
        int i = 0;
        for (HeatedProcessingOutput output : results) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131 + 19 * i, 50)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(output));
            i++;
        }
    }

    @Override
    public void draw(HeatedPressingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        AllGuiTextures.JEI_SHADOW.render(guiGraphics, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(guiGraphics, 52, 54);

        press.draw(guiGraphics, getWidth() / 2 - 17, 22);

        Color.drawCopyHeatBoxPress(recipe,recipeSlotsView,guiGraphics);
    }
}
