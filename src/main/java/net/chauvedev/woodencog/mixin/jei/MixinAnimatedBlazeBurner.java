//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.chauvedev.woodencog.mixin.jei;

import com.simibubi.create.compat.jei.category.animations.AnimatedBlazeBurner;
import net.chauvedev.woodencog.compat.jei.AnimatedBlocks.AnimatedCharcoalForge;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(
        value = {AnimatedBlazeBurner.class},
        remap = false
)
public class MixinAnimatedBlazeBurner {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        new AnimatedCharcoalForge().draw(graphics,xOffset,yOffset);
    }

}
