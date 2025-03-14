package net.chauvedev.woodencog.compat.jei.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HeatableItemRenderer implements IIngredientRenderer<ItemStack> {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void render(GuiGraphics guiGraphics, @Nullable ItemStack ingredient) {
        if (ingredient != null) {
            RenderSystem.enableDepthTest();

            Minecraft minecraft = Minecraft.getInstance();
            Font font = getFontRenderer(minecraft, ingredient);
            guiGraphics.renderFakeItem(ingredient, 0, 0);
            guiGraphics.renderItemDecorations(font, ingredient, 0, 0);
            RenderSystem.disableBlend();
        }
    }

    @Override
    public List<Component> getTooltip(ItemStack ingredient, TooltipFlag tooltipFlag) {
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        try {
            return ingredient.getTooltipLines(player, tooltipFlag);
        } catch (RuntimeException | LinkageError e) {
            //String itemStackInfo = ErrorUtil.getItemStackInfo(ingredient);
            LOGGER.error("Failed to get tooltip: {}", ingredient, e);
            List<Component> list = new ArrayList<>();
            MutableComponent crash = Component.translatable("jei.tooltip.error.crash");
            list.add(crash.withStyle(ChatFormatting.RED));
            return list;
        }
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, ItemStack ingredient, TooltipFlag tooltipFlag) {
        IIngredientRenderer.super.getTooltip(tooltip, ingredient, tooltipFlag);
    }

    @Override
    public int getWidth() {
        return 16;
    }

    @Override
    public int getHeight() {
        return 16;
    }
}
