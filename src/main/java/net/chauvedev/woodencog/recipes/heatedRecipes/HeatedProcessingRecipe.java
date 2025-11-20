package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonObject;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.utils.HeatHandlingUtil;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class HeatedProcessingRecipe<T extends Container> implements Recipe<T> {
    protected final ResourceLocation id;
    protected final NonNullList<Ingredient> ingredients;
    protected final NonNullList<HeatedProcessingOutput> results;
    protected final NonNullList<FluidIngredient> fluidIngredients;
    protected final NonNullList<FluidStack> fluidResults;
    protected final int processingDuration;
    protected final WoodenCogHeatCondition requiredHeat;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    private final IRecipeTypeInfo typeInfo;
    private Supplier<ItemStack> forcedResult = null;

    public HeatedProcessingRecipe(IRecipeTypeInfo typeInfo, HeatedProcessingRecipeBuilder.HeatedProcessingRecipeParams params) {
        this.typeInfo = typeInfo;
        this.processingDuration = params.processingDuration;
        this.fluidIngredients = params.fluidIngredients;
        this.fluidResults = params.fluidResults;
        this.serializer = typeInfo.getSerializer();
        this.requiredHeat = params.requiredHeat;
        this.ingredients = params.ingredients;
        this.type = typeInfo.getType();
        this.results = params.results;
        this.id = params.id;
        this.validate(typeInfo.getId());
    }

    protected abstract int getMaxInputCount();

    protected abstract int getMaxOutputCount();

    protected boolean canRequireHeat() {
        return false;
    }

    protected boolean canSpecifyDuration() {
        return false;
    }

    protected int getMaxFluidInputCount() {
        return 0;
    }

    protected int getMaxFluidOutputCount() {
        return 0;
    }

    private void validate(ResourceLocation recipeTypeId) {
        String messageHeader = "Your custom " + recipeTypeId + " recipe (" + this.id.toString() + ")";
        Logger logger = Create.LOGGER;
        int ingredientCount = this.ingredients.size();
        int outputCount = this.results.size();
        if (ingredientCount > this.getMaxInputCount()) {
            logger.warn(messageHeader + " has more item inputs (" + ingredientCount + ") than supported (" + this.getMaxInputCount() + ").");
        }

        if (outputCount > this.getMaxOutputCount()) {
            logger.warn(messageHeader + " has more item outputs (" + outputCount + ") than supported (" + this.getMaxOutputCount() + ").");
        }

        if (this.processingDuration > 0 && !this.canSpecifyDuration()) {
            logger.warn(messageHeader + " specified a duration. Durations have no impact on this type of recipe.");
        }

        if (this.requiredHeat.getTemperature() != 0 && !this.canRequireHeat()) {
            logger.warn(messageHeader + " specified a heat condition. Heat conditions have no impact on this type of recipe.");
        }

        ingredientCount = this.fluidIngredients.size();
        outputCount = this.fluidResults.size();
        if (ingredientCount > this.getMaxFluidInputCount()) {
            logger.warn(messageHeader + " has more fluid inputs (" + ingredientCount + ") than supported (" + this.getMaxFluidInputCount() + ").");
        }

        if (outputCount > this.getMaxFluidOutputCount()) {
            logger.warn(messageHeader + " has more fluid outputs (" + outputCount + ") than supported (" + this.getMaxFluidOutputCount() + ").");
        }
    }

    /**
     * @implNote Do not use, Use -> getHeatedIngredients();
     */
    public @NotNull NonNullList<Ingredient> getIngredients() {
        WoodenCog.LOGGER.warn("Fetched [Ingredients] instead of [HeatableIngredients] for: " + this.id);
        //Thread.dumpStack();
        return NonNullList.create();
    }
    public NonNullList<Ingredient> getHeatedIngredients(){
        return this.ingredients;
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return this.fluidIngredients;
    }

    public List<HeatedProcessingOutput> getRollableResults() {
        return this.results;
    }

    public NonNullList<FluidStack> getFluidResults() {
        return this.fluidResults;
    }

    public void enforceNextResult(Supplier<ItemStack> stack) {
        this.forcedResult = stack;
    }

    public List<ItemStack> rollResults(List<ItemStack> usedItems) {
        return this.rollResults(this.getRollableResults(), usedItems);
    }

    public List<ItemStack> rollResults(List<HeatedProcessingOutput> rollableResults, float temp) {
        List<ItemStack> results = new ArrayList<>();
        for(int i = 0; i < rollableResults.size(); ++i) {
            HeatedProcessingOutput output = rollableResults.get(i);
            output.setDynamicOutputTemp(temp);
            ItemStack stack = i == 0 && this.forcedResult != null ? this.forcedResult.get() : output.rollOutput();
            results.add(stack);
        }
        return results;
    }

    public List<ItemStack> rollResults(List<HeatedProcessingOutput> rollableResults, List<ItemStack> usedItems) {
        List<ItemStack> results = new ArrayList<>();
        for(int i = 0; i < rollableResults.size(); ++i) {
            HeatedProcessingOutput output = rollableResults.get(i);
            output.setDynamicOutputTemp(HeatHandlingUtil.computeThermalEquilibrium(usedItems));
            output.setDynamicUsedFoodItems(usedItems);
            ItemStack stack = i == 0 && this.forcedResult != null ? this.forcedResult.get() : output.rollOutput();
            results.add(stack);
        }
        return results;
    }

    public int getProcessingDuration() {
        return this.processingDuration;
    }

    public WoodenCogHeatCondition getRequiredHeat() {
        return this.requiredHeat;
    }

    public @NotNull ItemStack assemble(@NotNull T inv, @NotNull RegistryAccess registryAccess) {
        return this.getResultItem(registryAccess);
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public @NotNull ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return this.getRollableResults().isEmpty() ? ItemStack.EMPTY : this.getRollableResults().get(0).getStack();
    }

    public boolean isSpecial() {
        return true;
    }

    public @NotNull String getGroup() {
        return "heated_processing";
    }

    public @NotNull ResourceLocation getId() {
        return this.id;
    }

    public @NotNull RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    public @NotNull RecipeType<?> getType() {
        return this.type;
    }

    public IRecipeTypeInfo getTypeInfo() {
        return this.typeInfo;
    }

    public void readAdditional(JsonObject json) {
    }

    public void readAdditional(FriendlyByteBuf buffer) {
    }

    public void writeAdditional(JsonObject json) {
    }

    public void writeAdditional(FriendlyByteBuf buffer) {
    }
}
