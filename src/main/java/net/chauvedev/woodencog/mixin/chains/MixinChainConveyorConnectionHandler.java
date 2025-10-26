package net.chauvedev.woodencog.mixin.chains;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionHandler;
import net.chauvedev.woodencog.utils.ModTags;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ChainConveyorConnectionHandler.class, remap = false)
public class MixinChainConveyorConnectionHandler {

    /**
     * @author Manwe
     * @reason Check if item is a chain
     */
    @Overwrite
    private static boolean isChain(ItemStack itemStack) {
        if(ModTags.Items.CHAINS == null) return false;
        return itemStack.is(ModTags.Items.CHAINS);
    }
}
