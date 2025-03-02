package net.chauvedev.woodencog.recipes.heatedRecipes.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder;
import com.simibubi.create.foundation.blockEntity.behaviour.filtering.FilteringBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.DummyCraftingContainer;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.utility.Iterate;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipeBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.*;

public class HeatedBasinRecipe extends HeatedProcessingRecipe<SmartInventory> {

    public static boolean match(BasinBlockEntity basin, Recipe<?> recipe) {
        FilteringBehaviour filter = basin.getFilter();
        if (filter == null) {
            return false;
        } else {
            boolean filterTest = filter.test(recipe.getResultItem(basin.getLevel().registryAccess()));
            if (recipe instanceof BasinRecipe) {
                BasinRecipe basinRecipe = (BasinRecipe)recipe;
                if (basinRecipe.getRollableResults().isEmpty() && !basinRecipe.getFluidResults().isEmpty()) {
                    filterTest = filter.test((FluidStack)basinRecipe.getFluidResults().get(0));
                }
            }

            return !filterTest ? false : apply(basin, recipe, true);
        }
    }

    public static boolean apply(BasinBlockEntity basin, Recipe<?> recipe) {
        return apply(basin, recipe, false);
    }

    private static boolean apply(BasinBlockEntity basin, Recipe<?> recipe, boolean test) {
        boolean isBasinRecipe = recipe instanceof BasinRecipe;
        IItemHandler availableItems = (IItemHandler)basin.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse((Object)null);
        IFluidHandler availableFluids = (IFluidHandler)basin.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse((Object)null);
        if (availableItems != null && availableFluids != null) {
            BlazeBurnerBlock.HeatLevel heat = BasinBlockEntity.getHeatLevelOf(basin.getLevel().getBlockState(basin.getBlockPos().below(1)));
            if (isBasinRecipe && !((BasinRecipe)recipe).getRequiredHeat().testBlazeBurner(heat)) {
                return false;
            } else {
                List<ItemStack> recipeOutputItems = new ArrayList();
                List<FluidStack> recipeOutputFluids = new ArrayList();
                List<Ingredient> ingredients = new LinkedList(recipe.getIngredients());
                List<FluidIngredient> fluidIngredients = isBasinRecipe ? ((BasinRecipe)recipe).getFluidIngredients() : Collections.emptyList();
                boolean[] var11 = Iterate.trueAndFalse;
                int var12 = var11.length;

                for(int var13 = 0; var13 < var12; ++var13) {
                    boolean simulate = var11[var13];
                    if (!simulate && test) {
                        return true;
                    }

                    int[] extractedItemsFromSlot = new int[availableItems.getSlots()];
                    int[] extractedFluidsFromTank = new int[availableFluids.getTanks()];

                    ItemStack stack;
                    label110:
                    for(int i = 0; i < ingredients.size(); ++i) {
                        Ingredient ingredient = (Ingredient)ingredients.get(i);

                        for(int slot = 0; slot < availableItems.getSlots(); ++slot) {
                            if (!simulate || availableItems.getStackInSlot(slot).getCount() > extractedItemsFromSlot[slot]) {
                                stack = availableItems.extractItem(slot, 1, true);
                                if (ingredient.test(stack)) {
                                    if (!simulate) {
                                        availableItems.extractItem(slot, 1, false);
                                    }

                                    int var10002 = extractedItemsFromSlot[slot]++;
                                    continue label110;
                                }
                            }
                        }

                        return false;
                    }

                    boolean fluidsAffected = false;

                    label129:
                    for(int i = 0; i < ((List)fluidIngredients).size(); ++i) {
                        FluidIngredient fluidIngredient = (FluidIngredient)((List)fluidIngredients).get(i);
                        int amountRequired = fluidIngredient.getRequiredAmount();

                        for(int tank = 0; tank < availableFluids.getTanks(); ++tank) {
                            FluidStack fluidStack = availableFluids.getFluidInTank(tank);
                            if ((!simulate || fluidStack.getAmount() > extractedFluidsFromTank[tank]) && fluidIngredient.test(fluidStack)) {
                                int drainedAmount = Math.min(amountRequired, fluidStack.getAmount());
                                if (!simulate) {
                                    fluidStack.shrink(drainedAmount);
                                    fluidsAffected = true;
                                }

                                amountRequired -= drainedAmount;
                                if (amountRequired == 0) {
                                    extractedFluidsFromTank[tank] += drainedAmount;
                                    continue label129;
                                }
                            }
                        }

                        return false;
                    }

                    if (fluidsAffected) {
                        ((SmartFluidTankBehaviour)basin.getBehaviour(SmartFluidTankBehaviour.INPUT)).forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
                        ((SmartFluidTankBehaviour)basin.getBehaviour(SmartFluidTankBehaviour.OUTPUT)).forEach(SmartFluidTankBehaviour.TankSegment::onFluidStackChanged);
                    }

                    if (simulate) {
                        if (!(recipe instanceof BasinRecipe)) {
                            recipeOutputItems.add(recipe.getResultItem(basin.getLevel().registryAccess()));
                            if (recipe instanceof CraftingRecipe) {
                                CraftingRecipe craftingRecipe = (CraftingRecipe)recipe;
                                Iterator var32 = craftingRecipe.getRemainingItems(new DummyCraftingContainer(availableItems, extractedItemsFromSlot)).iterator();

                                while(var32.hasNext()) {
                                    ItemStack stack = (ItemStack)var32.next();
                                    if (!stack.isEmpty()) {
                                        recipeOutputItems.add(stack);
                                    }
                                }
                            }
                        } else {
                            BasinRecipe basinRecipe = (BasinRecipe)recipe;
                            recipeOutputItems.addAll(basinRecipe.rollResults());
                            Iterator var28 = basinRecipe.getFluidResults().iterator();

                            while(var28.hasNext()) {
                                FluidStack fluidStack = (FluidStack)var28.next();
                                if (!fluidStack.isEmpty()) {
                                    recipeOutputFluids.add(fluidStack);
                                }
                            }

                            var28 = basinRecipe.getRemainingItems(basin.getInputInventory()).iterator();

                            while(var28.hasNext()) {
                                stack = (ItemStack)var28.next();
                                if (!stack.isEmpty()) {
                                    recipeOutputItems.add(stack);
                                }
                            }
                        }
                    }

                    if (!basin.acceptOutputs(recipeOutputItems, recipeOutputFluids, simulate)) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }

    protected HeatedBasinRecipe(IRecipeTypeInfo type, HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        super(type, params);
    }

    public HeatedBasinRecipe(HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        this(AllRecipeTypes.BASIN, params);
    }

    protected int getMaxInputCount() {
        return 9;
    }

    protected int getMaxOutputCount() {
        return 4;
    }

    protected int getMaxFluidInputCount() {
        return 2;
    }

    protected int getMaxFluidOutputCount() {
        return 2;
    }

    protected boolean canRequireHeat() {
        return true;
    }

    protected boolean canSpecifyDuration() {
        return true;
    }

    public boolean matches(SmartInventory inv, @Nonnull Level worldIn) {
        return false;
    }
}
