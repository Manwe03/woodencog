package net.chauvedev.woodencog.utils;

import com.simibubi.create.Create;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateBlocksAccess {
    public static Block MECHANICAL_MIXER = ForgeRegistries.BLOCKS.getValue(Create.asResource("mechanical_mixer"));
    public static Block MECHANICAL_PRESS = ForgeRegistries.BLOCKS.getValue(Create.asResource("mechanical_press"));
    public static Block DEPOT = ForgeRegistries.BLOCKS.getValue(Create.asResource("depot"));
    public static Block BASIN = ForgeRegistries.BLOCKS.getValue(Create.asResource("basin"));
}
