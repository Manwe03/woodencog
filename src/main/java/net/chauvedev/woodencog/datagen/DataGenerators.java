package net.chauvedev.woodencog.datagen;

import net.chauvedev.woodencog.datagen.recipe.WoodencogRecipeProvider;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.FoodIngredient;
import net.chauvedev.woodencog.recipes.heatedRecipes.input.HeatedIngredient;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class DataGenerators {

    public static void gatherData(GatherDataEvent event) {
        DataGenerators.registerSerializers();

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new WoodencogRecipeProvider(generator, output));

    }

    public static void registerSerializers(){
        CraftingHelper.register(HeatedIngredient.Serializer.location, HeatedIngredient.Serializer.INSTANCE);
        CraftingHelper.register(FoodIngredient.Serializer.location, FoodIngredient.Serializer.INSTANCE);
    }
}
