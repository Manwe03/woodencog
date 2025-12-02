package net.chauvedev.woodencog.compat.createlowheated;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import net.chauvedev.woodencog.compat.CompatHeatSource;

public interface ICLHIntegration extends CompatHeatSource {
    float lowHeatTemp(BlazeBurnerBlock.HeatLevel heatLevel);
}
