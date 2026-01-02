package net.chauvedev.woodencog.mixin.heat;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.BlockEntityAccessor;
import net.chauvedev.woodencog.blockEntities.BlazeBurnerBlockentityExtended;
import net.chauvedev.woodencog.utils.CogUtil;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlazeBurnerBlockEntity.class, remap = false)
public abstract class MixinBlazeBurnerBlockEntity implements BlazeBurnerBlockentityExtended {

    @Shadow protected abstract BlazeBurnerBlock.HeatLevel getHeatLevel();

    @Shadow public boolean isCreative;

    @Unique
    public float getTemperature(){
        return isCreative ? WoodenCogCommonConfigs.BLAZE_BURNER_SEETHING.get().floatValue() : CogUtil.heatLevelToTemp(getHeatLevel());
    }

    /**
     * Add Blaze Burner the capability to heat TFC blocks
     */
    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {

        Level level = ((BlockEntityAccessor) this).getLevel();
        if(!level.isClientSide()){
            HeatCapability.provideHeatTo(level, ((BlockEntityAccessor) this).getWorldPosition().above(), getTemperature());
        }
    }
}
