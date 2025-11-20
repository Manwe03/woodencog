package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.google.gson.JsonObject;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.dries007.tfc.util.JsonHelpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;

public class WoodenCogIngredientSerializer {

    public static Ingredient parse(FriendlyByteBuf buffer) {
        boolean heat = buffer.readBoolean();
        return heat ? HeatableIngredient.Serializer.INSTANCE.parse(buffer) : Ingredient.fromNetwork(buffer);
    }

    public static List<Ingredient> parse(JsonObject json) {
        Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
        int min = JsonHelpers.getAsInt(json, "min_temp", Integer.MIN_VALUE);
        int max = JsonHelpers.getAsInt(json, "max_temp", Integer.MAX_VALUE);
        int count = JsonHelpers.getAsInt(json, "count", 1);
        if (internal == null) return List.of();
        Ingredient parsed = min <= 0 ? internal : HeatableIngredient.of(internal, min, max);
        return java.util.Collections.nCopies(count, parsed);
    }

    public static void write(FriendlyByteBuf buffer, Ingredient ingredient) {
        boolean heat = ingredient instanceof HeatableIngredient;
        buffer.writeBoolean(heat);
        if (heat) {
            HeatableIngredient.Serializer.INSTANCE.write(buffer, (HeatableIngredient) ingredient);
        } else {
            ingredient.toNetwork(buffer);
        }
    }
}