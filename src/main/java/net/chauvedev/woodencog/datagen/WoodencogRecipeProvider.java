package net.chauvedev.woodencog.datagen;

import com.simibubi.create.api.data.recipe.ProcessingRecipeGen;
import net.chauvedev.woodencog.datagen.recipe.WoodenCogCompactingRecipeGen;
import net.chauvedev.woodencog.datagen.recipe.WoodenCogCrushingRecipeGen;
import net.chauvedev.woodencog.datagen.recipe.WoodenCogHeatedCompactingRecipeGen;
import net.chauvedev.woodencog.datagen.recipe.WoodenCogHeatedMixingRecipeGen;
import net.chauvedev.woodencog.datagen.recipe.WoodenCogHeatedPressingRecipeGen;
import net.chauvedev.woodencog.datagen.recipe.WoodenCogMixingRecipeGen;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class WoodencogRecipeProvider extends RecipeProvider {

    public WoodencogRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    //WoodenCog processing recipes
    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        new WoodenCogHeatedCompactingRecipeGen(recipeOutput);
        new WoodenCogHeatedMixingRecipeGen(recipeOutput);
        new WoodenCogHeatedPressingRecipeGen(recipeOutput);
    }

    static final List<ProcessingRecipeGen> GENERATORS = new ArrayList<>();

    //Create processing recipes
    public static void registerAllProcessing(DataGenerator gen, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        GENERATORS.clear();

        GENERATORS.add(new WoodenCogCompactingRecipeGen(output, registries));
        GENERATORS.add(new WoodenCogCrushingRecipeGen(output, registries));
        GENERATORS.add(new WoodenCogMixingRecipeGen(output, registries));

        gen.addProvider(true, new DataProvider() {

            @Override
            public String getName() {
                return "Woodencog's Processing Recipes";
            }

            @Override
            public CompletableFuture<?> run(CachedOutput dc) {
                return CompletableFuture.allOf(GENERATORS.stream()
                        .map(gen -> gen.run(dc))
                        .toArray(CompletableFuture[]::new));
            }
        });
    }
}
