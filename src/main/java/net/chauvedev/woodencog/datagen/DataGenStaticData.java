package net.chauvedev.woodencog.datagen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataGenStaticData {

    public static final Map<String,Metal> METAL_REGISTRY = new HashMap<>();

    public static Metal BISMUTH = new Metal("bismuth", 162,216,270, 9780, 112, true);
    public static Metal BISMUTH_BRONZE = new Metal("bismuth_bronze", 162, 216,985, 8885, 335, true);
    public static Metal BLACK_BRONZE = new Metal("black_bronze", 642, 856,1070, 7717, 435, true);
    public static Metal BRONZE = new Metal("bronze", 570, 760,950, 7717, 435, true);
    public static Metal BRASS = new Metal("brass",  558, 744,930, 8890, 401, true);
    public static Metal COPPER = new Metal("copper", 648, 864,1080, 8800, 385, true);
    public static Metal GOLD = new Metal("gold", 636, 848,1060, 19300, 129, true);
    public static Metal NICKEL = new Metal("nickel", 872, 1162,1453, 8900, 445, true);
    public static Metal ROSE_GOLD = new Metal("rose_gold", 576, 768,960, 19300, 129, true);
    public static Metal SILVER = new Metal("silver", 577, 769,961, 10500, 232, true);
    public static Metal TIN = new Metal("tin", 138, 184,230, 7400, 228, true);
    public static Metal ZINC = new Metal("zinc", 252, 336,420, 7138, 390, true);
    public static Metal STERLING_SILVER = new Metal("sterling_silver", 570, 760,950, 10490, 249, true);
    public static Metal WROUGHT_IRON = new Metal("wrought_iron", 921, 1228,1535, 7870, 449, true);
    public static Metal CAST_IRON = new Metal("cast_iron", 921, 1228,1535, 7870, 449, true);
    public static Metal PIG_IRON = new Metal("pig_iron", 921, 1228,1535, 7870, 449, false);
    public static Metal STEEL = new Metal("steel", 924, 1232,1540, 7850, 466, true);
    public static Metal BLACK_STEEL = new Metal("black_steel", 891, 1188,1485, 7850, 466, true);
    public static Metal BLUE_STEEL = new Metal("blue_steel", 924, 1232,1540, 7850, 466, true);
    public static Metal RED_STEEL = new Metal("red_steel", 924, 1232,1540, 7850, 466, true);
    public static Metal WEAK_STEEL = new Metal("weak_steel", 924, 1232,1540, 7850, 466, false);
    public static Metal WEAK_BLUE_STEEL = new Metal("weak_blue_steel", 924, 1232,1540, 7850, 466, false);
    public static Metal WEAK_RED_STEEL = new Metal("weak_red_steel", 924, 1232,1540, 7850, 466, false);
    public static Metal HIGH_CARBON_STEEL = new Metal("high_carbon_steel", 924, 1232,1540, 7850, 466, false);
    public static Metal HIGH_CARBON_BLACK_STEEL = new Metal("high_carbon_black_steel", 924, 1232,1540, 7850, 466, false);
    public static Metal HIGH_CARBON_BLUE_STEEL = new Metal("high_carbon_blue_steel", 924, 1232,1540, 7850, 466, false);
    public static Metal HIGH_CARBON_RED_STEEL = new Metal("high_carbon_red_steel", 924, 1232,1540, 7850, 466, false);

    public static final List<Ore> ORE_REGISTRY = new ArrayList<>();

    public static Ore BISMUTHINITE = new Ore("bismuthinite",270,"bismuth");
    public static Ore CASSITERITE = new Ore("cassiterite",230,"tin");
    public static Ore GARNIERITE = new Ore("garnierite",1453,"nickel");
    public static Ore HEMATITE = new Ore("hematite",1535,"cast_iron");
    public static Ore LIMONITE = new Ore("limonite",1535,"cast_iron");
    public static Ore MAGNETITE = new Ore("magnetite",1535,"cast_iron");
    public static Ore MALACHITE = new Ore("malachite",1080,"copper");
    public static Ore NATIVE_COPPER = new Ore("native_copper",1080,"copper");
    public static Ore NATIVE_GOLD = new Ore("native_gold",1060,"gold");
    public static Ore NATIVE_SILVER = new Ore("native_silver",961,"silver");
    public static Ore SPHALERITE = new Ore("sphalerite",420,"zinc");
    public static Ore TETRAHEDRITE = new Ore("tetrahedrite",1080,"copper");

    public record Ore(String oreId, int meltTemperature, String metalId) {
            public Ore(String oreId, int meltTemperature, String metalId) {
                this.oreId = oreId;
                this.meltTemperature = meltTemperature;
                this.metalId = metalId;

                ORE_REGISTRY.add(this);
            }
        }

    public static class Metal {
        private final String name;
        private final int forginTemperature;
        private final int weldingTemperature;
        private final int meltTemperature;
        private final int density;
        private final int heatCapacity;
        private final boolean doubleIngot;

        private Metal(String name, int forginTemperature, int weldingTemperature, int meltTemperature, int density, int heatCapacity, boolean doubleIngot) {
            this.name = name;
            this.forginTemperature = forginTemperature;
            this.weldingTemperature = weldingTemperature;
            this.meltTemperature = meltTemperature;
            this.density = density;
            this.heatCapacity = heatCapacity;
            this.doubleIngot = doubleIngot;

            METAL_REGISTRY.put(name,this);
        }

        public String id() {
            return name;
        }

        public int getMeltTemperature() {
            return meltTemperature;
        }

        public int getWeldingTemperature() {
            return weldingTemperature;
        }

        public int getForginTemperature() {
            return forginTemperature;
        }

        public int getDensity() {
            return density;
        }

        public int getHeatCapacity() {
            return heatCapacity;
        }

        public boolean hasDoubleIngot() {
            return doubleIngot;
        }
    }
}
