package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.HeatedIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.DynamicProcessingOutput;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Iterator;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class HeatedProcessingRecipeSerializer<T extends HeatedProcessingRecipe<?>> implements RecipeSerializer<T> {
    private final HeatedProcessingRecipeBuilder.HeatedProcessingRecipeFactory<T> factory;

    public HeatedProcessingRecipeSerializer(HeatedProcessingRecipeBuilder.HeatedProcessingRecipeFactory<T> factory) {
        this.factory = factory;
    }

    protected void writeToJson(JsonObject json, T recipe) {
        try {
            JsonArray jsonIngredients = new JsonArray();
            JsonArray jsonOutputs = new JsonArray();
            try {
                recipe.ingredients.forEach((i) -> {
                    jsonIngredients.add(i.toJson());
                });
            }catch (Exception e){
                WoodenCog.LOGGER.error("Parse Ingredients: " + e.getMessage());
            }
            recipe.fluidIngredients.forEach((i) -> {
                jsonIngredients.add(i.serialize());
            });
            try {
                recipe.results.forEach((o) -> {
                    jsonOutputs.add(o.serialize());
                });
            }catch (Exception e){
                WoodenCog.LOGGER.error("Parse Outputs: " + e.getMessage());
            }
            recipe.fluidResults.forEach((o) -> {
                jsonOutputs.add(FluidHelper.serializeFluidStack(o));
            });
            json.add("ingredients", jsonIngredients);
            json.add("results", jsonOutputs);
            int processingDuration = recipe.getProcessingDuration();
            if (processingDuration > 0) {
                json.addProperty("processingTime", processingDuration);
            }
            WoodenCogHeatCondition requiredHeat = recipe.getRequiredHeat();
            if (requiredHeat.hasTemp()) {
                json.addProperty("heatRequirement", requiredHeat.serialize());
            }
            recipe.writeAdditional(json);
        } catch (Exception e){
            WoodenCog.LOGGER.error("ToJson: "+e.getMessage());
        }
    }

    protected T readFromJson(ResourceLocation recipeId, JsonObject json) {
        try {
            HeatedProcessingRecipeBuilder<T> builder = new HeatedProcessingRecipeBuilder<>(this.factory);
            NonNullList<Ingredient> ingredients = NonNullList.create();
            NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
            NonNullList<DynamicProcessingOutput<?>> results = NonNullList.create();
            NonNullList<FluidStack> fluidResults = NonNullList.create();
            Iterator<JsonElement> element = GsonHelper.getAsJsonArray(json, "ingredients").iterator();


            while(element.hasNext()) {
                JsonElement jsonInput = element.next();
                if (FluidIngredient.isFluidIngredient(jsonInput)) {
                    fluidIngredients.add(FluidIngredient.deserialize(jsonInput));
                } else {
                    try {
                        ingredients.add(CraftingHelper.getIngredient(jsonInput,true));
                    } catch (Exception e){
                        System.out.println("Exception thrown in WoodenCogIngredientSerializer.parse");
                        throw e;
                    }
                }
            }

            element = GsonHelper.getAsJsonArray(json, "results").iterator();

            while(element.hasNext()) {
                JsonElement jsonOutput = element.next();
                JsonObject jsonObject = jsonOutput.getAsJsonObject();
                if (GsonHelper.isValidNode(jsonObject, "fluid")) {
                    fluidResults.add(FluidHelper.deserializeFluidStack(jsonObject));
                } else {
                    results.add(DynamicProcessingOutput.deserialize(jsonOutput));
                }
            }

            builder.withItemIngredients(ingredients).withItemOutputs(results).withFluidIngredients(fluidIngredients).withFluidOutputs(fluidResults);
            if (GsonHelper.isValidNode(json, "processingTime")) {
                builder.duration(GsonHelper.getAsInt(json, "processingTime"));
            }

            if (GsonHelper.isValidNode(json, "heatRequirement")) {
                try {
                    builder.requiresHeat(WoodenCogHeatCondition.of(GsonHelper.getAsInt(json, "heatRequirement")));
                }catch (JsonSyntaxException e){
                    WoodenCog.LOGGER.error(e.getMessage());
                }
            }

            T recipe = builder.build(recipeId);
            recipe.readAdditional(json);

            return recipe;
        } catch (Exception e){
            WoodenCog.LOGGER.error("FromJson: "+e.getMessage());
            return null;
        }
    }

    protected void writeToBuffer(FriendlyByteBuf buffer, T recipe) {
        NonNullList<Ingredient> ingredients = recipe.ingredients;
        NonNullList<FluidIngredient> fluidIngredients = recipe.fluidIngredients;
        NonNullList<DynamicProcessingOutput<?>> outputs = recipe.results;
        NonNullList<FluidStack> fluidOutputs = recipe.fluidResults;
        buffer.writeVarInt(ingredients.size());
        ingredients.forEach((i) -> {
            i.toNetwork(buffer);
        });
        buffer.writeVarInt(fluidIngredients.size());
        fluidIngredients.forEach((i) -> {
            i.write(buffer);
        });
        buffer.writeVarInt(outputs.size());
        outputs.forEach((o) -> {
            o.write(buffer);
        });
        buffer.writeVarInt(fluidOutputs.size());
        fluidOutputs.forEach((o) -> {
            o.writeToPacket(buffer);
        });
        buffer.writeVarInt(recipe.getProcessingDuration());
        buffer.writeVarInt(recipe.getRequiredHeat().getTemperature());
        recipe.writeAdditional(buffer);
    }

    protected T readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        System.out.println("[WoodenCog] Reading recipe from buffer: " + recipeId);

        NonNullList<Ingredient> ingredients = NonNullList.create();
        NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
        NonNullList<DynamicProcessingOutput<?>> results = NonNullList.create();
        NonNullList<FluidStack> fluidResults = NonNullList.create();
        int size = buffer.readVarInt();

        int i;
        for(i = 0; i < size; ++i) {
            ingredients.add(Ingredient.fromNetwork(buffer));
        }

        size = buffer.readVarInt();

        for(i = 0; i < size; ++i) {
            fluidIngredients.add(FluidIngredient.read(buffer));
        }

        size = buffer.readVarInt();

        for(i = 0; i < size; ++i) {
            results.add(DynamicProcessingOutput.read(buffer));
        }

        size = buffer.readVarInt();

        for(i = 0; i < size; ++i) {
            fluidResults.add(FluidStack.readFromPacket(buffer));
        }

        int processingDuration = buffer.readVarInt();
        int temperature = buffer.readVarInt();

        T recipe = (new HeatedProcessingRecipeBuilder<>(this.factory)).withItemIngredients(ingredients)
                .withItemOutputs(results).withFluidIngredients(fluidIngredients)
                .withFluidOutputs(fluidResults).duration(processingDuration)
                .requiresHeat(WoodenCogHeatCondition.of(temperature)).build(recipeId);
        recipe.readAdditional(buffer);
        return recipe;
    }

    public final void write(JsonObject json, T recipe) {
        this.writeToJson(json, recipe);
    }

    public final T fromJson(ResourceLocation id, JsonObject json) {
        return this.readFromJson(id, json);
    }

    public final void toNetwork(FriendlyByteBuf buffer, T recipe) {
        this.writeToBuffer(buffer, recipe);
    }

    public final T fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        return this.readFromBuffer(id, buffer);
    }

    public HeatedProcessingRecipeBuilder.HeatedProcessingRecipeFactory<T> getFactory() {
        return this.factory;
    }
}
