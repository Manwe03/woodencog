package net.chauvedev.woodencog.utils;

import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateBlocksAccess {
    public static Block MECHANICAL_MIXER = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Create.ID,"mechanical_mixer"));
    public static Block MECHANICAL_PRESS = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Create.ID,"mechanical_press"));
    public static Block GEARBOX = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Create.ID,"gearbox"));
    public static Block DEPOT = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Create.ID,"depot"));
    public static Block BASIN = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(Create.ID,"basin"));
}
