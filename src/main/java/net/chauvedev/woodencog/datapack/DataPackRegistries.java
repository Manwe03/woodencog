package net.chauvedev.woodencog.datapack;

import com.mojang.serialization.Codec;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.utils.CogUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.*;

public class DataPackRegistries {
    public static final ResourceLocation BLOCK_TEMPERATURE_LOCATION = WoodenCog.asResource("block_temperature");
    public static final ResourceLocation ITEM_TEMPERATURE_BLACKLIST_LOCATION = WoodenCog.asResource("item_temperature_blacklist");

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
        HolderSet<Item> blacklist = registryAccess.registryOrThrow(DataPackRegistries.TEMPERATURE_BLACKLIST).get(DataPackRegistries.ITEM_TEMPERATURE_BLACKLIST_LOCATION);
        if(CogUtil.logConditional(blacklist == null, DataPackRegistries.class,"temperature blacklist file not found, (nothing is blacklisted)")) return false;
        return blacklist.contains(inputStack.getItemHolder());
    }
}
