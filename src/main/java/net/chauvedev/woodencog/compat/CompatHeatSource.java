package net.chauvedev.woodencog.compat;

import net.minecraft.world.level.block.entity.BlockEntity;

public interface CompatHeatSource {

    /**
     * Get a tfc temperature value for a given blockEntity
     * @param be BlockEntity of other mods
     */
    float getTFCTemperatureOf(BlockEntity be);
}
