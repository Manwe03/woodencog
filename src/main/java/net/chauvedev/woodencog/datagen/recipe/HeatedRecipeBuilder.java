package net.chauvedev.woodencog.datagen.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HeatedRecipeBuilder {
    private final List<MixIngredient> ingredients = new ArrayList<>();
    private final List<MixResult> results = new ArrayList<>();
    private double heatRequirement;
    private int processingTime = -1; // opcional

    public HeatedRecipeBuilder addItemIngredient(ResourceLocation item, double minTemp, double maxTemp) {
        ingredients.add(new ItemIngredient(item, minTemp, maxTemp));
        return this;
    }

    public HeatedRecipeBuilder addItemIngredient(ResourceLocation item) {
        ingredients.add(new ItemIngredient(item));
        return this;
    }

    public HeatedRecipeBuilder addFluidIngredient(ResourceLocation fluid, int amount) {
        ingredients.add(new FluidIngredient(fluid, amount));
        return this;
    }

    public HeatedRecipeBuilder addItemResult(ResourceLocation item, int count, int temperature, int cooling, boolean copyHeat) {
        results.add(new ItemResult(item, count, temperature, cooling, copyHeat));
        return this;
    }
    public HeatedRecipeBuilder addFluidResult(ResourceLocation fluid, int amount) {
        results.add(new FluidResult(fluid, amount));
        return this;
    }

    public HeatedRecipeBuilder heatRequirement(double heat) {
        this.heatRequirement = heat;
        return this;
    }

    public HeatedRecipeBuilder processingTime(int ticks) {
        this.processingTime = ticks;
        return this;
    }

    public void save(Consumer<FinishedRecipe> consumer, AllHeatedRecipeTypes recipeType, ResourceLocation id) {
        consumer.accept(new Result(recipeType, id, ingredients, results, heatRequirement, processingTime));
    }

    static class Result implements FinishedRecipe {
        private final AllHeatedRecipeTypes type;
        private final ResourceLocation id;
        private final List<MixIngredient> ingredients;
        private final List<MixResult> results;
        private final double heatRequirement;
        private final int processingTime;

        public Result(AllHeatedRecipeTypes recipeType ,ResourceLocation id, List<MixIngredient> ingredients, List<MixResult> results, double heatRequirement, int processingTime) {
            this.type = recipeType;
            this.id = id;
            this.ingredients = ingredients;
            this.results = results;
            this.heatRequirement = heatRequirement;
            this.processingTime = processingTime;
        }

        @Override
        public void serializeRecipeData(JsonObject json) {

            json.addProperty("type", type.getId().toString());

            JsonArray ingArray = new JsonArray();
            for (MixIngredient i : ingredients) {
                ingArray.add(i.toJson());
            }
            json.add("ingredients", ingArray);

            JsonArray resArray = new JsonArray();
            for (MixResult r : results) {
                resArray.add(r.toJson());
            }
            json.add("results", resArray);

            if (heatRequirement > 0) json.addProperty("heatRequirement", heatRequirement);
            if (processingTime > 0) json.addProperty("processingTime", processingTime);
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return type.getSerializer();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return new ResourceLocation("woodencog:advancement1");
        }
    }

    public abstract static class MixResult {
        public abstract JsonObject toJson();
    }

    public static class FluidResult extends MixResult {
        private final ResourceLocation fluid;
        private final int amount;

        public FluidResult(ResourceLocation fluid, int amount) {
            this.fluid = fluid;
            this.amount = amount;
        }

        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("fluid", fluid.toString());
            obj.add("nbt", new JsonObject());
            obj.addProperty("amount", amount);
            return obj;
        }
    }

    public static class ItemResult extends MixResult {
        private final ResourceLocation item;
        private final int count;
        private final int temperature;
        private final int cooling;
        private final boolean copyHeat;

        public ItemResult(ResourceLocation item, int count, int temperature, int cooling, boolean copyHeat) {
            this.item = item;
            this.count = count;
            this.temperature = temperature;
            this.cooling = cooling;
            this.copyHeat = copyHeat;
        }

        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("item", item.toString());
            if(count > 1) obj.addProperty("count", count);
            obj.addProperty("temperature", temperature);
            obj.addProperty("copy_heat", copyHeat);
            obj.addProperty("cooling", cooling);
            return obj;
        }
    }

    public abstract static class MixIngredient {
        public abstract JsonObject toJson();
    }

    public static class ItemIngredient extends MixIngredient {
        private final ResourceLocation ingredient;
        private final double minTemp;
        private final double maxTemp;

        public ItemIngredient(ResourceLocation ingredient, double minTemp, double maxTemp) {
            this.ingredient = ingredient;
            this.minTemp = minTemp;
            this.maxTemp = maxTemp;
        }

        public ItemIngredient(ResourceLocation ingredient) {
            this.ingredient = ingredient;
            this.minTemp = 0;
            this.maxTemp = 0;
        }

        @Override
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            JsonObject item = new JsonObject();
            item.addProperty("item", ingredient.toString());
            obj.add("ingredient", item);
            if(minTemp > 0) obj.addProperty("min_temp", minTemp);
            if(maxTemp > 0) obj.addProperty("max_temp", maxTemp);
            return obj;
        }
    }

    public static class FluidIngredient extends MixIngredient {
        private final ResourceLocation fluid;
        private final int amount;

        public FluidIngredient(ResourceLocation fluid, int amount) {
            this.fluid = fluid;
            this.amount = amount;
        }

        @Override
        public JsonObject toJson() {
            JsonObject obj = new JsonObject();
            obj.addProperty("fluid", fluid.toString());
            obj.add("nbt", new JsonObject());
            obj.addProperty("amount", amount);
            return obj;
        }
    }
}
