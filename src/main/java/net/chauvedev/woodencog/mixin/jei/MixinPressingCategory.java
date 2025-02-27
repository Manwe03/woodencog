package net.chauvedev.woodencog.mixin.jei;

import com.simibubi.create.compat.jei.category.PressingCategory;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedPressingRecipe;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.security.DrbgParameters;
import java.util.Iterator;

@Mixin(value = PressingCategory.class, remap = false)
public class MixinPressingCategory {

    @Shadow @Final private AnimatedPress press;

    /**
     * @author
     * @reason
     */
    @Inject(
        method = "draw(Lcom/simibubi/create/content/kinetics/press/PressingRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
        at = @At("HEAD")
    )
    public void draw(PressingRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {

        for (IRecipeSlotView view : recipeSlots.getSlotViews()) {
            if(view.getRole().equals(RecipeIngredientRole.INPUT)){
                float temperature = recipe.getIngredients().get(0).getItems()[0].getCapability(HeatCapability.CAPABILITY).resolve().get().getTemperature();
                view.getDisplayedItemStack().ifPresent((itemStack -> {
                    HeatCapability.setTemperature(itemStack, temperature);
                }));
            }
            if(view.getRole().equals(RecipeIngredientRole.OUTPUT)){
                float temperature = recipe.rollResults().get(0).getCapability(HeatCapability.CAPABILITY).resolve().get().getTemperature();
                view.getDisplayedItemStack().ifPresent((itemStack -> {
                    HeatCapability.setTemperature(itemStack, temperature);
                }));
            }
        }
    }
}
