package net.chauvedev.woodencog.utils;

import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.DynamicProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.HeatedProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class Color {

    public static int modify(int color, float factor, int sum) {
        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        r = (int) Math.min((r * factor)+sum, 255);
        g = (int) Math.min((g * factor)+sum, 255);
        b = (int) Math.min((b * factor)+sum, 255);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static void drawCopyHeatBoxPress(HeatedProcessingRecipe<?,?> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics) {
        drawCopyHeatBoxes(recipe, recipeSlotsView, guiGraphics, (i, size) -> {
            int x = 131 + 19 * i;
            int y = 50;
            return new int[]{x, y};
        });
    }

    public static void drawCopyHeatBoxBasin(HeatedProcessingRecipe<?,?> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics) {
        drawCopyHeatBoxes(recipe, recipeSlotsView, guiGraphics, (i, size) -> {
            int x = 142 - (size % 2 != 0 && i == size - 1 ? 0 : i % 2 == 0 ? 10 : -9);
            int y = -19 * (i / 2) + 51;
            return new int[]{x, y};
        });
    }

    private static void drawCopyHeatBoxes(
            HeatedProcessingRecipe<?,?> recipe,
            IRecipeSlotsView recipeSlotsView,
            GuiGraphics guiGraphics,
            PositionFunction posFunction
    ) {
        float time = (AnimationTickHolder.getRenderTime() / 100.0f) % 1.0f;
        List<DynamicProcessingOutput<?>> results = recipe.getRollableResults();
        List<IRecipeSlotView> views = recipeSlotsView.getSlotViews(RecipeIngredientRole.OUTPUT);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 100);

        int size = views.size();

        for (int i = 0; i < size; i++) {
            for (DynamicProcessingOutput<?> output : results) {
                if (output instanceof HeatedProcessingOutput heatedOutput) {
                    Optional<ItemStack> viewItemStack = views.get(i).getDisplayedItemStack();
                    if (viewItemStack.isPresent() && heatedOutput.getStack().getItem() == viewItemStack.get().getItem() && heatedOutput.getCopyHeat()) {
                        int[] pos = posFunction.compute(i, size);
                        int x = pos[0];
                        int y = pos[1];
                        drawBorder(guiGraphics, x, y, time);
                    }
                }
            }
        }

        guiGraphics.pose().popPose();
    }

    private static void drawBorder(GuiGraphics guiGraphics, int x, int y, float time) {
        int color = Color.tempColorgradient(time);
        guiGraphics.fill(x, y, x + 16, y + 1, color);
        guiGraphics.fill(x, y + 15, x + 16, y + 16, color);
        guiGraphics.fill(x, y, x + 1, y + 16, color);
        guiGraphics.fill(x + 15, y, x + 16, y + 16, color);
    }

    @FunctionalInterface
    private interface PositionFunction {
        int[] compute(int index, int totalSize);
    }

    public enum TextColors {
        BLACK(0xFF000000),
        DARK_BLUE(0xFF0000AA),
        DARK_GREEN(0xFF00AA00),
        DARK_AQUA(0xFF00AAAA),
        DARK_RED(0xFFAA0000),
        DARK_PURPLE(0xFFAA00AA),
        GOLD(0xFFFFAA00),
        GRAY(0xFFAAAAAA),
        DARK_GRAY(0xFF555555),
        BLUE(0xFF5555FF),
        GREEN(0xFF55FF55),
        AQUA(0xFF55FFFF),
        RED(0xFFFF5555),
        LIGHT_PURPLE(0xFFFF55FF),
        YELLOW(0xFFFFFF55),
        WHITE(0xFFFFFFFF);

        private final int color;
        TextColors(int color) {
            this.color = color;
        }
        public int getColor() {
            return color;
        }
    }

    public static int tempColorgradient(float t) {
        t = Math.min(1.0f, Math.max(0.0f, t));

        int dark_gray = TextColors.DARK_GRAY.getColor();
        int dark_red = TextColors.DARK_RED.getColor();
        int red = TextColors.RED.getColor();
        int gold = TextColors.GOLD.getColor();
        int yellow = TextColors.YELLOW.getColor();
        int white  = TextColors.WHITE.getColor();

        if (t < 0.2f) {
            float localT = t / (1f / 5f);
            return lerpColor(dark_gray, dark_red, localT);
        } else if (t < 0.4f) {
            float localT = (t - 1f / 5f) / (1f / 5f);
            return lerpColor(dark_red, red, localT);
        } else if (t < 0.6f) {
            float localT = (t - 2f / 5f) / (1f / 5f);
            return lerpColor(red, gold, localT);
        } else if (t < 0.8f) {
            float localT = (t - 3f / 5f) / (1f / 5f);
            return lerpColor(gold, yellow, localT);
        } else {
            float localT = (t - 4f / 5f) / (1f / 5f);
            return lerpColor(yellow, white, localT);
        }
    }

    /**
     * Linear interpolation
     */
    private static int lerpColor(int startColor, int endColor, float t) {
        int a1 = (startColor >> 24) & 0xFF;
        int r1 = (startColor >> 16) & 0xFF;
        int g1 = (startColor >> 8) & 0xFF;
        int b1 = startColor & 0xFF;

        int a2 = (endColor >> 24) & 0xFF;
        int r2 = (endColor >> 16) & 0xFF;
        int g2 = (endColor >> 8) & 0xFF;
        int b2 = endColor & 0xFF;

        int a = (int)(a1 + (a2 - a1) * t);
        int r = (int)(r1 + (r2 - r1) * t);
        int g = (int)(g1 + (g2 - g1) * t);
        int b = (int)(b1 + (b2 - b1) * t);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
