package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.codec.CreateCodecs;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.DynamicProcessingOutput;
import net.createmod.catnip.codecs.stream.CatnipStreamCodecBuilders;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class HeatedProcessingRecipeParams {
    public static MapCodec<HeatedProcessingRecipeParams> CODEC = codec(HeatedProcessingRecipeParams::new);
    public static StreamCodec<RegistryFriendlyByteBuf, HeatedProcessingRecipeParams> STREAM_CODEC = streamCodec(HeatedProcessingRecipeParams::new);

    protected NonNullList<Ingredient> ingredients;
    protected NonNullList<DynamicProcessingOutput<?>> results;
    protected NonNullList<SizedFluidIngredient> fluidIngredients;
    protected NonNullList<FluidStack> fluidResults;
    protected int processingDuration;
    protected WoodenCogHeatCondition requiredHeat;
    public boolean keepHeldItem;

    protected HeatedProcessingRecipeParams() {
        this.ingredients = NonNullList.create();
        this.results = NonNullList.create();
        this.fluidIngredients = NonNullList.create();
        this.fluidResults = NonNullList.create();
        this.processingDuration = 0;
        this.requiredHeat = new WoodenCogHeatCondition(0);
        this.keepHeldItem = false;
    }

    //Serialization for 1.21 with codecs at the RecipeParams level

    protected static <P extends HeatedProcessingRecipeParams> MapCodec<P> codec(Supplier<P> factory) {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.either(CreateCodecs.FLAT_SIZED_FLUID_INGREDIENT_WITH_TYPE, Ingredient.CODEC).listOf().fieldOf("ingredients")
                        .forGetter(HeatedProcessingRecipeParams::ingredients),
                Codec.either(FluidStack.CODEC, DynamicProcessingOutput.CODEC).listOf().fieldOf("results")
                        .forGetter(HeatedProcessingRecipeParams::results),
                Codec.INT.optionalFieldOf("processing_time", 0)
                        .forGetter(HeatedProcessingRecipeParams::processingDuration),
                WoodenCogHeatCondition.CODEC.optionalFieldOf("heat_requirement", WoodenCogHeatCondition.NONE)
                        .forGetter(HeatedProcessingRecipeParams::requiredHeat)
        ).apply(instance, (ingredients, results, processingDuration, requiredHeat) -> {
            P params = factory.get();
            ingredients.forEach(either -> either
                    .ifRight(params.ingredients::add)
                    .ifLeft(params.fluidIngredients::add));
            results.forEach(either -> either
                    .ifRight(params.results::add)
                    .ifLeft(params.fluidResults::add));
            params.processingDuration = processingDuration;
            params.requiredHeat = requiredHeat;
            return params;
        }));
    }

    protected static <P extends HeatedProcessingRecipeParams> StreamCodec<RegistryFriendlyByteBuf, P> streamCodec(Supplier<P> factory) {
        return StreamCodec.of(
                (buffer, params) -> params.encode(buffer),
                buffer -> {
                    P params = factory.get();
                    params.decode(buffer);
                    return params;
                });
    }

    protected final List<Either<SizedFluidIngredient, Ingredient>> ingredients() {
        List<Either<SizedFluidIngredient, Ingredient>> ingredients =
                new ArrayList<>(this.ingredients.size() + this.fluidIngredients.size());
        this.ingredients.forEach(ingredient -> ingredients.add(Either.right(ingredient)));
        this.fluidIngredients.forEach(ingredient -> ingredients.add(Either.left(ingredient)));
        return ingredients;
    }

    protected final List<Either<FluidStack, DynamicProcessingOutput<?>>> results() {
        List<Either<FluidStack, DynamicProcessingOutput<?>>> results =
                new ArrayList<>(this.results.size() + this.fluidResults.size());
        this.results.forEach(result -> results.add(Either.right(result)));
        this.fluidResults.forEach(result -> results.add(Either.left(result)));
        return results;
    }

    protected final int processingDuration() {
        return processingDuration;
    }

    protected final WoodenCogHeatCondition requiredHeat() {
        return requiredHeat;
    }

    protected void encode(RegistryFriendlyByteBuf buffer) {
        CatnipStreamCodecBuilders.nonNullList(Ingredient.CONTENTS_STREAM_CODEC).encode(buffer, ingredients);
        CatnipStreamCodecBuilders.nonNullList(SizedFluidIngredient.STREAM_CODEC).encode(buffer, fluidIngredients);
        CatnipStreamCodecBuilders.nonNullList(DynamicProcessingOutput.STREAM_CODEC).encode(buffer, results);
        CatnipStreamCodecBuilders.nonNullList(FluidStack.STREAM_CODEC).encode(buffer, fluidResults);
        ByteBufCodecs.VAR_INT.encode(buffer, processingDuration);
        WoodenCogHeatCondition.STREAM_CODEC.encode(buffer, requiredHeat);
    }

    protected void decode(RegistryFriendlyByteBuf buffer) {
        ingredients = CatnipStreamCodecBuilders.nonNullList(Ingredient.CONTENTS_STREAM_CODEC).decode(buffer);
        fluidIngredients = CatnipStreamCodecBuilders.nonNullList(SizedFluidIngredient.STREAM_CODEC).decode(buffer);
        results = CatnipStreamCodecBuilders.nonNullList(DynamicProcessingOutput.STREAM_CODEC).decode(buffer);
        fluidResults = CatnipStreamCodecBuilders.nonNullList(FluidStack.STREAM_CODEC).decode(buffer);
        processingDuration = ByteBufCodecs.VAR_INT.decode(buffer);
        requiredHeat = WoodenCogHeatCondition.STREAM_CODEC.decode(buffer);
    }
}
