package net.chauvedev.woodencog.utils;

import com.simibubi.create.Create;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class CreateBlocksAccess {
    public static final Block MECHANICAL_MIXER = BuiltInRegistries.BLOCK.get(Create.asResource("mechanical_mixer"));
    public static final Block MECHANICAL_PRESS = BuiltInRegistries.BLOCK.get(Create.asResource("mechanical_press"));
    public static final Block GEARBOX = BuiltInRegistries.BLOCK.get(Create.asResource("gearbox"));
    public static final Block DEPOT = BuiltInRegistries.BLOCK.get(Create.asResource("depot"));
    public static final Block BASIN = BuiltInRegistries.BLOCK.get(Create.asResource("basin"));
}
