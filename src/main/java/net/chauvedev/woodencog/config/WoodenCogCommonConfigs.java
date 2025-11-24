package net.chauvedev.woodencog.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WoodenCogCommonConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> HANDLE_TEMPERATURE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DEPLOYER_COPY_TEMPERATURE;

    public static final ForgeConfigSpec.ConfigValue<Float> BLAZE_BURNER_NONE;
    public static final ForgeConfigSpec.ConfigValue<Float> BLAZE_BURNER_SMOULDERING;
    public static final ForgeConfigSpec.ConfigValue<Float> BLAZE_BURNER_FADING;
    public static final ForgeConfigSpec.ConfigValue<Float> BLAZE_BURNER_KINDLED;
    public static final ForgeConfigSpec.ConfigValue<Float> BLAZE_BURNER_SEETHING;

    public static final ForgeConfigSpec.ConfigValue<Boolean> NETHERITE_RESKIN;

    public static final ForgeConfigSpec.ConfigValue<Integer> CT_TRANSFORMER_IMPACT;
    public static final ForgeConfigSpec.ConfigValue<Integer> WOODEN_GENERATOR_BASE_SU;
    public static final ForgeConfigSpec.ConfigValue<Integer> WOODEN_GENERATOR_WIND_FACTOR;
    public static final ForgeConfigSpec.ConfigValue<Integer> WOODEN_GENERATOR_SPEED_FACTOR;

    public static final Map<String, ForgeConfigSpec.ConfigValue<List<Integer>>> MATERIAL_PROPERTIES = new HashMap<>();

    static {
        BUILDER.push("woodencog");

        BUILDER.push("temperature");
            HANDLE_TEMPERATURE = BUILDER
                .comment("Should create handle temperature ?")
                .define("handle_temperature", true);

            DEPLOYER_COPY_TEMPERATURE = BUILDER
                .comment("Should deploying copy input item temperature (ignored if handle temperature disabled)")
                .define("deployer_copy_temperature",true);

            BUILDER.comment("Blaze Burner Temperature");

            BLAZE_BURNER_NONE = BUILDER.define("blaze_burner_none", 0.0f);
            BLAZE_BURNER_SMOULDERING = BUILDER.define("blaze_burner_smouldering", 80.0f);
            BLAZE_BURNER_FADING = BUILDER.define("blaze_burner_fading", 580.0f);
            BLAZE_BURNER_KINDLED = BUILDER.define("blaze_burner_kindled", 930.0f);
            BLAZE_BURNER_SEETHING = BUILDER.define("blaze_burner_seething", 1600.0f);

        BUILDER.pop();

        BUILDER.push("stress");
            BUILDER.comment("Stress impact of the transformer");
            CT_TRANSFORMER_IMPACT = BUILDER.define("ct_transformer_impact", 64);
            BUILDER.comment("Stress generation factors, this values are multiplied");
            WOODEN_GENERATOR_BASE_SU = BUILDER.define("wooden_generator_base_su", 64);
            WOODEN_GENERATOR_WIND_FACTOR = BUILDER.define("wooden_generator_wind_factor", 4);
            WOODEN_GENERATOR_SPEED_FACTOR = BUILDER.define("wooden_generator_speed_factor", 16);
        BUILDER.pop();

        NETHERITE_RESKIN = BUILDER.comment("Change netherite divingGear to look like red steel armor. (wearing netherite pants disables re-skin)").define("netherite_reskin", true);

        BUILDER.comment("Density [kg/m3] - HeatCapacity [J/(kg∙K)]").push("materials");
        addDensityConfig("allthemodium",20000,1500); //Not real
        addDensityConfig("aluminium",2700,897);
        addDensityConfig("aluminum",2700,897);
        addDensityConfig("americium",13670,116);
        addDensityConfig("annealed_copper",8940,385);
        addDensityConfig("antimony",6697,210);
        addDensityConfig("beryllium",1848,1825);
        addDensityConfig("bisalloy_400",7850,466);
        addDensityConfig("blasted_iron",7870,449); //~~
        addDensityConfig("blue_alloy",4000,300); //Not real
        addDensityConfig("borosilicate_glass",2230,830);
        addDensityConfig("chrome",7140,450);
        addDensityConfig("chromium",7140,450);
        addDensityConfig("cobalt",8900,420);
        addDensityConfig("cobalt_brass",8900,420); //~~
        addDensityConfig("conductive_alloy",2900,600); //Not real
        addDensityConfig("constantan",8900,377);
        addDensityConfig("copper_alloy",8800,385); //~~
        addDensityConfig("cupronickel",8900,385);
        addDensityConfig("damascus_steel",7850,466); //~~
        addDensityConfig("dark_chocolate",1325,2600);
        addDensityConfig("dark_steel",7850,466); //~~
        addDensityConfig("darmstadtium",26500,130); //Theoretic - N/A
        addDensityConfig("duranium",2800,897); //Not real
        addDensityConfig("electrum",2900,700); //Not real
        addDensityConfig("end_steel",6700,530); //Not real
        addDensityConfig("enderium",3000,100); //Not real
        addDensityConfig("energetic_alloy",2700,500); //Not real
        addDensityConfig("enriched_naquadah",32400,400); //Not real
        addDensityConfig("enriched_naquadah_trinium_europium_duranide",34000,200); //Not real
        addDensityConfig("epoxy",1250,1100);
        addDensityConfig("europium",5244,180);
        addDensityConfig("fiberglass",2600,787);
        addDensityConfig("fluorite",3180,867);
        addDensityConfig("gallium",5904,371);
        addDensityConfig("gallium_arsenide",5317,330);
        addDensityConfig("graphene",1800,710); //??
        addDensityConfig("graphite",1600,710);
        addDensityConfig("hastelloy_c_276",8890,380); //~~
        addDensityConfig("hastelloy_x",8220,380); //~~
        addDensityConfig("incoloy_ma_956",7250,460); //~~
        addDensityConfig("indium",7290,234);
        addDensityConfig("indium_gallium_phosphide",4810,430);//~~
        addDensityConfig("indium_tin_barium_titanium_cuprate",5500,500); //??
        addDensityConfig("infinity_matter",82000,3400); //Not real
        addDensityConfig("invar",8050,280);
        addDensityConfig("iridium",22562,130);
        addDensityConfig("iron",7870,449);
        addDensityConfig("kanthal",7100,460);
        addDensityConfig("lead",11300,127);
        addDensityConfig("lumium",4300,129); //Not real
        addDensityConfig("magnalium",1900,962); //??
        addDensityConfig("manganese",7430,480);
        addDensityConfig("molybdenum",10280,250);
        addDensityConfig("naquadah",32500,450); //Not real
        addDensityConfig("naquadah_alloy",32100,390); //Not real
        addDensityConfig("nickel",8900,445);
        addDensityConfig("niobium",8570,265);
        addDensityConfig("nitinol",6450,322);
        addDensityConfig("osmium",22587,130);
        addDensityConfig("palladium",12023,244);
        addDensityConfig("platinum",21400,130);
        addDensityConfig("plutonium",19816,35);
        addDensityConfig("red_alloy",4000,300); //Not real
        addDensityConfig("silicon",2330,700);
        addDensityConfig("stainless_steel",8000,466); //~~
        addDensityConfig("tantalum",16650,140);
        addDensityConfig("tin",7400,228);
        addDensityConfig("titanium",4500,520);
        addDensityConfig("tungsten",19250,130);
        addDensityConfig("wolframium",19250,130);
        addDensityConfig("uranium",19050,120);
        addDensityConfig("vibranium", 2566,1600); //Not real
        addDensityConfig("wrought_iron",7870,449);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private static void addDensityConfig(String itemId, int density, int heatCapacity) {
        MATERIAL_PROPERTIES.put(itemId, BUILDER.define(itemId, Arrays.asList(density,heatCapacity)));
    }

    public static void register(FMLJavaModLoadingContext ctx) {
        ctx.registerConfig(ModConfig.Type.COMMON, WoodenCogCommonConfigs.SPEC, "woodencog-common.toml");
    }
}

