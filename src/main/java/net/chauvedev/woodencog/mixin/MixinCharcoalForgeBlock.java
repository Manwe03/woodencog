package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmItem;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CharcoalForgeBlock.class, remap = false)
public class MixinCharcoalForgeBlock {
    public MixinCharcoalForgeBlock() {
    }

    @Inject(method = {"useItemOn"}, at = {@At("HEAD")}, cancellable = true)
    public void useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<ItemInteractionResult> cir){
        if(stack.getItem() instanceof ArmItem){
            cir.setReturnValue(ItemInteractionResult.FAIL);
        }
    }
}
