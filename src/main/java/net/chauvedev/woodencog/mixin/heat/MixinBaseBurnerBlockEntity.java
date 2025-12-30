package net.chauvedev.woodencog.mixin.heat;

import net.chauvedev.woodencog.compat.Compat;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.BlockEntityAccessor;
import net.dragonegg.moreburners.content.block.entity.BaseBurnerBlockEntity;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BaseBurnerBlockEntity.class, remap = false)
public class MixinBaseBurnerBlockEntity {

    @Inject(method = "tick", at = @At("HEAD"))
    void tick(Level level, BlockPos pos, BlockState state, CallbackInfo ci){
        float temp = Compat.CMB_INSTANCE.getTFCTemperatureOf((BaseBurnerBlockEntity)(Object)this);
        HeatCapability.provideHeatTo(level, ((BlockEntityAccessor) this).getBlockPos().above(), temp);
    }
}
