package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.chauvedev.woodencog.utils.HeatHandlingUtil;
import net.createmod.catnip.data.Iterate;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

public class HeatedBasinRecipe extends HeatedProcessingRecipe<Container> {

    public static boolean match(BasinBlockEntity basin, Recipe<?> recipe) {
        FilteringBehaviour filter = basin.getFilter();
        if (filter == null){
            WoodenCog.LOGGER.error("Filter is null");
            return false;
        }

        boolean filterTest = filter.test(recipe.getResultItem(basin.getLevel().registryAccess()));
        if (recipe instanceof HeatedBasinRecipe basinRecipe) {
            if (basinRecipe.getRollableResults()
                    .isEmpty()
                    && !basinRecipe.getFluidResults()
                    .isEmpty())
                filterTest = filter.test(basinRecipe.getFluidResults()
                        .get(0));
        }

        if (!filterTest){
            return false;
        }

        return apply(basin, recipe, true);
    }

    public static boolean apply(BasinBlockEntity basin, Recipe<?> recipe) {
        return apply(basin, recipe, false);
    }

    private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test) {
        if(recipe instanceof HeatedBasinRecipe heatedRecipe){
            IItemHandler availableItems = basin.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
            IFluidHandler availableFluids = basin.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);

            try{
                BlockEntity blockEntity = basin.getLevel().getBlockEntity(basin.getBlockPos().below(1));
                if(!heatedRecipe.getRequiredHeat().testCharcoalForge(blockEntity)) {
                    WoodenCog.LOGGER.error("Charcoal forge does not match temperature requirements");
                    return false; //Does not match required temperature
                }
            } catch (NullPointerException e){
                WoodenCog.LOGGER.error("Charcoal forge not found");
                return false; //Charcoal forge not found
            }
            //WoodenCog.LOGGER.info("Has enough heat");

            List<ItemStack> recipeOutputItems = new ArrayList<>();
            List<FluidStack> recipeOutputFluids = new ArrayList<>();

            List<HeatableIngredient> ingredients = new LinkedList<>(heatedRecipe.getHeatedIngredients());
            List<FluidIngredient> fluidIngredients = heatedRecipe.getFluidIngredients();

            for (boolean simulate : Iterate.trueAndFalse) {

                if (!simulate && test) {
                    return true;
                }

                int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
                int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

                Ingredients:
                for (HeatableIngredient ingredient : ingredients) {
                    for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                        if (simulate && availableItems.getStackInSlot(slot).getCount() <= extractedItemsFromSlot[slot]) {
                            continue;
                        }
                        ItemStack extracted = availableItems.extractItem(slot, 1, true);
                        if (!ingredient.test(extracted)) {
                            continue; //test item and item temperature
                        }
                        if (!simulate) availableItems.extractItem(slot, 1, false);
                        extractedItemsFromSlot[slot]++;
                        continue Ingredients;
                    }

                    // something wasn't found
                    return false;
                }

                boolean fluidsAffected = false;
                FluidIngredients:
                for (FluidIngredient fluidIngredient : fluidIngredients) {
                    int amountRequired = fluidIngredient.getRequiredAmount();

                    for (int tank = 0; tank < availableFluids.getTanks(); tank++) {
                        FluidStack fluidStack = availableFluids.getFluidInTank(tank);
                        if (simulate && fluidStack.getAmount() <= extractedFluidsFromTank[tank])
                            continue;
                        if (!fluidIngredient.test(fluidStack))
                            continue;
                        int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
                        if (!simulate) {
                            fluidStack.shrink(drainedAmount);
                            fluidsAffected = true;
                        }
                        amountRequired -= drainedAmount;
                        if (amountRequired != 0)
                            continue;
                        extractedFluidsFromTank[tank] += drainedAmount;
                        continue FluidIngredients;
                    }

                    // something wasn't found
                    return false;
                }

                if (fluidsAffected) {
                    basin.getBehaviour(SmartFluidTankBehaviour.INPUT)
                            .forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
                    basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT)
                            .forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
                }

                if (simulate) {
                    if (WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()) {
                        int count = 0;
                        List<ItemStack> extractedItems = new ArrayList<>();
                        for (int slot = 0; slot < availableItems.getSlots(); slot++) {
                            int extractedCount = extractedItemsFromSlot[slot];
                            if (extractedCount > 0) { //It's used in recipe
                                extractedItems.add(availableItems.getStackInSlot(slot));
                            }
                        }
                        float outputTemp = HeatHandlingUtil.computeThermalEquilibrium(extractedItems);
                        recipeOutputItems.addAll(heatedRecipe.rollResults(outputTemp));
                    } else {
                        recipeOutputItems.addAll(heatedRecipe.rollResults(0));
                    }


                    CraftingContainer remainderContainer = new DummyCraftingContainer(availableItems, extractedItemsFromSlot);

                    for (FluidStack fluidStack : heatedRecipe.getFluidResults())
                        if (!fluidStack.isEmpty()) recipeOutputFluids.add(fluidStack);
                    for (ItemStack stack : heatedRecipe.getRemainingItems(remainderContainer))
                        if (!stack.isEmpty()) recipeOutputItems.add(stack);
                }

                if (!basin.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate)){
                    //WoodenCog.LOGGER.info("Basin cant accept outputs");
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    protected HeatedBasinRecipe(IRecipeTypeInfo type, HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        super(type, params);
    }

    public HeatedBasinRecipe(HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        this(AllHeatedRecipeTypes.HEATED_BASIN, params);
    }

    @Override
    protected int getMaxInputCount() {
        return 9;
    }

    @Override
    protected int getMaxOutputCount() {
        return 4;
    }

    @Override
    protected int getMaxFluidInputCount() {
        return 4;
    }

    @Override
    protected int getMaxFluidOutputCount() {
        return 2;
    }

    @Override
    protected boolean canRequireHeat() {
        return true;
    }

    @Override
    protected boolean canSpecifyDuration() {
        return true;
    }

    @Override
    public boolean matches(Container inv, @Nonnull Level worldIn) {
        return false;
    }
}
