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
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public class WoodenCogCompactingRecipeGen extends CompactingRecipeGen {
    public WoodenCogCompactingRecipeGen(PackOutput output) {
        super(output, WoodenCog.MOD_ID);

        TFCBlocks.ROCK_BLOCKS.keySet().forEach(this::createCompactingRawRock);
        createCompactingVanillaRock();
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

    private void createCompactingVanillaRock() {
        Item looseRock1 = getLooseRock(Rock.BASALT);
        create("vanilla_basalt_from_loose_rock", b ->
            b.require(looseRock1)
            .require(looseRock1)
            .require(looseRock1)
            .require(looseRock1)
            .requiresHeat(HeatCondition.SUPERHEATED)
            .output(Blocks.BASALT, 1));

        Item looseRock2 = getLooseRock(Rock.SLATE);
        create("vanilla_slate_from_loose_rock", b ->
            b.require(looseRock2)
            .require(looseRock2)
            .require(looseRock2)
            .require(looseRock2)
            .requiresHeat(HeatCondition.SUPERHEATED)
            .output(Blocks.DEEPSLATE, 1));
        /*
        Item looseRock3 = getLooseRock(Rock.DIORITE);
        create("vanilla_diorite_from_loose_rock", b ->
            b.require(looseRock3)
            .require(looseRock3)
            .require(looseRock3)
            .require(looseRock3)
            .requiresHeat(HeatCondition.SUPERHEATED)
            .output(Blocks.DIORITE, 1));
        */
        Item looseRock4 = getLooseRock(Rock.ANDESITE);
        create("vanilla_andesite_from_loose_rock", b ->
            b.require(looseRock4)
            .require(looseRock4)
            .require(looseRock4)
            .require(looseRock4)
            .requiresHeat(HeatCondition.SUPERHEATED)
            .output(Blocks.ANDESITE, 1));

        Item looseRock5 = getLooseRock(Rock.GRANITE);
        create("vanilla_granite_from_loose_rock", b ->
            b.require(looseRock5)
            .require(looseRock5)
            .require(looseRock5)
            .require(looseRock5)
            .requiresHeat(HeatCondition.SUPERHEATED)
            .output(Blocks.GRANITE, 1));
    }

    private Block getBlock(Rock rock, Rock.BlockType type) {
        return TFCBlocks.ROCK_BLOCKS.get(rock).get(type).get();
    }

    private Item getLooseRock(Rock rock) {
        return ForgeRegistries.ITEMS.getValue(ResourceLocation.tryBuild(TerraFirmaCraft.MOD_ID,"rock/loose/"+rock.getSerializedName()));
    }

}
