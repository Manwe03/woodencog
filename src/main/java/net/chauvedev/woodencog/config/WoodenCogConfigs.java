package net.chauvedev.woodencog.config;

import com.simibubi.create.api.stress.BlockStressValues;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class WoodenCogConfigs {

    public static WStress stressConfig;

    public static void registerConfigs() {
        Pair<WStress, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
            WStress config = new WStress();
            config.registerAll(builder);
            return config;
        });

        stressConfig = specPair.getLeft();
        stressConfig.specification = specPair.getRight();

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, stressConfig.specification);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WoodenCogCommonConfigs.SPEC, "woodencog-common.toml");

        // Registrar proveedor de impactos y capacidades en Create
        BlockStressValues.IMPACTS.registerProvider(stressConfig::getImpact);
        BlockStressValues.CAPACITIES.registerProvider(stressConfig::getCapacity);
    }

}
