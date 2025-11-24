package net.chauvedev.woodencog.recipes.heatedRecipes.input;

import com.google.gson.JsonObject;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.common.recipes.ingredients.DelegateIngredient;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class FoodIngredient extends DelegateIngredient {

    private final int copies;

    public FoodIngredient(@Nullable Ingredient delegate, int copies) {
        super(delegate);
        this.copies = copies;
    }

    public static FoodIngredient of(@Nullable Ingredient ingredient, int copies) {
        return new FoodIngredient(ingredient,copies);
    }

    public static FoodIngredient of(@Nullable Ingredient ingredient) {
        return new FoodIngredient(ingredient,1);
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        if (stack == null) return false;
        IFood cap = FoodCapability.get(stack);
        if(cap != null && cap.isRotten()) return false;
        return super.test(stack);
    }

    @Override
    protected @Nullable ItemStack testDefaultItem(ItemStack stack) {
        return super.testDefaultItem(stack);
    }

    @Override
    public IIngredientSerializer<? extends DelegateIngredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements IIngredientSerializer<FoodIngredient> {
        INSTANCE;

        public static final ResourceLocation location = WoodenCog.asResource("food");

        Serializer() {

        }

        public FoodIngredient parse(FriendlyByteBuf buffer) {
            Ingredient internal = (Ingredient) Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            int copies = buffer.readInt();
            return new FoodIngredient(internal, copies);
        }

        public FoodIngredient parse(JsonObject json) {
            Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
            int copies = JsonHelpers.getAsInt(json,"count",1);
            return new FoodIngredient(internal, copies);
        }

        @Override
        public void write(FriendlyByteBuf buffer, FoodIngredient ingredient) {
            Helpers.encodeNullable(ingredient.delegate, buffer, Ingredient::toNetwork);
            buffer.writeInt(ingredient.copies);
        }
    }
}
