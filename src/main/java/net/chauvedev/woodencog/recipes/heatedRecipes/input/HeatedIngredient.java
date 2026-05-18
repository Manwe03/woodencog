package net.chauvedev.woodencog.recipes.heatedRecipes.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.stream.Stream;

public class HeatedIngredient implements ICustomIngredient {

    private final Ingredient delegate;
    private final int minTemp;
    private final int maxTemp;

    public static final MapCodec<HeatedIngredient> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                            Ingredient.CODEC.fieldOf("ingredient").forGetter(i -> i.delegate),
                            Codec.INT.fieldOf("min_temp").forGetter(i -> i.minTemp),
                            Codec.INT.fieldOf("max_temp").forGetter(i -> i.maxTemp))
                    .apply(instance, HeatedIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, HeatedIngredient> STREAM_CODEC =
            ByteBufCodecs.fromCodecWithRegistries(CODEC.codec());

    public static Ingredient of(int minTemp, int maxTemp) {
        return of(null, minTemp, maxTemp);
    }

    public static Ingredient of(@Nullable Ingredient ingredient) {
        return of(ingredient, 0, Integer.MAX_VALUE);
    }

    public static Ingredient of(@Nullable Ingredient ingredient, int minTemp, int maxTemp) {
        return new HeatedIngredient(ingredient, minTemp, maxTemp).toVanilla();
    }

    protected HeatedIngredient(@Nullable Ingredient delegate, int minTemp, int maxTemp) {
        this.delegate = delegate;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    public int getMinTemp() {
        return minTemp;
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
        return WoodenCogIngredients.HEATED.get();
    }

    @Override
    public boolean test(ItemStack stack) {
        if (stack.isEmpty()) return false;
        IHeat heat = HeatCapability.get(stack);
        return heat != null &&
            heat.getTemperature() >= (float)this.minTemp &&
            heat.getTemperature() <= (float)this.maxTemp &&
            delegate.test(stack);
    }
}
