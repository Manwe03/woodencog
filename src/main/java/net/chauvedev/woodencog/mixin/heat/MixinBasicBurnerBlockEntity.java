package net.chauvedev.woodencog.mixin.heat;

import net.chauvedev.woodencog.compat.Compat;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.BlockEntityAccessor;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zeh.createlowheated.content.processing.basicburner.BasicBurnerBlockEntity;

@Mixin(value = BasicBurnerBlockEntity.class, remap = false)
public class MixinBasicBurnerBlockEntity {

    @Inject(method = "tickFuel",at = @At("HEAD"))
    void tick(CallbackInfo ci){
        Level level = ((BlockEntityAccessor) this).getLevel();
        float temp = Compat.CLH_INSTANCE.getTFCTemperatureOf((BasicBurnerBlockEntity)(Object)this);
        HeatCapability.provideHeatTo(level, ((BlockEntityAccessor) this).getBlockPos().above(), temp);
    }
}
