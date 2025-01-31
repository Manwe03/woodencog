package net.chauvedev.woodencog.recipes.heatedRecipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AllHeatedProcessingRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, WoodenCog.MOD_ID);
    public static final RegistryObject<RecipeSerializer<PressingRecipe>> HEATED_PRESSING_SERIALIZER =
            SERIALIZERS.register("heated_pressing", () -> new ProxyProcessingRecipeSerializer<>(
                    () -> new ProxyProcessingRecipeSerializer<>(()-> (RecipeSerializer<PressingRecipe>) AllRecipeTypes.PRESSING.getSerializer())
            ));

    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
    }
}
