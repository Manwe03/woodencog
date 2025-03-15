package net.chauvedev.woodencog.compat.jei;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.chauvedev.woodencog.compat.jei.AnimatedBlocks.AnimatedCharcoalForge;
import net.chauvedev.woodencog.mixin.HeatableIngredientAccessor;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.WoodenCogHeatCondition;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedBasinRecipe;
import net.chauvedev.woodencog.utils.HeatedItemHelper;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Pair;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.dries007.tfc.util.Helpers;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Near copy of - credit to the Create team
 * @see com.simibubi.create.compat.jei.category.BasinCategory
 */
@ParametersAreNonnullByDefault
public abstract class HeatedBasinCategory extends WoodenCogRecipeCategory<HeatedBasinRecipe> {
    private static final ResourceLocation FORGE_TEXTURE = Helpers.identifier("textures/gui/charcoal_forge.png");
    private final AnimatedCharcoalForge heater = new AnimatedCharcoalForge();

    public HeatedBasinCategory(Info<HeatedBasinRecipe> info) {
        super(info);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, HeatedBasinRecipe recipe, IFocusGroup iFocusGroup) {
        List<Pair<HeatableIngredient, MutableInt>> condensedIngredients = HeatedItemHelper.condenseIngredients(recipe.getHeatedIngredients());

        int size = condensedIngredients.size() + recipe.getFluidIngredients().size();
        int xOffset = size < 3 ? (3 - size) * 19 / 2 : 9;
        int i = 0;

        for (Pair<HeatableIngredient, MutableInt> pair : condensedIngredients) {
            List<ItemStack> stacks = new ArrayList<>();
            HeatableIngredient ingredient = pair.getFirst();
            int minTemp = ((HeatableIngredientAccessor) ingredient).getMinTemp();
            for (ItemStack itemStack : pair.getFirst().getItems()) {
                HeatCapability.setTemperature(itemStack,minTemp);
                itemStack.setCount(pair.getSecond().getValue());
                stacks.add(itemStack);
            }

            builder.addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19)
                .setBackground(getRenderedSlot(), -1, -1)
                .addItemStacks(stacks);
            i++;
        }
        for (FluidIngredient fluidIngredient : recipe.getFluidIngredients()) {
            builder
                    .addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addIngredients(ForgeTypes.FLUID_STACK, withImprovedVisibility(fluidIngredient.getMatchingFluidStacks()))
                    .addRichTooltipCallback(addFluidTooltip(fluidIngredient.getRequiredAmount()));
            i++;
        }

        size = recipe.getRollableResults().size() + recipe.getFluidResults().size();
        i = 0;

        for (ProcessingOutput result : recipe.getRollableResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;

            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                    .setBackground(getRenderedSlot(result), -1, -1)
                    .addItemStack(result.getStack())
                    .addRichTooltipCallback(addStochasticTooltip(result));
            i++;
        }

        for (FluidStack fluidResult : recipe.getFluidResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;

            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addIngredient(ForgeTypes.FLUID_STACK, withImprovedVisibility(fluidResult))
                    .addRichTooltipCallback(addFluidTooltip(fluidResult.getAmount()));
            i++;
        }

        WoodenCogHeatCondition requiredHeat = recipe.getRequiredHeat();
        if (requiredHeat.getTemperature() > 0) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81).addItemStack(TFCBlocks.CHARCOAL_FORGE.get().asItem().getDefaultInstance());
        }
    }

    @Override
    public void draw(HeatedBasinRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        WoodenCogHeatCondition requiredHeat = recipe.getRequiredHeat();

        boolean noHeat = !requiredHeat.hasTemp();


        int vRows = (1 + recipe.getFluidResults().size() + recipe.getRollableResults().size()) / 2;

        if (vRows <= 2) AllGuiTextures.JEI_DOWN_ARROW.render(guiGraphics, 136, -19 * (vRows - 1) + 32);

        AllGuiTextures shadow = noHeat ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
        shadow.render(guiGraphics, 81, 58 + (noHeat ? 10 : 30));

        AllGuiTextures heatBar = noHeat ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR;
        heatBar.render(guiGraphics, 4, 80);

        if(!noHeat) heater.draw(guiGraphics, getWidth() / 2 + 3, 55);

        if (!noHeat) {
            int guiTemp = Heat.scaleTemperatureForGui(requiredHeat.getTemperature());
            guiGraphics.blit(FORGE_TEXTURE, 0, 0, 7, 14, 17, 74);
            guiGraphics.blit(FORGE_TEXTURE, 1, 60 - Math.min(51, guiTemp), 176, 0, 15, 5);
        }

        drawTemperatureCapability(recipe, recipeSlotsView);
    }

    private static void drawTemperatureCapability(HeatedBasinRecipe recipe, IRecipeSlotsView recipeSlotsView) {
        float time = AnimationTickHolder.getRenderTime()/2;
        if(((int) time) % 50 == 0) {
            setInputTemperatureCapability(recipe, recipeSlotsView,false);
        } else if(((int) time) % 10 == 0) {
            setInputTemperatureCapability(recipe, recipeSlotsView, true);
        }
        setOputputTemperatureCapability(recipe,recipeSlotsView);
    }

    private static void setInputTemperatureCapability(HeatedBasinRecipe recipe, IRecipeSlotsView recipeSlotsView, boolean setMax) {
        for (IRecipeSlotView slotView : recipeSlotsView.getSlotViews()){
            if(slotView.getDisplayedItemStack().isEmpty()) return;
            ItemStack displayItemStack = slotView.getDisplayedItemStack().get();
            for (HeatableIngredient heatableIngredient : recipe.getHeatedIngredients()) {
                for (ItemStack ingredientItemStack : heatableIngredient.getItems()) {
                    if (displayItemStack.getItem().equals(ingredientItemStack.getItem())) {
                        int temp = setMax ? ((HeatableIngredientAccessor) heatableIngredient).getMaxTemp()
                                : ((HeatableIngredientAccessor) heatableIngredient).getMinTemp();
                        if(temp >= 3000){
                            temp = ((HeatableIngredientAccessor) heatableIngredient).getMinTemp();
                        }
                        HeatCapability.setTemperature(displayItemStack, temp);
                        break; // Found match, no need to check further for this slot
                    }
                }
            }
        }
    }

    private static void setOputputTemperatureCapability(HeatedBasinRecipe recipe, IRecipeSlotsView recipeSlotsView) {
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