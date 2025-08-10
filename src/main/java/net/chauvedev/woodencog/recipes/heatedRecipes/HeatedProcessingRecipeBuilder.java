package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.*;
import com.simibubi.create.foundation.data.recipe.Mods;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import net.chauvedev.woodencog.WoodenCog;
import net.createmod.catnip.data.Pair;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class HeatedProcessingRecipeBuilder<T extends HeatedProcessingRecipe<?>>  {
    protected HeatedProcessingRecipeFactory<T> factory;
    protected HeatedProcessingRecipeParams params;
    protected List<ICondition> recipeConditions;

    public HeatedProcessingRecipeBuilder(HeatedProcessingRecipeFactory<T> factory, ResourceLocation recipeId) {
        this.params = new HeatedProcessingRecipeParams(recipeId);
        this.recipeConditions = new ArrayList<>();
        this.factory = factory;
    }

    public HeatedProcessingRecipeBuilder<T> withItemIngredients(HeatableIngredient... ingredients) {
        return this.withItemIngredients(NonNullList.of((HeatableIngredient) Ingredient.EMPTY, ingredients));
    }

    public HeatedProcessingRecipeBuilder<T> withItemIngredients(NonNullList<HeatableIngredient> ingredients) {
        this.params.ingredients = ingredients;
        return this;
    }

    public HeatedProcessingRecipeBuilder<T> withSingleItemOutput(ItemStack output) {
        return this.withItemOutputs(new HeatedProcessingOutput(output, 1.0F,0,true,0));
    }

    public HeatedProcessingRecipeBuilder<T> withItemOutputs(HeatedProcessingOutput... outputs) {
        return this.withItemOutputs(NonNullList.of((HeatedProcessingOutput) ProcessingOutput.EMPTY, outputs));
    }

    public HeatedProcessingRecipeBuilder<T> withItemOutputs(NonNullList<HeatedProcessingOutput> outputs) {
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

    public T build() {
        return this.factory.create(this.params);
    }

    public void build(Consumer<FinishedRecipe> consumer) {
        consumer.accept(new HeatedProcessingRecipeBuilder.DataGenResult<>(this.build(), this.recipeConditions));
    }

    public HeatedProcessingRecipeBuilder<T> require(TagKey<Item> tag) {
        return this.require((HeatableIngredient) Ingredient.of(tag));
    }

    public HeatedProcessingRecipeBuilder<T> require(ItemLike item) {
        return this.require((HeatableIngredient) Ingredient.of(new ItemLike[]{item}));
    }

    public HeatedProcessingRecipeBuilder<T> require(HeatableIngredient ingredient) {
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

    public HeatedProcessingRecipeBuilder<T> output(float chance, Mods mod, String id, int amount, HeatedIngridientParams params) {
        return this.output(new HeatedProcessingOutput(Pair.of(mod.asResource(id), amount), chance, params));
    }

    public HeatedProcessingRecipeBuilder<T> output(Mods mod, String id, HeatedIngridientParams params) {
        return this.output(1.0F, (ResourceLocation) mod.asResource(id), 1, params);
    }

    public HeatedProcessingRecipeBuilder<T> output(float chance, ResourceLocation registryName, int amount, HeatedIngridientParams params) {
        return this.output(new HeatedProcessingOutput(Pair.of(registryName, amount), chance, params));
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
        protected NonNullList<HeatableIngredient> ingredients;
        protected NonNullList<HeatedProcessingOutput> results;
        protected NonNullList<FluidIngredient> fluidIngredients;
        protected NonNullList<FluidStack> fluidResults;
        protected int processingDuration;
        protected WoodenCogHeatCondition requiredHeat;
        public boolean keepHeldItem;

        protected HeatedProcessingRecipeParams(ResourceLocation id) {
            this.id = id;
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
        private ResourceLocation id;
        private final S recipe;

        @SuppressWarnings("unchecked")
        public DataGenResult(S recipe, List<ICondition> recipeConditions) {
            this.recipe = recipe;
            this.recipeConditions = recipeConditions;
            IRecipeTypeInfo recipeType = this.recipe.getTypeInfo();
            ResourceLocation typeId = recipeType.getId();

            if (!(recipeType.getSerializer() instanceof HeatedProcessingRecipeSerializer))
                throw new IllegalStateException("Cannot datagen HeatedProcessingRecipe of type: " + typeId);

            this.id = new ResourceLocation(recipe.getId().getNamespace(),
                    typeId.getPath() + "/" + recipe.getId().getPath());

            try (FileWriter fw = new FileWriter("client_fromNetwork.log", true)) {
                fw.write("[DANGER][DANGER][DANGER] DataGenResult ID"+id);
            } catch (IOException e) {
                WoodenCog.LOGGER.error("Error writing client log", e);
            }

            this.serializer = (HeatedProcessingRecipeSerializer<S>) recipe.getSerializer();
        }

        @Override
        public void serializeRecipeData(JsonObject json) {
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
