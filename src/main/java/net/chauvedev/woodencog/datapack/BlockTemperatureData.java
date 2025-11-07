package net.chauvedev.woodencog.datapack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record BlockTemperatureData(ResourceLocation block, float temperature) {
    public static final Codec<BlockTemperatureData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("block").forGetter(BlockTemperatureData::block),
            Codec.FLOAT.fieldOf("temperature").forGetter(BlockTemperatureData::temperature)
    ).apply(instance, BlockTemperatureData::new));
}


