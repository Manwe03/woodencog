package net.chauvedev.woodencog.datagen.recipe;

import com.simibubi.create.api.data.recipe.CrushingRecipeGen;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class WoodenCogCrushingRecipeGen extends CrushingRecipeGen {
    public WoodenCogCrushingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, WoodenCog.MOD_ID);

        TFCBlocks.ROCK_BLOCKS.keySet().forEach(this::crushingRawRock);
    }

    private void crushingRawRock(Rock rock) {
        Block rockRaw = getRockBlock(rock,Rock.BlockType.RAW);
        Block rockCobble = getRockBlock(rock,Rock.BlockType.COBBLE);
        Block rockGravel = getRockBlock(rock,Rock.BlockType.GRAVEL);

        switch (rock){
            case DIORITE -> create(() -> rockRaw, b -> b.duration(160)
                    .output(.5f, rockCobble)
                    .output(.03f, Items.QUARTZ));
            case MARBLE -> create(() -> rockRaw, b -> b.duration(160)
                    .output(.5f, rockCobble)
                    .output(.05f, Items.QUARTZ));
            case QUARTZITE -> create(() -> rockRaw, b -> b.duration(160)
                    .output(.5f, rockCobble)
                    .output(.1f, Items.QUARTZ));
            case LIMESTONE -> create(() -> rockRaw, b -> b.duration(160)
                    .output(.5f, rockCobble)
                    .output(.7f, getOreItem(Ore.GYPSUM)));
            default -> create(() -> rockRaw, b -> b.duration(160)
                    .output(.9f, rockCobble));
        }

        create(() -> rockCobble, b -> b.duration(160).output(rockGravel));
    }

    private Block getRockBlock(Rock rock, Rock.BlockType type) {
        return TFCBlocks.ROCK_BLOCKS.get(rock).get(type).get();
    }

    private Item getOreItem(Ore ore) {
        return TFCItems.ORES.get(ore).get();
    }
}
