package net.chauvedev.woodencog.utils;

import net.dries007.tfc.util.Metal;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;

public class FluidAccess {

    public static Fluid copper = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.COPPER));
    public static Fluid bismuth = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.BISMUTH));
    public static Fluid zinc = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.ZINC));
    public static Fluid bismuthBronze = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.BISMUTH_BRONZE));
    public static Fluid silver = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.SILVER));
    public static Fluid gold = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.GOLD));
    public static Fluid blackBronze = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.BLACK_BRONZE));
    public static Fluid brass = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.BRASS));
    public static Fluid tin = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.TIN));
    public static Fluid bronze = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.BRONZE));
    public static Fluid roseGold = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.ROSE_GOLD));
    public static Fluid sterlingSilver = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.STERLING_SILVER));
    public static Fluid blackSteel = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.BLACK_STEEL));
    public static Fluid steel = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.STEEL));
    public static Fluid weakBlueSteel = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.WEAK_BLUE_STEEL));
    public static Fluid weakRedSteel = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.WEAK_RED_STEEL));
    public static Fluid nickel = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.NICKEL));
    public static Fluid weakSteel = CogUtil.findNotNullFluid(TFCMetalRS(Metal.Default.WEAK_STEEL));

    private static ResourceLocation TFCMetalRS(Metal.Default metalEnum) {
        return ResourceLocation.tryBuild("tfc","metal/"+metalEnum.getSerializedName());
    }
}
