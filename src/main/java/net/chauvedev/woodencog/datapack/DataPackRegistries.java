package net.chauvedev.woodencog.datapack;

import com.mojang.serialization.Codec;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.Map;

public class DataPackRegistries {
    public static ResourceLocation BLOCK_TEMPERATURE_LOCATION = WoodenCog.asResource("block_temperature");

    public static final ResourceKey<Registry<Map<ResourceLocation, Integer>>> TEMPERATURE_KEY =
            ResourceKey.createRegistryKey(WoodenCog.asResource("temperature"));

    public static void register(DataPackRegistryEvent.NewRegistry event) {

        Codec<Map<ResourceLocation, Integer>> TEMPERATURE_MAP_CODEC =
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT);

        event.dataPackRegistry(
                DataPackRegistries.TEMPERATURE_KEY,
                TEMPERATURE_MAP_CODEC,
                TEMPERATURE_MAP_CODEC
        );

    }
}
