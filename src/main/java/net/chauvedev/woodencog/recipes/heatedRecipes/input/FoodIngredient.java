package net.chauvedev.woodencog.recipes.heatedRecipes.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class FoodIngredient implements ICustomIngredient {

    private final Ingredient delegate;
    private final int copies;

    public static final MapCodec<FoodIngredient> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(i -> i.delegate),
                            Codec.INT.fieldOf("copies").forGetter(i -> i.copies))
                    .apply(instance, FoodIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FoodIngredient> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    public FoodIngredient(@Nullable Ingredient delegate, int copies) {
        this.delegate = delegate;
        this.copies = copies;
    }

    public static Ingredient of(@Nullable Ingredient ingredient) {
        return of(ingredient,1);
    }

    public static Ingredient of(@Nullable Ingredient ingredient, int copies) {
        return new FoodIngredient(ingredient,copies).toVanilla();
    }

    /**
     * {@return the list of stacks that this ingredient accepts}
     *
     * <p>The following guidelines should be followed for good compatibility:
     * <ul>
     * <li>These stacks are generally used for display purposes, and need not be exhaustive or perfectly accurate.</li>
     * <li>An exception is ingredients that {@linkplain #isSimple() are simple},
     * for which it is important that the returned stacks correspond exactly to all the accepted {@link Item}s.</li>
     * <li>At least one stack must be returned for the ingredient not to be considered {@linkplain Ingredient#hasNoItems() accidentally empty}.</li>
     * <li>The ingredient should try to return at least one stack with each accepted {@link Item}.
     * This allows mods that inspect the ingredient to figure out which stacks it might accept.</li>
     * </ul>
     *
     * <p>Note: no caching needs to be done by the implementation, this is already handled by the ingredient itself.
     */
    @Override
    public @NotNull Stream<ItemStack> getItems() {
        return delegate != null ? Stream.of(delegate.getItems()) : Stream.empty();
    }

    /**
     * Returns whether this ingredient always requires {@linkplain #test direct stack testing}.
     *
     * @return {@code true} if this ingredient ignores NBT data when matching stacks, {@code false} otherwise
     * @see Ingredient#isSimple()
     */
    @Override
    public boolean isSimple() {
        return false;
    }

    /**
     * {@return the type of this ingredient}
     *
     * <p>The type must be registered to {@link NeoForgeRegistries#INGREDIENT_TYPES}.
     */
    @Override
    public IngredientType<?> getType() {
        return WoodenCogIngredients.FOOD.get();
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack == null) return false;
        IFood cap = FoodCapability.get(stack);
        if(cap != null && cap.isRotten()) return false;

        return delegate.test(stack);
    }
}
