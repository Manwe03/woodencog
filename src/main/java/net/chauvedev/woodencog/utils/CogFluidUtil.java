package net.chauvedev.woodencog.utils;

import net.dries007.tfc.util.Metal;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

public class CogFluidUtil {

    public static FluidStack fluidStack(Metal metal, int amount){
        return new FluidStack(BuiltInRegistries.FLUID.get(TFCMetalRS(metal)), amount);
    }

    public static SizedFluidIngredient ingredient(Metal metal, int amount){
        return SizedFluidIngredient.of(fluidStack(metal, amount));
    }

    public static FluidStack fluidStack(Fluid fluid, int amount){
        return new FluidStack(fluid, amount);
    }

    public static SizedFluidIngredient ingredient(Fluid fluid, int amount){
        return SizedFluidIngredient.of(fluidStack(fluid, amount));
    }

    private static ResourceLocation TFCMetalRS(Metal metalEnum) {
        return ResourceLocation.tryBuild("tfc","metal/"+metalEnum.getSerializedName());
    }
}
