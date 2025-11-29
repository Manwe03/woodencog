package net.chauvedev.woodencog.mixin.blockEnitites.accessors;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BasinBlockEntity.class, remap = false)
public interface BasinBlockEntityAccessor {
    @Accessor("outputTank")
    SmartFluidTankBehaviour getOutputTank();
}
