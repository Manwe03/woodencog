package net.chauvedev.woodencog.utils;

import com.simibubi.create.Create;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateBlocksAccess {
    public static final Block MECHANICAL_MIXER = ForgeRegistries.BLOCKS.getValue(Create.asResource("mechanical_mixer"));
    public static final Block MECHANICAL_PRESS = ForgeRegistries.BLOCKS.getValue(Create.asResource("mechanical_press"));
    public static final Block GEARBOX = ForgeRegistries.BLOCKS.getValue(Create.asResource("gearbox"));
    public static final Block DEPOT = ForgeRegistries.BLOCKS.getValue(Create.asResource("depot"));
    public static final Block BASIN = ForgeRegistries.BLOCKS.getValue(Create.asResource("basin"));
}
