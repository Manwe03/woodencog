package net.chauvedev.woodencog.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class WoodenCogCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HANDLE_TEMPERATURE;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> WEAR_BLACKLIST;
    public static ForgeConfigSpec.ConfigValue<Integer> DEFAULT_DURABILITY;
    public static ForgeConfigSpec.ConfigValue<Integer> DEFAULT_DAMAGE_CHANCE;

    static {
        BUILDER.push("woodencog");

        BUILDER.push("temperature");
        HANDLE_TEMPERATURE = BUILDER
                .comment("Should create handle temperature ?")
                .define("handle_temperature", true);
        BUILDER.pop();

        BUILDER.push("wearing");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}

