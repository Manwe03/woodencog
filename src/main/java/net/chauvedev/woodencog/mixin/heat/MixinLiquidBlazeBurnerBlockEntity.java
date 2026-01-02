package net.chauvedev.woodencog.mixin.heat;

import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlockEntity;
import net.chauvedev.woodencog.compat.Compat;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.BlockEntityAccessor;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LiquidBlazeBurnerBlockEntity.class,remap = false)
public class MixinLiquidBlazeBurnerBlockEntity {

    @Inject(method = "burningTick",at = @At("HEAD"))
    void tick(CallbackInfo ci){
        Level level = ((BlockEntityAccessor) this).getLevel();
        float temp = Compat.CCA_INSTANCE.getTFCTemperatureOf((LiquidBlazeBurnerBlockEntity)(Object)this);
        HeatCapability.provideHeatTo(level, ((BlockEntityAccessor) this).getWorldPosition().above(), temp);
    }
}
