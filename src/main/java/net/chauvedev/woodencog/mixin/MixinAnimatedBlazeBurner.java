//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.chauvedev.woodencog.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(
        value = {AnimatedBlazeBurner.class},
        remap = false
)
public class MixinAnimatedBlazeBurner extends AnimatedKinetics {
    @Override
    public void draw(GuiGraphics guiGraphics, int xOffset, int yOffset) {
        PoseStack matrixStack = guiGraphics.pose();
        matrixStack.pushPose();
        matrixStack.translate((float)xOffset, (float)yOffset, 200.0F);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5F));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5F));
        int scale = 23;
        this.blockElement(TFCBlocks.CHARCOAL_FORGE.get().defaultBlockState()).atLocal(0.0, 1.65, 0.0).scale((double)scale).render(guiGraphics);
        matrixStack.scale((float)scale, (float)(-scale), (float)scale);
        matrixStack.translate(0.0, -1.8, 0.0);
        Minecraft mc = Minecraft.getInstance();
        matrixStack.popPose();
    }
}
