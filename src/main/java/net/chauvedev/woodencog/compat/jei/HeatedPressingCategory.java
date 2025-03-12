package net.chauvedev.woodencog.compat.jei;

import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.simibubi.create.compat.jei.category.animations.AnimatedPress;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.mixin.HeatableIngredientAccessor;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedBasinRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedPressingRecipe;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class HeatedPressingCategory extends WoodenCogRecipeCategory<HeatedPressingRecipe> {

    private final AnimatedPress press = new AnimatedPress(false);

    public HeatedPressingCategory(WoodenCogRecipeCategory.Info<HeatedPressingRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HeatedPressingRecipe recipe, IFocusGroup iFocusGroup) {
        WoodenCog.LOGGER.info("SET RECIPE FOR PRESSING RECIPES");
        builder
                .addSlot(RecipeIngredientRole.INPUT, 27, 51)
                .setBackground(getRenderedSlot(), -1, -1)
                .addIngredients(recipe.getHeatedIngredients().get(0));

        List<HeatedProcessingOutput> results = recipe.getRollableResults();
        int i = 0;
        for (ProcessingOutput output : results) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 131 + 19 * i, 50)
                    .setBackground(getRenderedSlot(output), -1, -1)
                    .addItemStack(output.getStack())
                    .addTooltipCallback(addStochasticTooltip(output));
            i++;
        }
    }

    @Override
    public void draw(HeatedPressingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        AllGuiTextures.JEI_SHADOW.render(guiGraphics, 61, 41);
        AllGuiTextures.JEI_LONG_ARROW.render(guiGraphics, 52, 54);

        press.draw(guiGraphics, getWidth() / 2 - 17, 22);

        float time = AnimationTickHolder.getRenderTime()/2;
        if(((int) time) % 50 == 0) {
            setInputTemperatureCapability(recipe, recipeSlotsView,false);
        } else if(((int) time) % 10 == 0) {
            setInputTemperatureCapability(recipe, recipeSlotsView, true);
        }
        setOputputTemperatureCapability(recipe,recipeSlotsView);
    }

    private static void setInputTemperatureCapability(HeatedPressingRecipe recipe, IRecipeSlotsView recipeSlotsView, boolean setMax) {
        for (IRecipeSlotView slotView : recipeSlotsView.getSlotViews()){
            if(slotView.getDisplayedItemStack().isEmpty()) return;
            ItemStack displayItemStack = slotView.getDisplayedItemStack().get();
            HeatableIngredient heatableIngredient = recipe.getHeatedIngredients().get(0);
            for (ItemStack ingredientItemStack : heatableIngredient.getItems()) {
                if(displayItemStack.getItem().equals(ingredientItemStack.getItem())){
                    int temp = setMax ? ((HeatableIngredientAccessor) heatableIngredient).getMaxTemp() : ((HeatableIngredientAccessor) heatableIngredient).getMinTemp();
                    HeatCapability.setTemperature(displayItemStack,temp);
                    return; //Found
                }
            }
        }
    }

    private static void setOputputTemperatureCapability(HeatedPressingRecipe recipe, IRecipeSlotsView recipeSlotsView) {
        for (IRecipeSlotView slotView : recipeSlotsView.getSlotViews()){
            if(slotView.getDisplayedItemStack().isEmpty()) return; //Slot has no itemStack
            ItemStack displayItemStack = slotView.getDisplayedItemStack().get();
            for(HeatedProcessingOutput heatedProcessingOutput : recipe.getRollableResults()){
                ItemStack outputItemStack = heatedProcessingOutput.getStack();
                if(outputItemStack.getItem().equals(displayItemStack.getItem())){
                    if(heatedProcessingOutput.getCopyHeat()){
                        float time = AnimationTickHolder.getRenderTime();
                        float maxTemp = 1000;
                        Optional<IHeat> iheat = displayItemStack.getCapability(HeatCapability.CAPABILITY).resolve();
                        if(iheat.isPresent()){
                            maxTemp = iheat.get().getWeldingTemperature() + 200;
                        }
                        float temp = (float) (Math.sin(time / 10.0) * maxTemp) + 200;
                        HeatCapability.setTemperature(displayItemStack,temp - heatedProcessingOutput.getCooling());
                    }else {
                        HeatCapability.setTemperature(displayItemStack,heatedProcessingOutput.getTemperature());
                    }
                    break; //Found
                }
            }
        }
    }
}
