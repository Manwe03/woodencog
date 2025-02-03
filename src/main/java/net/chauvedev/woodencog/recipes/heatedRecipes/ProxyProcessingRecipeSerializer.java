package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonObject;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ProxyProcessingRecipeSerializer<T extends ProcessingRecipe<?>> implements RecipeSerializer<T> {

    private final Supplier<RecipeSerializer<T>> serializerSupplier;
    private RecipeSerializer<T> serializer;

    public ProxyProcessingRecipeSerializer(Supplier<RecipeSerializer<T>> serializerSupplier) {
        this.serializerSupplier = serializerSupplier;
    }

    private RecipeSerializer<T> getSerializer() {
        if (serializer == null) {
            serializer = serializerSupplier.get();
            if (serializer == null) {
                throw new IllegalStateException("Serializer not available");
            }
        }
        return serializer;
    }

    @Override
    public T fromJson(ResourceLocation id, JsonObject json) {
        return getSerializer().fromJson(id, json);
    }

    @Override
    public @Nullable T fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
        return getSerializer().fromNetwork(id, buffer);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, T recipe) {
        getSerializer().toNetwork(buffer, recipe);
    }
}