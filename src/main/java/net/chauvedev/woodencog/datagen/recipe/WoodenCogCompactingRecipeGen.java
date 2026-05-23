package net.chauvedev.woodencog.datagen.recipe;

import com.simibubi.create.api.data.recipe.CompactingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;

public class WoodenCogCompactingRecipeGen extends CompactingRecipeGen {
    public WoodenCogCompactingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider>registries) {
        super(output, registries, WoodenCog.MOD_ID);

        TFCItems.UNFIRED_MOLDS.forEach(this::tfcCompactingMolds);
        TFCBlocks.ROCK_BLOCKS.keySet().forEach(this::createCompactingRawRock);
        this.createCompactingVanillaRock();
        this.unfiredRecipes();
    }

    private void tfcCompactingMolds(Metal.ItemType itemType, TFCItems.ItemId moldId){
        if(!itemType.hasMold()) return;

        Item unfiredMold = moldId.get();

        create(WoodenCog.asWoodencogResource(unfiredMold), b ->
            b.require(TFCTags.Items.CLAY_KNAPPING)
                .require(TFCTags.Items.CLAY_KNAPPING)
                .require(TFCTags.Items.CLAY_KNAPPING)
                .require(TFCTags.Items.CLAY_KNAPPING)
                .output(unfiredMold,1));
    }

    private void createCompactingRawRock(Rock rock) {
        Item looseRock = getLooseRock(rock);
        Block rawRock = getBlock(rock,Rock.BlockType.RAW);
        create(rock.getSerializedName() + "_from_loose_rock", b ->
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

    private void unfiredRecipes(){
        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_BELL_MOLD.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_BELL_MOLD.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_BLOWPIPE.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_BLOWPIPE.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_BOWL.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_BOWL.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_BRICK.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_BRICK.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_CRUCIBLE.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_CRUCIBLE.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_FLOWER_POT.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_FLOWER_POT.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_JUG.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_JUG.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_LARGE_VESSEL.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_LARGE_VESSEL.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_PAN.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_PAN.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_POT.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_POT.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_SPINDLE_HEAD.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_SPINDLE_HEAD.get(),1));

        create(WoodenCog.asWoodencogResource(TFCItems.UNFIRED_VESSEL.get()), b ->
                b.require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .require(TFCTags.Items.CLAY_KNAPPING)
                        .output(TFCItems.UNFIRED_VESSEL.get(),1));
    }

    private Block getBlock(Rock rock, Rock.BlockType type) {
        return TFCBlocks.ROCK_BLOCKS.get(rock).get(type).get();
    }

    private Item getLooseRock(Rock rock) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath(TerraFirmaCraft.MOD_ID,"rock/loose/"+rock.getSerializedName()));
    }

}
