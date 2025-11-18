package net.chauvedev.woodencog.utils;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.TerraFirmaCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;

import java.util.Objects;

public class ModTags {
    public static class Compat {
        public static final TagKey<Item> BREADS = TagKey.create(Registries.ITEM, Objects.requireNonNull(ResourceLocation.tryBuild("tfc", "foods/breads")));
    }

    public static class Blocks {
        private static TagKey<Block> modTag(String name) {
            return BlockTags.create(WoodenCog.asResource(name));
        }

        private static TagKey<Block> forgeTag(String name) {
            ResourceLocation tagRS = ResourceLocation.tryBuild("forge", name);
            return tagRS != null ? BlockTags.create(tagRS) : null;
        }
    }

    public static class Items {
        public static final TagKey<Item> UNBURNABLE = modTag("unburnable");
        public static final TagKey<Item> CHAINS = forgeTag("chains");

        public static TagKey<Item> COLORED_RAW_ALABASTER = tfcTag("colored_raw_alabaster");
        public static TagKey<Item> COLORED_BRICKS_ALABASTER = tfcTag("colored_bricks_alabaster");
        public static TagKey<Item> COLORED_POLISHED_ALABASTER = tfcTag("colored_polished_alabaster");

        private static TagKey<Item> modTag(String name) {
            ResourceLocation tagRS = ResourceLocation.tryBuild(WoodenCog.MOD_ID, name);
            return tagRS != null ? ItemTags.create(tagRS) : null;
        }

        private static TagKey<Item> tfcTag(String name) {
            ResourceLocation tagRS = ResourceLocation.tryBuild(TerraFirmaCraft.MOD_ID, name);
            return tagRS != null ? ItemTags.create(tagRS) : null;
        }

        private static TagKey<Item> forgeTag(String name) {
            ResourceLocation tagRS = ResourceLocation.tryBuild("forge", name);
            return tagRS != null ? ItemTags.create(tagRS) : null;
        }
    }
}