package net.chauvedev.woodencog.datagen.recipe;

import com.simibubi.create.api.data.recipe.MixingRecipeGen;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.utils.ModTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.fluids.SimpleFluid;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.concurrent.CompletableFuture;

public class WoodenCogMixingRecipeGen extends MixingRecipeGen {
    public WoodenCogMixingRecipeGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, WoodenCog.MOD_ID);

        this.barrelRecipes();
        TFCFluids.COLORED_FLUIDS.keySet().forEach(this::dyeing);
    }

    private void barrelRecipes(){
        GeneratedTFCBarrelMixingData.RECIPES.forEach(barrelRecipe -> {

            GeneratedTFCBarrelMixingData.ItemInput inputItem = barrelRecipe.inputItem();
            SizedFluidIngredient fluidIngredient1 = barrelRecipe.inputFluid();
            SizedFluidIngredient fluidIngredient2 = barrelRecipe.addedFluid();

            GeneratedTFCBarrelMixingData.FluidResult outputFluid = barrelRecipe.outputFluid();
            GeneratedTFCBarrelMixingData.ItemResult outputItem = barrelRecipe.outputItem();

            create(barrelRecipe.id(), b -> {
                if (inputItem != null && inputItem.tag() != null) {
                    b.require(inputItem.tag());
                }
                if(inputItem != null && !inputItem.items().isEmpty()){
                    for (ItemLike input : inputItem.items()){
                        b.require(input);
                    }
                }
                if (fluidIngredient1 != null) {
                    b.require(fluidIngredient1);
                }
                if (fluidIngredient2 != null) {
                    b.require(fluidIngredient2);
                }
                if (outputFluid != null) {
                    b.output(outputFluid.fluid(),outputFluid.amount());
                }
                if (outputItem != null) {
                    b.output(outputItem.item(),outputItem.count());
                }
                return b.duration(barrelRecipe.processingTime()*20);
            });
        });
    }

    GeneratedRecipe

    LIMEWATER_FROM_LIME = create("limewater_from_lime", b ->
            b.require(Fluids.WATER, 500)
            .require(getPowderItem(Powder.LIME))
            .output(getFlowingFluid(SimpleFluid.LIMEWATER), 500)),

    LIMEWATER_FROM_FLUX = create("limewater_from_flux", b ->
            b.require(Fluids.WATER, 500)
            .require(getPowderItem(Powder.FLUX))
            .output(getFlowingFluid(SimpleFluid.LIMEWATER), 500)),

    LYE = create("lye", b ->
            b.require(Fluids.WATER, 1000)
            .require(getPowderItem(Powder.WOOD_ASH))
            .require(getPowderItem(Powder.WOOD_ASH))
            .require(getPowderItem(Powder.WOOD_ASH))
            .require(getPowderItem(Powder.WOOD_ASH))
            .require(getPowderItem(Powder.WOOD_ASH))
            .requiresHeat(HeatCondition.HEATED)
            .output(getFlowingFluid(SimpleFluid.LYE), 1000)),

    LYE_RAW_ALABASTER = create("lye_raw_alabaster", b ->
            b.require(getFlowingFluid(SimpleFluid.LYE), 25)
            .require(ModTags.Items.COLORED_RAW_ALABASTER)
            .output(TFCBlocks.PLAIN_ALABASTER.get())
            .duration(600)),

    LYE_ALABASTER_BRICKS = create("lye_alabaster_bricks", b ->
            b.require(getFlowingFluid(SimpleFluid.LYE), 25)
            .require(ModTags.Items.COLORED_BRICKS_ALABASTER)
            .output(TFCBlocks.PLAIN_ALABASTER_BRICKS.get())
            .duration(600)),

    LYE_POLISHED_ALABASTER = create("lye_polished_alabaster", b ->
            b.require(getFlowingFluid(SimpleFluid.LYE), 25)
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

    private FlowingFluid getFlowingFluid(SimpleFluid fluid) {
        return TFCFluids.SIMPLE_FLUIDS.get(fluid).getSource();
    }
}
