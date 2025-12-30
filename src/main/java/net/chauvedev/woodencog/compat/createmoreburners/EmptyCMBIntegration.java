package net.chauvedev.woodencog.compat.createmoreburners;

import net.dragonegg.moreburners.content.block.entity.BaseBurnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class EmptyCMBIntegration implements CMBIntegration{
    /**
     * Get a tfc temperature value for a given blockEntity
     *
     * @param be BlockEntity of other mods
     */
    @Override
    public float getTFCTemperatureOf(BlockEntity be) {
        return 0;
    }
}
