package net.chauvedev.woodencog.datapack;

import com.mojang.serialization.Codec;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.*;
import java.util.stream.Collectors;

public class DataPackRegistries {
    public static ResourceLocation BLOCK_TEMPERATURE_LOCATION = WoodenCog.asResource("block_temperature");
    public static ResourceLocation ITEM_TEMPERATURE_BLACKLIST_LOCATION = WoodenCog.asResource("item_temperature_blacklist");

    public static final ResourceKey<Registry<Map<ResourceLocation, Integer>>> TEMPERATURE_KEY =
            ResourceKey.createRegistryKey(WoodenCog.asResource("temperature"));

    public static final ResourceKey<Registry<HolderSet<Item>>> TEMPERATURE_BLACKLIST =
            ResourceKey.createRegistryKey(WoodenCog.asResource("item_blacklist"));

    public static void register(DataPackRegistryEvent.NewRegistry event) {

        Codec<Map<ResourceLocation, Integer>> TEMPERATURE_MAP_CODEC =
                Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT);
        event.dataPackRegistry(
                DataPackRegistries.TEMPERATURE_KEY,
                TEMPERATURE_MAP_CODEC,
                TEMPERATURE_MAP_CODEC
        );

        Codec<HolderSet<Item>> TEMPERATURE_BLACKLIST_CODEC =
                RegistryCodecs.homogeneousList(Registries.ITEM);

        event.dataPackRegistry(
                DataPackRegistries.TEMPERATURE_BLACKLIST,
                TEMPERATURE_BLACKLIST_CODEC,
                TEMPERATURE_BLACKLIST_CODEC
        );
    }

    public static boolean isInTempBlacklist(ItemStack inputStack, RegistryAccess registryAccess){
        return registryAccess.registryOrThrow(DataPackRegistries.TEMPERATURE_BLACKLIST).get(DataPackRegistries.ITEM_TEMPERATURE_BLACKLIST_LOCATION).contains(inputStack.getItemHolder());
    }
}
