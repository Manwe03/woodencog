package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.*;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.DynamicProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.HeatedProcessingOutput;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.common.crafting.conditions.NotCondition;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HeatedProcessingRecipeBuilder<T extends HeatedProcessingRecipe<?>>  {
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
        return this.withItemOutputs(NonNullList.of((DynamicProcessingOutput<?>) DynamicProcessingOutput.EMPTY, outputs));
    }

    public HeatedProcessingRecipeBuilder<T> withItemOutputs(NonNullList<DynamicProcessingOutput<?>> outputs) {
        this.params.results = outputs;
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> withFluidIngredients(FluidIngredient... ingredients) {
        return this.withFluidIngredients(NonNullList.of(FluidIngredient.EMPTY, ingredients));
    }

    public HeatedProcessingRecipeBuilder<T> withFluidIngredients(NonNullList<FluidIngredient> ingredients) {
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

    public T build(ResourceLocation id) {
        this.params.id = id;
        return this.factory.create(this.params);
    }

    public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
        consumer.accept(new HeatedProcessingRecipeBuilder.DataGenResult<>(this.build(id), this.recipeConditions));
    }

    public HeatedProcessingRecipeBuilder<T> require(TagKey<Item> tag) {
        return this.require((HeatableIngredient) Ingredient.of(tag));
    }

    public HeatedProcessingRecipeBuilder<T> require(ItemLike item) {
        return this.require((HeatableIngredient) Ingredient.of(new ItemLike[]{item}));
    }

    public <I extends HeatableIngredient> HeatedProcessingRecipeBuilder<T> require(I ingredient) {
        this.params.ingredients.add(ingredient);
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> require(Fluid fluid, int amount) {
        return this.require(FluidIngredient.fromFluid(fluid, amount));
    }

    public HeatedProcessingRecipeBuilder<T> require(TagKey<Fluid> fluidTag, int amount) {
        return this.require(FluidIngredient.fromTag(fluidTag, amount));
    }

    public HeatedProcessingRecipeBuilder<T> require(FluidIngredient ingredient) {
        this.params.fluidIngredients.add(ingredient);
        return this;
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

    public HeatedProcessingRecipeBuilder<T> toolNotConsumed() {
        this.params.keepHeldItem = true;
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> whenModLoaded(String modid) {
        return this.withCondition(new ModLoadedCondition(modid));
    }

    public HeatedProcessingRecipeBuilder<T> whenModMissing(String modid) {
        return this.withCondition(new NotCondition(new ModLoadedCondition(modid)));
    }

    public HeatedProcessingRecipeBuilder<T> withCondition(ICondition condition) {
        this.recipeConditions.add(condition);
        return this;
    }

    @FunctionalInterface
    public interface HeatedProcessingRecipeFactory<T extends HeatedProcessingRecipe<?>> {
        T create(HeatedProcessingRecipeParams var1);
    }

    public static class HeatedProcessingRecipeParams {
        protected ResourceLocation id;
        protected NonNullList<Ingredient> ingredients;
        protected NonNullList<DynamicProcessingOutput<?>> results;
        protected NonNullList<FluidIngredient> fluidIngredients;
        protected NonNullList<FluidStack> fluidResults;
        protected int processingDuration;
        protected WoodenCogHeatCondition requiredHeat;
        public boolean keepHeldItem;

        protected HeatedProcessingRecipeParams() {
            this.id = null;
            this.ingredients = NonNullList.create();
            this.results = NonNullList.create();
            this.fluidIngredients = NonNullList.create();
            this.fluidResults = NonNullList.create();
            this.processingDuration = 0;
            this.requiredHeat = new WoodenCogHeatCondition(0);
            this.keepHeldItem = false;
        }
    }

    public static class HeatedIngridientParams {
        public static HeatedIngridientParams DEFAULT = new HeatedIngridientParams(0,true,0);
        public int temperature;
        public boolean copyHeat;
        public int cooling;

        HeatedIngridientParams(int temperature, boolean copyHeat, int cooling){
            this.temperature = temperature;
            this.copyHeat = copyHeat;
            this.cooling = cooling;
        }
    }

    public static class DataGenResult<S extends HeatedProcessingRecipe<?>> implements FinishedRecipe {

        private final List<ICondition> recipeConditions;
        private final HeatedProcessingRecipeSerializer<S> serializer;
        private final ResourceLocation id;
        private final S recipe;

        @SuppressWarnings("unchecked")
        public DataGenResult(S recipe, List<ICondition> recipeConditions) {
            this.recipe = recipe;
            this.recipeConditions = recipeConditions;
            IRecipeTypeInfo recipeType = this.recipe.getTypeInfo();
            ResourceLocation typeId = recipeType.getId();

            if (!(recipeType.getSerializer() instanceof HeatedProcessingRecipeSerializer))
                throw new IllegalStateException("Cannot datagen HeatedProcessingRecipe of type: " + typeId);

            this.id = ResourceLocation.tryBuild(recipe.getId().getNamespace(), typeId.getPath() + "/" + recipe.getId().getPath());

            this.serializer = (HeatedProcessingRecipeSerializer<S>) recipe.getSerializer();
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
            System.out.println("serializeRecipeData");
            serializer.write(json, recipe);
            if (recipeConditions.isEmpty())
                return;

            JsonArray conds = new JsonArray();
            recipeConditions.forEach(c -> conds.add(CraftingHelper.serialize(c)));
            json.add("conditions", conds);
        }

        @Override
        public @NotNull ResourceLocation getId() {
            return id;
        }

        @Override
        public @NotNull RecipeSerializer<?> getType() {
            return serializer;
        }

        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }

    }
}
