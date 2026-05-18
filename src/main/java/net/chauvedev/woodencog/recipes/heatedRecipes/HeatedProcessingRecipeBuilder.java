package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.simibubi.create.foundation.fluid.FluidHelper;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.DynamicProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.HeatedProcessingOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.FluidIngredient;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;


public class HeatedProcessingRecipeBuilder<T extends HeatedProcessingRecipe<?, ?>>  {
    protected final HeatedProcessingRecipeFactory<T> factory;
    protected final HeatedProcessingRecipeParams params;
    protected final List<ICondition> recipeConditions;

    public HeatedProcessingRecipeBuilder(HeatedProcessingRecipeFactory<T> factory) {
        this.params = new HeatedProcessingRecipeParams();
        this.recipeConditions = new ArrayList<>();
        this.factory = factory;
    }

    public HeatedProcessingRecipeBuilder<T> withItemIngredients(Ingredient... ingredients) {
        return this.withItemIngredients(NonNullList.of(Ingredient.EMPTY, ingredients));
    }

    public HeatedProcessingRecipeBuilder<T> withItemIngredients(NonNullList<Ingredient> ingredients) {
        this.params.ingredients = ingredients;
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> withItemOutputs(DynamicProcessingOutput<?>... outputs) {
        return this.withItemOutputs(NonNullList.of(DynamicProcessingOutput.EMPTY, outputs));
    }

    public HeatedProcessingRecipeBuilder<T> withItemOutputs(NonNullList<DynamicProcessingOutput<?>> outputs) {
        this.params.results = outputs;
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> withFluidIngredients(SizedFluidIngredient... ingredients) {
        return withFluidIngredients(NonNullList.of(new SizedFluidIngredient(FluidIngredient.empty(), 1000), ingredients));
    }

    public HeatedProcessingRecipeBuilder<T> withFluidIngredients(NonNullList<SizedFluidIngredient> ingredients) {
        this.params.fluidIngredients = ingredients;
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> withFluidOutputs(FluidStack... outputs) {
        return this.withFluidOutputs(NonNullList.of(FluidStack.EMPTY, outputs));
    }

    public HeatedProcessingRecipeBuilder<T> withFluidOutputs(NonNullList<FluidStack> outputs) {
        this.params.fluidResults = outputs;
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> duration(int ticks) {
        this.params.processingDuration = ticks;
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> averageProcessingDuration() {
        return this.duration(100);
    }

    public HeatedProcessingRecipeBuilder<T> requiresHeat(WoodenCogHeatCondition condition) {
        this.params.requiredHeat = condition;
        return this;
    }

    public T build() {
        return this.factory.create(this.params);
    }

    public void build(RecipeOutput consumer, ResourceLocation id) {
        T recipe = build();
        consumer.accept(id, recipe, null, recipeConditions.toArray(new ICondition[0]));
    }

    public HeatedProcessingRecipeBuilder<T> output(ItemLike item) {
        return this.output(item, 1, HeatedIngridientParams.DEFAULT);
    }

    public HeatedProcessingRecipeBuilder<T> output(ItemLike item, HeatedIngridientParams params) {
        return this.output(item, 1, params);
    }

    public HeatedProcessingRecipeBuilder<T> output(float chance, ItemLike item) {
        return this.output(chance, item, 1, HeatedIngridientParams.DEFAULT);
    }

    public HeatedProcessingRecipeBuilder<T> output(float chance, ItemLike item, HeatedIngridientParams params) {
        return this.output(chance, item, 1, params);
    }

    public HeatedProcessingRecipeBuilder<T> output(ItemLike item, int amount, HeatedIngridientParams params) {
        return this.output(1.0F, item, amount, params);
    }

    public HeatedProcessingRecipeBuilder<T> output(float chance, ItemLike item, int amount, HeatedIngridientParams params) {
        return this.output(chance, new ItemStack(item, amount), params);
    }

    public HeatedProcessingRecipeBuilder<T> output(ItemStack output, HeatedIngridientParams params) {
        return this.output(1.0F, output, params);
    }

    public HeatedProcessingRecipeBuilder<T> output(float chance, ItemStack output, HeatedIngridientParams params) {
        return this.output(new HeatedProcessingOutput(output, chance, params));
    }

    public HeatedProcessingRecipeBuilder<T> output(HeatedProcessingOutput output) {
        this.params.results.add(output);
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> output(Fluid fluid, int amount) {
        fluid = FluidHelper.convertToStill(fluid);
        return this.output(new FluidStack(fluid, amount));
    }

    public HeatedProcessingRecipeBuilder<T> output(FluidStack fluidStack) {
        this.params.fluidResults.add(fluidStack);
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> withCondition(ICondition condition) {
        this.recipeConditions.add(condition);
        return this;
    }

    @FunctionalInterface
    public interface HeatedProcessingRecipeFactory<T extends HeatedProcessingRecipe<?,?>> {
        T create(HeatedProcessingRecipeParams var1);
    }

    public static class HeatedIngridientParams {
        public static final HeatedIngridientParams DEFAULT = new HeatedIngridientParams(0,true,0);
        public final int temperature;
        public final boolean copyHeat;
        public final int cooling;

        HeatedIngridientParams(int temperature, boolean copyHeat, int cooling){
            this.temperature = temperature;
            this.copyHeat = copyHeat;
            this.cooling = cooling;
        }
    }


}
