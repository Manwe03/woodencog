package net.chauvedev.woodencog.compat.jei;

import com.simibubi.create.compat.jei.category.animations.AnimatedMixer;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedBasinRecipe;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Near copy of - credit to the Create team
 * @see com.simibubi.create.compat.jei.category.MixingCategory
 */
public class HeatedMixingCategory extends HeatedBasinCategory {
    private final AnimatedMixer mixer = new AnimatedMixer();
    MixingType type;

    enum MixingType {
        MIXING
    }

    protected HeatedMixingCategory(Info<HeatedBasinRecipe> info) {
        super(info);
        this.type = MixingType.MIXING;
    }

    @Override
    public void draw(HeatedBasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        mixer.draw(guiGraphics, getWidth() / 2 + 3, 34);
    }
}
