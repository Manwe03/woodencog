package net.chauvedev.woodencog.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WoodenCogCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HANDLE_TEMPERATURE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DEPLOYER_COPY_TEMPERATURE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> WEAR_BLACKLIST;
    public static ForgeConfigSpec.ConfigValue<Integer> DEFAULT_DURABILITY;
    public static ForgeConfigSpec.ConfigValue<Integer> DEFAULT_DAMAGE_CHANCE;
    public static final Map<String, ForgeConfigSpec.DoubleValue> ITEM_DENSITY = new HashMap<>();

    static {
        BUILDER.push("woodencog");

        BUILDER.push("temperature");
        HANDLE_TEMPERATURE = BUILDER
                .comment("Should create handle temperature ?")
                .define("handle_temperature", true);
        BUILDER.pop();

        BUILDER.push("temperature");
        DEPLOYER_COPY_TEMPERATURE = BUILDER
                .comment("Should deploying copy input item temperature (ignored if handle temperature disabled)")
                .define("deployer_copy_temperature",true);
        BUILDER.pop();

        BUILDER.push("wearing");

        BUILDER.pop();

        BUILDER.comment("Density of items").push("density");
        addDensityConfig("d",1);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private static void addDensityConfig(String itemId, double defaultDensity) {
        ITEM_DENSITY.put(itemId, BUILDER.comment("Densidad de " + itemId).defineInRange(itemId, defaultDensity, 0.1, 100.0));
    }
}

