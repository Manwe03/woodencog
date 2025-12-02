package net.chauvedev.woodencog.compat.createaddition;

import net.minecraft.world.level.block.entity.BlockEntity;

public class EmptyCCAIntegration implements ICCAIntegration {
    @Override
    public float getTFCTemperatureOf(BlockEntity be) {
        return 0;
    }
}
