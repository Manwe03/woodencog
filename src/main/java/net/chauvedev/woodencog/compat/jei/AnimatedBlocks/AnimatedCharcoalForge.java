package net.chauvedev.woodencog.compat.jei.AnimatedBlocks;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.client.gui.GuiGraphics;

public class AnimatedCharcoalForge extends AnimatedKinetics {

    public AnimatedCharcoalForge() {}

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();

        // Traslada y rota la matriz para obtener la vista deseada
        matrixStack.translate((float)xOffset, (float)yOffset, 200.0F);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));

        int scale = 23;
        this.blockElement(TFCBlocks.CHARCOAL_FORGE.get().defaultBlockState())
                .atLocal(0.0, 1.65, 0.0)
                .scale((double) scale)
                .render(graphics);

        matrixStack.popPose();
    }
}
