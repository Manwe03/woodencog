package net.chauvedev.woodencog.compat.jei;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.item.ItemHelper;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.chauvedev.woodencog.compat.jei.animatedBlocks.AnimatedCharcoalForge;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.HeatedIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.BowlProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.DynamicProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.WoodenCogHeatCondition;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedBasinRecipe;
import net.chauvedev.woodencog.utils.Color;
import net.chauvedev.woodencog.utils.HeatedItemHelper;
import net.createmod.catnip.data.Pair;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.util.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static com.simibubi.create.compat.jei.category.CreateRecipeCategory.addFluidSlot;

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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<HeatedBasinRecipe> recipe, IFocusGroup focuses) {
        List<Pair<Ingredient, MutableInt>> condensedIngredients = ItemHelper.condenseIngredients(recipe.value().getIngredients());

        int size = condensedIngredients.size() + recipe.value().getFluidIngredients().size();
        int xOffset = size < 3 ? (3 - size) * 19 / 2 : 0;
        int i = 0;

        for (Pair<Ingredient, MutableInt> pair : condensedIngredients) {

            Ingredient ingredient = pair.getFirst();
            int minTemp = 0;

            if(ingredient.isCustom()){
                if(ingredient.getCustomIngredient() instanceof HeatedIngredient heatedIngredient){
                    minTemp = heatedIngredient.getMinTemp();
                }
            }

            List<ItemStack> stacks = new ArrayList<>();
            for (ItemStack itemStack : pair.getFirst().getItems()) {
                ItemStack copy = itemStack.copy();
                if(minTemp > 0) HeatCapability.setTemperature(itemStack,minTemp);
                copy.setCount(pair.getSecond().getValue());
                stacks.add(copy);
            }

            builder
                    .addSlot(RecipeIngredientRole.INPUT, 17 + xOffset + (i % 3) * 19, 51 - (i / 3) * 19)
                    .setBackground(getRenderedSlot(), -1, -1)
                    .addItemStacks(stacks);
            i++;
        }
        for (SizedFluidIngredient fluidIngredient : recipe.value().getFluidIngredients()) {
            int x = 17 + xOffset + (i % 3) * 19;
            int y = 51 - (i / 3) * 19;
            addFluidSlot(builder, x, y, fluidIngredient);
            i++;
        }

        size = recipe.value().getRollableResults().size() + recipe.value().getFluidResults().size();
        i = 0;

        for (DynamicProcessingOutput<?> result : recipe.value().getRollableResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;

            if(result instanceof BowlProcessingOutput bowlResult){
                builder
                        .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                        .setBackground(getRenderedSlot(result), -1, -1)
                        .addItemStacks(bowlResult.getStacks())
                        .addRichTooltipCallback(addStochasticTooltip(result));
            }else {
                builder
                        .addSlot(RecipeIngredientRole.OUTPUT, xPosition, yPosition)
                        .setBackground(getRenderedSlot(result), -1, -1)
                        .addItemStack(result.getStack())
                        .addRichTooltipCallback(addStochasticTooltip(result));
            }

            i++;
        }

        for (FluidStack fluidResult : recipe.value().getFluidResults()) {
            int xPosition = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int yPosition = -19 * (i / 2) + 51;
            addFluidSlot(builder, xPosition, yPosition, fluidResult);
            i++;
        }

        WoodenCogHeatCondition requiredHeat = recipe.value().getRequiredHeat();
        if (requiredHeat.getTemperature() > 0) {
            builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 134, 81).addItemStack(TFCBlocks.CHARCOAL_FORGE.get().asItem().getDefaultInstance());
        }
    }

    @Override
    public void draw(RecipeHolder<HeatedBasinRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        WoodenCogHeatCondition requiredHeat = recipe.value().getRequiredHeat();

        boolean noHeat = !requiredHeat.hasTemp();


        int vRows = (1 + recipe.value().getFluidResults().size() + recipe.value().getRollableResults().size()) / 2;

        if (vRows <= 2) AllGuiTextures.JEI_DOWN_ARROW.render(guiGraphics, 136, -19 * (vRows - 1) + 32);

        AllGuiTextures shadow = noHeat ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
        shadow.render(guiGraphics, 81, 58 + (noHeat ? 10 : 30));

        AllGuiTextures heatBar = noHeat ? AllGuiTextures.JEI_NO_HEAT_BAR : AllGuiTextures.JEI_HEAT_BAR;
        heatBar.render(guiGraphics, 4, 80);

        if(!noHeat) heater.draw(guiGraphics, getWidth() / 2 + 3, 55);

        if (!noHeat) {
            int realTemp = requiredHeat.getTemperature();
            int guiTemp = Heat.scaleTemperatureForGui(realTemp);
            guiGraphics.blit(FORGE_TEXTURE, 0, 0, 7, 14, 17, 74);
            guiGraphics.blit(FORGE_TEXTURE, 1, 60 - Math.min(51, guiTemp), 176, 0, 15, 5);


            Heat heat = Heat.getHeat(realTemp);
            if(heat != null){
                Integer color = heat.getColor().getColor();
                if(color!= null){
                    guiGraphics.drawString(
                            Minecraft.getInstance().font,
                            realTemp + "°C",
                            10,
                            86,
                            color,
                            false
                    );
                }
            }
        }

        Color.drawCopyHeatBoxBasin(recipe.value(), recipeSlotsView, guiGraphics);
    }

}