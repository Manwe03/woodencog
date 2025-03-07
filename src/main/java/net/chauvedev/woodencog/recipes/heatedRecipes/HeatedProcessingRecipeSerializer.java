package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.HeatCondition;
import com.simibubi.create.foundation.fluid.FluidHelper;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.RecipeSerializer;
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
            HeatCondition requiredHeat = recipe.getRequiredHeat();
            if (requiredHeat != HeatCondition.NONE) {
                json.addProperty("heatRequirement", requiredHeat.serialize());
            }
            recipe.writeAdditional(json);
        } catch (Exception e){
            WoodenCog.LOGGER.error("ToJson: "+e.getMessage());
        }
    }

    protected T readFromJson(ResourceLocation recipeId, JsonObject json) {
        try {
            HeatedProcessingRecipeBuilder<T> builder = new HeatedProcessingRecipeBuilder<>(this.factory, recipeId);
            NonNullList<HeatableIngredient> ingredients = NonNullList.create();
            NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
            NonNullList<HeatedProcessingOutput> results = NonNullList.create();
            NonNullList<FluidStack> fluidResults = NonNullList.create();
            Iterator<JsonElement> var8 = GsonHelper.getAsJsonArray(json, "ingredients").iterator();

            JsonElement je;
            while(var8.hasNext()) {
                je = (JsonElement) var8.next();
                if (FluidIngredient.isFluidIngredient(je)) {
                    fluidIngredients.add(FluidIngredient.deserialize(je));
                } else {
                    ingredients.add(HeatableIngredient.Serializer.INSTANCE.parse((JsonObject) je));
                }
            }

            var8 = GsonHelper.getAsJsonArray(json, "results").iterator();

            while(var8.hasNext()) {
                je = (JsonElement)var8.next();
                JsonObject jsonObject = je.getAsJsonObject();
                if (GsonHelper.isValidNode(jsonObject, "fluid")) {
                    fluidResults.add(FluidHelper.deserializeFluidStack(jsonObject));
                } else {
                    results.add(HeatedProcessingOutput.deserialize(je));
                }
            }

            builder.withItemIngredients(ingredients).withItemOutputs(results).withFluidIngredients(fluidIngredients).withFluidOutputs(fluidResults);
            if (GsonHelper.isValidNode(json, "processingTime")) {
                builder.duration(GsonHelper.getAsInt(json, "processingTime"));
            }

            if (GsonHelper.isValidNode(json, "heatRequirement")) {
                builder.requiresHeat(HeatCondition.deserialize(GsonHelper.getAsString(json, "heatRequirement")));
            }

            T recipe = builder.build();
            recipe.readAdditional(json);

            WoodenCog.LOGGER.info("Get recipe form JSON "+ recipe.getId());
            WoodenCog.LOGGER.info(recipe.toString());
            return recipe;
        } catch (Exception e){
            WoodenCog.LOGGER.error("FromJson: "+e.getMessage());
            return null;
        }
    }

    protected void writeToBuffer(FriendlyByteBuf buffer, T recipe) {
        NonNullList<HeatableIngredient> ingredients = recipe.ingredients;
        NonNullList<FluidIngredient> fluidIngredients = recipe.fluidIngredients;
        NonNullList<HeatedProcessingOutput> outputs = recipe.results;
        NonNullList<FluidStack> fluidOutputs = recipe.fluidResults;
        buffer.writeVarInt(ingredients.size());
        ingredients.forEach((i) -> {
            i.getSerializer().parse(buffer);
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
        buffer.writeVarInt(recipe.getRequiredHeat().ordinal());
        recipe.writeAdditional(buffer);
    }

    protected T readFromBuffer(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        NonNullList<HeatableIngredient> ingredients = NonNullList.create();
        NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
        NonNullList<HeatedProcessingOutput> results = NonNullList.create();
        NonNullList<FluidStack> fluidResults = NonNullList.create();
        int size = buffer.readVarInt();

        int i;
        for(i = 0; i < size; ++i) {
            ingredients.add(HeatableIngredient.Serializer.INSTANCE.parse(buffer));
        }

        size = buffer.readVarInt();

        for(i = 0; i < size; ++i) {
            fluidIngredients.add(FluidIngredient.read(buffer));
        }

        size = buffer.readVarInt();

        for(i = 0; i < size; ++i) {
            results.add(HeatedProcessingOutput.read(buffer));
        }

        size = buffer.readVarInt();

        for(i = 0; i < size; ++i) {
            fluidResults.add(FluidStack.readFromPacket(buffer));
        }

        T recipe = (new HeatedProcessingRecipeBuilder<>(this.factory, recipeId)).withItemIngredients(ingredients).withItemOutputs(results).withFluidIngredients(fluidIngredients).withFluidOutputs(fluidResults).duration(buffer.readVarInt()).requiresHeat(HeatCondition.values()[buffer.readVarInt()]).build();
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
