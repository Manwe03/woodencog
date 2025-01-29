package net.chauvedev.woodencog.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface IMixinProcessingRecipe {
    List<ItemStack> rollResultsHeated(List<ProcessingOutput> rollableResults, ItemStack itemStackIn);
}
