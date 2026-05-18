package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.common.base.Joiner;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.DynamicProcessingOutput;
import net.chauvedev.woodencog.utils.HeatHandlingUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class HeatedProcessingRecipe<I extends RecipeInput, P extends HeatedProcessingRecipeParams> implements Recipe<I> {

    protected final ResourceLocation id;
    protected final P params;
    protected final NonNullList<Ingredient> ingredients;
    protected final NonNullList<DynamicProcessingOutput<?>> results;
    protected final NonNullList<SizedFluidIngredient> fluidIngredients;
    protected final NonNullList<FluidStack> fluidResults;
    protected final int processingDuration;
    protected final WoodenCogHeatCondition requiredHeat;
    private final RecipeType<?> type;
    private final RecipeSerializer<?> serializer;
    private final IRecipeTypeInfo typeInfo;
    private Supplier<ItemStack> forcedResult = null;

    public HeatedProcessingRecipe(IRecipeTypeInfo typeInfo, P params) {
        this.processingDuration = params.processingDuration;
        this.fluidIngredients = params.fluidIngredients;
        this.fluidResults = params.fluidResults;
        this.requiredHeat = params.requiredHeat;
        this.ingredients = params.ingredients;
        this.results = params.results;
        this.id = typeInfo.getId();
        this.params = params;
        this.serializer = typeInfo.getSerializer();
        this.type = typeInfo.getType();
        this.typeInfo = typeInfo;
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

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        String messageHeader = "Recipe (" + this.id.toString() + ")";
        int ingredientCount = this.ingredients.size();
        int outputCount = this.results.size();

        if (ingredientCount > this.getMaxInputCount()) {
            errors.add(messageHeader + " has more item inputs (" + ingredientCount + ") than supported (" + this.getMaxInputCount() + ").");
        }

        if (outputCount > this.getMaxOutputCount()) {
            errors.add(messageHeader + " has more item outputs (" + outputCount + ") than supported (" + this.getMaxOutputCount() + ").");
        }

        if (this.processingDuration > 0 && !this.canSpecifyDuration()) {
            errors.add(messageHeader + " specified a duration. Durations have no impact on this type of recipe.");
        }

        if (this.requiredHeat.getTemperature() != 0 && !this.canRequireHeat()) {
            errors.add(messageHeader + " specified a heat condition. Heat conditions have no impact on this type of recipe.");
        }

        ingredientCount = this.fluidIngredients.size();
        outputCount = this.fluidResults.size();
        if (ingredientCount > this.getMaxFluidInputCount()) {
            errors.add(messageHeader + " has more fluid inputs (" + ingredientCount + ") than supported (" + this.getMaxFluidInputCount() + ").");
        }

        if (outputCount > this.getMaxFluidOutputCount()) {
            errors.add(messageHeader + " has more fluid outputs (" + outputCount + ") than supported (" + this.getMaxFluidOutputCount() + ").");
        }
        return errors;
    }

    public P getParams() {
        return params;
    }

    /**
     * @implNote Do not use, Use -> getHeatedIngredients();
     */
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public NonNullList<SizedFluidIngredient> getFluidIngredients() {
        return this.fluidIngredients;
    }

    public List<DynamicProcessingOutput<?>> getRollableResults() {
        return this.results;
    }

    public NonNullList<FluidStack> getFluidResults() {
        return this.fluidResults;
    }

    public void enforceNextResult(Supplier<ItemStack> stack) {
        this.forcedResult = stack;
    }

    public List<ItemStack> rollResults(List<ItemStack> usedItems, RandomSource randomSource) {
        return this.rollResults(this.getRollableResults(),randomSource, usedItems);
    }

    public List<ItemStack> rollResults(List<DynamicProcessingOutput<?>> rollableResults, RandomSource randomSource, float temp) {
        List<ItemStack> results = new ArrayList<>();
        for(int i = 0; i < rollableResults.size(); ++i) {
            DynamicProcessingOutput<?> output = rollableResults.get(i);
            DynamicProcessingOutput.setDynamicData(output,temp);
            ItemStack stack = i == 0 && this.forcedResult != null ? this.forcedResult.get() : output.rollOutput(randomSource);
            results.add(stack);
        }
        return results;
    }

    public List<ItemStack> rollResults(List<DynamicProcessingOutput<?>> rollableResults, RandomSource randomSource, List<ItemStack> usedItems) {
        List<ItemStack> results = new ArrayList<>();
        for(int i = 0; i < rollableResults.size(); ++i) {
            DynamicProcessingOutput<?> output = rollableResults.get(i);
            if(output.getType() == DynamicProcessingOutput.ProcessingOutputTypes.HEATED){
                float temp = HeatHandlingUtil.computeThermalEquilibrium(usedItems);
                DynamicProcessingOutput.setDynamicData(output, temp);
            } else {
                DynamicProcessingOutput.setDynamicData(output, usedItems);
            }
            ItemStack stack = i == 0 && this.forcedResult != null ? this.forcedResult.get() : output.rollOutput(randomSource);
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

    @Override
    public @NotNull ItemStack assemble(@NotNull I input, @NotNull HolderLookup.Provider registryAccess) {
        return this.getResultItem(registryAccess);
    }

    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider registryAccess) {
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

    public static <P extends HeatedProcessingRecipeParams, R extends HeatedProcessingRecipe<?, P>> MapCodec<R> codec(
            HeatedProcessingRecipe.Factory<P, R> factory, MapCodec<P> paramsCodec) {
        return paramsCodec.xmap(factory::create, recipe -> recipe.getParams())
                .validate(recipe -> {
                    var errors = recipe.validate();
                    if (errors.isEmpty())
                        return DataResult.success(recipe);
                    errors.add(recipe.getClass().getSimpleName() + " failed validation:");
                    return DataResult.error(() -> Joiner.on('\n').join(errors), recipe);
                });
    }

    public static <P extends HeatedProcessingRecipeParams, R extends HeatedProcessingRecipe<?, P>> StreamCodec<RegistryFriendlyByteBuf, R> streamCodec(
            HeatedProcessingRecipe.Factory<P, R> factory, StreamCodec<RegistryFriendlyByteBuf, P> streamCodec
    ) {
        return streamCodec.map(factory::create, HeatedProcessingRecipe::getParams);
    }

    @FunctionalInterface
    public interface Factory<P extends HeatedProcessingRecipeParams, R extends HeatedProcessingRecipe<?, P>> {
        R create(P params);
    }

    public static class Serializer<P extends HeatedProcessingRecipeParams, R extends HeatedProcessingRecipe<?,P>> implements RecipeSerializer<R> {
        private final HeatedProcessingRecipe.Factory<P,R> factory;
        private final MapCodec<R> codec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        public Serializer(HeatedProcessingRecipe.Factory<P,R> factory) {
            this.factory = factory;
            this.codec = HeatedProcessingRecipe.codec(factory, (MapCodec<P>) HeatedProcessingRecipeParams.CODEC);
            this.streamCodec = HeatedProcessingRecipe.streamCodec(factory, (StreamCodec<RegistryFriendlyByteBuf, P>) HeatedProcessingRecipeParams.STREAM_CODEC);
        }

        @Override
        public MapCodec<R> codec() {
            return codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec() {
            return streamCodec;
        }

        public HeatedProcessingRecipe.Factory<P,R> factory() {
            return factory;
        }
    }
}
