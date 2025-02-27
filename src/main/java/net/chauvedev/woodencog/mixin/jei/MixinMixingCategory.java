package net.chauvedev.woodencog.mixin.jei;

import com.simibubi.create.compat.jei.category.MixingCategory;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingIngredient;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Mixin(value = MixingCategory.class, remap = false)
public class MixinMixingCategory {
    @Inject(
            method = "draw(Lcom/simibubi/create/content/processing/basin/BasinRecipe;Lmezz/jei/api/gui/ingredient/IRecipeSlotsView;Lnet/minecraft/client/gui/GuiGraphics;DD)V",
            at = @At("TAIL")
    )
    public void draw(BasinRecipe recipe, IRecipeSlotsView recipeSlots, GuiGraphics graphics, double mouseX, double mouseY, CallbackInfo ci) {
        for (IRecipeSlotView view : recipeSlots.getSlotViews()) {
            ItemStack viewItemstack = null; //Item mostrado en gui
            if(view.getDisplayedItemStack().isPresent()) {
                viewItemstack = view.getDisplayedItemStack().get();
            }
            if(view.getRole() == RecipeIngredientRole.INPUT){
                drawHeatOnIngredients(graphics,recipe,viewItemstack);
            }else if(view.getRole() == RecipeIngredientRole.OUTPUT){

            }
        }
    }

    public void drawHeatOnIngredients(GuiGraphics graphics, BasinRecipe recipe, ItemStack viewItemstack){
        for (Ingredient ingredient : recipe.getIngredients()){
            WoodenCog.LOGGER.info("CLASE");
            WoodenCog.LOGGER.info(String.valueOf(ingredient.getClass()));
            if(ingredient instanceof HeatedProcessingIngredient heatedIngredient){

                float temperature = heatedIngredient.getMinTemp();
                ItemStack recipeItemStack = ingredient.getItems()[0];
                if(viewItemstack != null && viewItemstack.getItem().equals(recipeItemStack.getItem())){

                    //HeatCapability.setTemperature(viewItemstack, temperature);
                    AllGuiTextures.JEI_LONG_ARROW.render(graphics,40,40);

                    HeatCapability.setTemperature(viewItemstack, temperature);

                    WoodenCog.LOGGER.info("Set temperature " + viewItemstack.getItem() + " : " + temperature);
                    //System.out.println("Set temperature " + viewItemstack.getItem() + " : " + temperature);
                }

                WoodenCog.LOGGER.info("Instance Of HeatedProcessingIngredient");
            }else {
                WoodenCog.LOGGER.info("Not Instance Of HeatedProcessingIngredient");
            }
        }
    }
}
