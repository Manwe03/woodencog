package net.chauvedev.woodencog.recipes.heatedRecipes.input;

import net.chauvedev.woodencog.WoodenCog;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class WoodenCogIngredients {
    public static final DeferredRegister<IngredientType<?>> INGREDIENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.INGREDIENT_TYPES, WoodenCog.MOD_ID);

    public static final Supplier<IngredientType<HeatedIngredient>> HEATED =
            INGREDIENT_TYPES.register("heated",
                    () -> new IngredientType<>(HeatedIngredient.CODEC));
    public static final Supplier<IngredientType<FoodIngredient>> FOOD =
            INGREDIENT_TYPES.register("food",
                    () -> new IngredientType<>(FoodIngredient.CODEC));

    public static void register(IEventBus eventBus){
        INGREDIENT_TYPES.register(eventBus);
    }
}
