package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.kinetics.fan.processing.AllFanProcessingTypes;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = AllFanProcessingTypes.SmokingType.class, remap = false)
public class MixinSmokingType {
    @Inject(method = "isValidAt",at = @At("RETURN"), cancellable = true)
    public void isValidAtReturn(Level level, BlockPos pos, CallbackInfoReturnable<Boolean> cir){
        if(!cir.getReturnValue()){
            BlockState blockState = level.getBlockState(pos);
            if(blockState.hasProperty(FirepitBlock.LIT)){
                cir.setReturnValue(blockState.getValue(FirepitBlock.LIT));
            }
        }
    }
}
