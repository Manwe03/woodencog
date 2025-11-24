package net.chauvedev.woodencog.utils;

import com.simibubi.create.Create;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class CreateBlocksAccess {
    public static final Block MECHANICAL_MIXER = CogUtil.findNotNullBlock(Create.asResource("mechanical_mixer"));
    public static final Block MECHANICAL_PRESS = CogUtil.findNotNullBlock(Create.asResource("mechanical_press"));
    public static final Block GEARBOX = CogUtil.findNotNullBlock(Create.asResource("gearbox"));
    public static final Block DEPOT = CogUtil.findNotNullBlock(Create.asResource("depot"));
    public static final Block BASIN = CogUtil.findNotNullBlock(Create.asResource("basin"));
}
