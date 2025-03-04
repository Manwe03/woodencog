package net.chauvedev.woodencog.utils;

import net.dries007.tfc.common.capabilities.heat.IHeat;

import java.util.List;

public class HeatHandlingUtil {
    /**
     * @param heat1
     * @param heat2
     * @return temperature after perfect heat transfer between materials
     */
    public static float computeThermalEquilibrium(IHeat heat1, IHeat heat2){
        heat1.getHeatCapacity();
        return 0f;
    }
}
