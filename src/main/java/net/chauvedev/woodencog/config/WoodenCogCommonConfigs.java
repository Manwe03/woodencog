package net.chauvedev.woodencog.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
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
    public static final Map<String, ForgeConfigSpec.ConfigValue<List<Double>>> MATERIAL_PROPERTIES = new HashMap<>();

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

        BUILDER.comment("Density [kg/m3] - HeatCapacity [J/(kg∙K)]").push("materials");
        addDensityConfig("forge:ingots/allthemodium",20000,1500); //Not real
        addDensityConfig("forge:ingots/aluminium",2700,897);
        addDensityConfig("forge:ingots/aluminum",2700,897);
        addDensityConfig("forge:ingots/americium",13670,116);
        addDensityConfig("forge:ingots/annealed_copper",8940,385);
        addDensityConfig("forge:ingots/antimony",6697,210);
        addDensityConfig("forge:ingots/beryllium",1848,1825);
        addDensityConfig("forge:ingots/bisalloy_400",7850,466);
        addDensityConfig("forge:ingots/bismuth",9780,112);
        addDensityConfig("forge:ingots/bismuth_bronze",8885,335);
        addDensityConfig("forge:ingots/black_bronze",7717,435); //~~
        addDensityConfig("forge:ingots/black_steel",7850,466); //~~
        addDensityConfig("forge:ingots/blasted_iron",7870,449); //~~
        addDensityConfig("forge:ingots/blue_alloy",4000,300); //Not real
        addDensityConfig("forge:ingots/blue_steel",7850,466);
        addDensityConfig("forge:ingots/borosilicate_glass",2230,830);
        addDensityConfig("forge:ingots/brass",8890,401);
        addDensityConfig("forge:ingots/bronze",7717,435);
        addDensityConfig("forge:ingots/cast_iron",7870,449);
        addDensityConfig("forge:ingots/chrome",7140,450);
        addDensityConfig("forge:ingots/chromium",7140,450);
        addDensityConfig("forge:ingots/cobalt",8900,420);
        addDensityConfig("forge:ingots/cobalt_brass",8900,420); //~~
        addDensityConfig("forge:ingots/conductive_alloy",2900,600); //Not real
        addDensityConfig("forge:ingots/constantan",8900,377);
        addDensityConfig("forge:ingots/copper",8800,385);
        addDensityConfig("forge:ingots/copper_alloy",8800,385); //~~
        addDensityConfig("forge:ingots/cupronickel",8900,385);
        addDensityConfig("forge:ingots/damascus_steel",7850,466); //~~
        addDensityConfig("forge:ingots/dark_chocolate",1325,2600);
        addDensityConfig("forge:ingots/dark_steel",7850,466); //~~
        addDensityConfig("forge:ingots/darmstadtium",26500,130); //Theoretic - N/A
        addDensityConfig("forge:ingots/duranium",2800,897); //Not real
        addDensityConfig("forge:ingots/electrum",2900,700); //Not real
        addDensityConfig("forge:ingots/end_steel",6700,530); //Not real
        addDensityConfig("forge:ingots/enderium",3000,100); //Not real
        addDensityConfig("forge:ingots/energetic_alloy",2700,500); //Not real
        addDensityConfig("forge:ingots/enriched_naquadah",32400,400); //Not real
        addDensityConfig("forge:ingots/enriched_naquadah_trinium_europium_duranide",34000,200); //Not real
        addDensityConfig("forge:ingots/epoxy",1250,1100);
        addDensityConfig("forge:ingots/europium",5244,180);
        addDensityConfig("forge:ingots/fiberglass",2600,787);
        addDensityConfig("forge:ingots/fluorite",3180,867);
        addDensityConfig("forge:ingots/gallium",5904,371);
        addDensityConfig("forge:ingots/gallium_arsenide",5317,330);
        addDensityConfig("forge:ingots/gold",19300,129);
        addDensityConfig("forge:ingots/graphene",1800,710); //??
        addDensityConfig("forge:ingots/graphite",1600,710);
        addDensityConfig("forge:ingots/hastelloy_c_276",8890,380); //~~
        addDensityConfig("forge:ingots/hastelloy_x",8220,380); //~~
        addDensityConfig("forge:ingots/high_carbon_black_steel",7850,466); //~~
        addDensityConfig("forge:ingots/high_carbon_blue_steel",7850,466); //~~
        addDensityConfig("forge:ingots/high_carbon_red_steel",7850,466); //~~
        addDensityConfig("forge:ingots/high_carbon_steel",7850,466); //~~
        addDensityConfig("forge:ingots/incoloy_ma_956",7250,460); //~~
        addDensityConfig("forge:ingots/indium",7290,234);
        addDensityConfig("forge:ingots/indium_gallium_phosphide",4810,430);//~~
        addDensityConfig("forge:ingots/indium_tin_barium_titanium_cuprate",5500,500); //??
        addDensityConfig("forge:ingots/infinity_matter",82000,3400); //Not real
        addDensityConfig("forge:ingots/invar",8050,280);
        addDensityConfig("forge:ingots/iridium",22562,130);
        addDensityConfig("forge:ingots/iron",7870,449);
        addDensityConfig("forge:ingots/kanthal",7100,460);
        addDensityConfig("forge:ingots/lead",11300,127);
        addDensityConfig("forge:ingots/lumium",4300,129); //Not real
        addDensityConfig("forge:ingots/magnalium",1900,962); //??
        addDensityConfig("forge:ingots/manganese",7430,480);
        addDensityConfig("forge:ingots/molybdenum",10280,250);
        addDensityConfig("forge:ingots/naquadah",32500,450); //Not real
        addDensityConfig("forge:ingots/naquadah_alloy",32100,390); //Not real
        addDensityConfig("forge:ingots/nickel",8900,445 );
        addDensityConfig("forge:ingots/niobium",8570,265);
        addDensityConfig("forge:ingots/nitinol",6450,322);
        addDensityConfig("forge:ingots/osmium",22587,130);
        addDensityConfig("forge:ingots/palladium",12023,244);
        addDensityConfig("forge:ingots/platinum",21400,130);
        addDensityConfig("forge:ingots/plutonium",19816,35);
        addDensityConfig("forge:ingots/red_alloy",4000,300); //Not real
        addDensityConfig("forge:ingots/silicon",2330,700);
        addDensityConfig("forge:ingots/silver",10500,232);
        addDensityConfig("forge:ingots/stainless_steel",8000,466); //~~
        addDensityConfig("forge:ingots/steel",7850,466);
        addDensityConfig("forge:ingots/tantalum",16650,140);
        addDensityConfig("forge:ingots/tin",7400,228);
        addDensityConfig("forge:ingots/titanium",4500,520);
        addDensityConfig("forge:ingots/tungsten",19250,130);
        addDensityConfig("forge:ingots/wolframium",19250,130);
        addDensityConfig("forge:ingots/uranium",19050,120);
        addDensityConfig("forge:ingots/vibranium", 2566,1600); //Not real
        addDensityConfig("forge:ingots/wrought_iron",7870,449);
        addDensityConfig("forge:ingots/zinc",7138,390);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    private static void addDensityConfig(String itemId, double density, double heatCapacity) {
        MATERIAL_PROPERTIES.put(itemId, BUILDER.define(itemId, Arrays.asList(density,heatCapacity)));
    }
}

