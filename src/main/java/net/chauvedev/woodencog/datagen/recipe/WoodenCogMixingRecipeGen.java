package net.chauvedev.woodencog.datagen.recipe;

import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.utils.ModTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

public class WoodenCogMixingRecipeGen extends MixingRecipeGen {
    public WoodenCogMixingRecipeGen(PackOutput output) {
        super(output, WoodenCog.MOD_ID);

        TFCFluids.COLORED_FLUIDS.keySet().forEach(this::dyeing);
    }

    GeneratedRecipe

    LIMEWATER_FROM_LIME = create("limewater_from_lime", b ->
            b.require(Fluids.WATER, 500)
            .require(getPowderItem(Powder.LIME))
            .output(getSimpleFluid(SimpleFluid.LIMEWATER), 500)),

    LIMEWATER_FROM_FLUX = create("limewater_from_flux", b ->
            b.require(Fluids.WATER, 500)
            .require(getPowderItem(Powder.FLUX))
            .output(getSimpleFluid(SimpleFluid.LIMEWATER), 500)),

    LYE = create("lye", b ->
            b.require(Fluids.WATER, 1000)
            .require(getPowderItem(Powder.WOOD_ASH))
            .require(getPowderItem(Powder.WOOD_ASH))
            .require(getPowderItem(Powder.WOOD_ASH))
            .require(getPowderItem(Powder.WOOD_ASH))
            .require(getPowderItem(Powder.WOOD_ASH))
            .requiresHeat(HeatCondition.HEATED)
            .output(getSimpleFluid(SimpleFluid.LYE), 1000)),

    LYE_RAW_ALABASTER = create("lye_raw_alabaster", b ->
            b.require(getSimpleFluid(SimpleFluid.LYE), 25)
            .require(ModTags.Items.COLORED_RAW_ALABASTER)
            .output(TFCBlocks.PLAIN_ALABASTER.get())
            .duration(600)),

    LYE_ALABASTER_BRICKS = create("lye_alabaster_bricks", b ->
            b.require(getSimpleFluid(SimpleFluid.LYE), 25)
            .require(ModTags.Items.COLORED_BRICKS_ALABASTER)
            .output(TFCBlocks.PLAIN_ALABASTER_BRICKS.get())
            .duration(600)),

    LYE_POLISHED_ALABASTER = create("lye_polished_alabaster", b ->
            b.require(getSimpleFluid(SimpleFluid.LYE), 25)
            .require(ModTags.Items.COLORED_POLISHED_ALABASTER)
            .output(TFCBlocks.PLAIN_POLISHED_ALABASTER.get())
            .duration(600));

    private void dyeing(DyeColor dyeColor){
        create("liquid_"+dyeColor.getName()+"_dye", b ->
                b.require(Fluids.WATER, 500)
                        .require(dyeColor.getTag())
                        .requiresHeat(HeatCondition.HEATED)
                        .output(TFCFluids.COLORED_FLUIDS.get(dyeColor).getSource(), 500));

        create("dyeing_"+dyeColor.getName()+"_raw_alabaster", b ->
                b.require(TFCFluids.COLORED_FLUIDS.get(dyeColor).getSource(), 25)
                        .require(TFCBlocks.PLAIN_ALABASTER.get())
                        .output(TFCBlocks.RAW_ALABASTER.get(dyeColor).get())
                        .duration(600));

        create("dyeing_"+dyeColor.getName()+"_alabaster_bricks", b ->
                b.require(TFCFluids.COLORED_FLUIDS.get(dyeColor).getSource(), 25)
                        .require(TFCBlocks.PLAIN_ALABASTER_BRICKS.get())
                        .output(TFCBlocks.ALABASTER_BRICKS.get(dyeColor).get())
                        .duration(600));

        create("dyeing_"+dyeColor.getName()+"_polished_alabaster", b ->
                b.require(TFCFluids.COLORED_FLUIDS.get(dyeColor).getSource(), 25)
                        .require(TFCBlocks.PLAIN_POLISHED_ALABASTER.get())
                        .output(TFCBlocks.POLISHED_ALABASTER.get(dyeColor).get())
                        .duration(600));
    }

    private Item getPowderItem(Powder powder) {
        return TFCItems.POWDERS.get(powder).get();
    }

    private Fluid getSimpleFluid(SimpleFluid fluid) {
        return TFCFluids.SIMPLE_FLUIDS.get(fluid).getSource();
    }
}
