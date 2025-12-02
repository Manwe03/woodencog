package net.chauvedev.woodencog.compat.createlowheated;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EmptyCLHIntegration implements ICLHIntegration {
    /**
     * Get a tfc temperature value for a given blockEntity
     *
     * @param be BlockEntity of other mods
     */
    @Override
    public float getTFCTemperatureOf(BlockEntity be) {
        return 0;
    }

    @Override
    public float lowHeatTemp(BlazeBurnerBlock.HeatLevel heatLevel) {
        return 0;
    }
}
