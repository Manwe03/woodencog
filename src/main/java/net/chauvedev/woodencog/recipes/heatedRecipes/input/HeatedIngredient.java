package net.chauvedev.woodencog.recipes.heatedRecipes.input;

import com.google.gson.JsonObject;
import net.chauvedev.woodencog.WoodenCog;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.ingredients.DelegateIngredient;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class HeatedIngredient extends DelegateIngredient {

    private final int minTemp;
    private final int maxTemp;

    public static HeatedIngredient of(int minTemp, int maxTemp) {
        return of(null, minTemp, maxTemp);
    }

    public static HeatedIngredient of(@Nullable Ingredient ingredient) {
        return new HeatedIngredient(ingredient, 0, Integer.MAX_VALUE);
    }

    public static HeatedIngredient of(@Nullable Ingredient ingredient, int minTemp, int maxTemp) {
        return new HeatedIngredient(ingredient, minTemp, maxTemp);
    }

    protected HeatedIngredient(@Nullable Ingredient delegate, int minTemp, int maxTemp) {
        super(delegate);
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    public IIngredientSerializer<? extends DelegateIngredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Nullable
    protected ItemStack testDefaultItem(ItemStack stack) {
        IHeat heat = HeatCapability.get(stack);
        if (heat != null) {
            heat.setTemperature((float)this.minTemp);
            return stack;
        } else {
            return null;
        }
    }

    public boolean test(@Nullable ItemStack stack) {
        if (super.test(stack) && stack != null && !stack.isEmpty()) {
            IHeat heat = HeatCapability.get(stack);
            return heat != null && heat.getTemperature() >= (float)this.minTemp && heat.getTemperature() <= (float)this.maxTemp;
        } else {
            return false;
        }
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        if (this.minTemp != 0) {
            json.addProperty("min_temp", this.minTemp);
        }

        if (this.maxTemp != Integer.MAX_VALUE) {
            json.addProperty("max_temp", this.maxTemp);
        }

        return json;
    }

    public enum Serializer implements IIngredientSerializer<HeatedIngredient> {
        INSTANCE;

        public static final ResourceLocation location = WoodenCog.asResource("heated");

        Serializer() {

        }

        public HeatedIngredient parse(FriendlyByteBuf buffer) {
            Ingredient internal = Helpers.decodeNullable(buffer, Ingredient::fromNetwork);
            int min = buffer.readVarInt();
            int max = buffer.readVarInt();
            return new HeatedIngredient(internal, min, max);
        }

        public HeatedIngredient parse(JsonObject json) {
            Ingredient internal = json.has("ingredient") ? Ingredient.fromJson(JsonHelpers.get(json, "ingredient")) : null;
            int min = JsonHelpers.getAsInt(json, "min_temp", 0);
            int max = JsonHelpers.getAsInt(json, "max_temp", Integer.MAX_VALUE);
            return new HeatedIngredient(internal, min, max);
        }

        @Override
        public void write(FriendlyByteBuf buffer, HeatedIngredient ingredient) {
            Helpers.encodeNullable(ingredient.delegate, buffer, Ingredient::toNetwork);
            buffer.writeVarInt(ingredient.minTemp);
            buffer.writeVarInt(ingredient.maxTemp);
        }
    }
}
