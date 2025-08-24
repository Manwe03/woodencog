package net.chauvedev.woodencog.datagen.recipe;

import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class WoodenCogCompactingRecipeGen extends CompactingRecipeGen {
    public WoodenCogCompactingRecipeGen(PackOutput output) {
        super(output, WoodenCog.MOD_ID);

        TFCBlocks.ROCK_BLOCKS.keySet().forEach(this::createCompactingRawRock);
    }

    private GeneratedRecipe createCompactingRawRock(Rock rock) {
        Item looseRock = getLooseRock(rock);
        Block rawRock = getBlock(rock,Rock.BlockType.RAW);
        return create(rock.getSerializedName()+"_from_loose_rock", b ->
            b.require(looseRock)
            .require(looseRock)
            .require(looseRock)
            .require(looseRock)
            .requiresHeat(HeatCondition.HEATED)
            .output(rawRock, 1));
    }

    private Block getBlock(Rock rock, Rock.BlockType type) {
        return TFCBlocks.ROCK_BLOCKS.get(rock).get(type).get();
    }

    private Item getLooseRock(Rock rock) {
        return ForgeRegistries.ITEMS.getValue(new ResourceLocation(TerraFirmaCraft.MOD_ID,"rock/loose/"+rock.getSerializedName()));
    }

}
