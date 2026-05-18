package net.chauvedev.woodencog.compat.createaddition;

import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlockEntity;
import net.chauvedev.woodencog.utils.CogUtil;
import net.minecraft.world.level.block.entity.BlockEntity;

public class CCAIntegrationImpl implements ICCAIntegration{

    @Override
    public float getTFCTemperatureOf(BlockEntity be) {
        if(be instanceof LiquidBlazeBurnerBlockEntity liquidBurner){
            return CogUtil.heatLevelToTemp(liquidBurner.getHeatLevelFromBlock());
        }
        return 0;
    }
}
