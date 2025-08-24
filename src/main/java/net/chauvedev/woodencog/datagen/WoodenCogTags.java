package net.chauvedev.woodencog.datagen;

import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class WoodenCogTags {

    public static TagKey<Item> COLORED_RAW_ALABASTER = create("colored_raw_alabaster");
    public static TagKey<Item> COLORED_BRICKS_ALABASTER = create("colored_bricks_alabaster");
    public static TagKey<Item> COLORED_POLISHED_ALABASTER = create("colored_polished_alabaster");

    private static TagKey<Item> create(String id) {
        return TagKey.create(Registries.ITEM, new ResourceLocation(TerraFirmaCraft.MOD_ID,id));
    }
}
