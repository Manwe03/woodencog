package net.chauvedev.woodencog.compat.createmoreburners;

import net.chauvedev.woodencog.utils.CogUtil;
import net.dragonegg.moreburners.content.block.entity.BaseBurnerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CMBIntegrationImpl implements CMBIntegration{
    /**
     * Get a tfc temperature value for a given blockEntity
     *
     * @param be BlockEntity of other mods
     */
    @Override
    public float getTFCTemperatureOf(BlockEntity be) {
        if(be instanceof BaseBurnerBlockEntity burner){
            return (float) ((burner.heat / 300.0) * 1350.0);
        }
        return 0;
    }
}
