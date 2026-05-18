package net.chauvedev.woodencog.compat.jei;

import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedCompactingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.crafting.RecipeHolder;

public class HeatedCompactingCategory extends HeatedBasinCategory<HeatedCompactingRecipe>{
    private final AnimatedPress press = new AnimatedPress(true);

    public HeatedCompactingCategory(Info<HeatedCompactingRecipe> info) {
        super(info);
        WoodenCog.LOGGER.info("HeatedBasinRecipe loaded" + info.recipeType().toString());
    }

    @Override
    public void draw(RecipeHolder<HeatedCompactingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        press.draw(guiGraphics, getWidth() / 2 + 3, 34);
    }
}
