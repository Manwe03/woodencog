package net.chauvedev.woodencog.mixin.chains;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ChainConveyorConnectionHandler.class, remap = false)
public class MixinChainConveyorConnectionHandler {

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static boolean isChain(ItemStack itemStack) {
        return itemStack.is(ItemTags.create(new ResourceLocation("forge", "chains")));
    }
}
