package net.chauvedev.woodencog.datagen;

import com.simibubi.create.foundation.data.recipe.CreateRecipeProvider;
import net.chauvedev.woodencog.WoodenCog;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

public class WoodenCogDatagen {
    public static void gatherData(GatherDataEvent event) {
        if (!event.getMods().contains(WoodenCog.MOD_ID))
            return;

        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        if (event.includeServer()) {
            generator.addProvider(true, new WoodencogRecipeProvider(output, lookupProvider));
            WoodencogRecipeProvider.registerAllProcessing(generator, output, lookupProvider);
        }
    }
}
