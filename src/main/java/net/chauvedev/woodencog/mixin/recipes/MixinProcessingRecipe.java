package net.chauvedev.woodencog.mixin.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.recipes.IMixinProcessingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingOutput;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@Mixin({ProcessingRecipe.class})
public abstract class MixinProcessingRecipe implements IMixinProcessingRecipe {

    @Shadow protected NonNullList<ProcessingOutput> results;
    @Shadow protected NonNullList<Ingredient> ingredients;

    @Shadow private Supplier<ItemStack> forcedResult;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public List<ProcessingOutput> getRollableResults() {
        if(!WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()) return this.results;
        for (ProcessingOutput processingOutput : this.results) {
            if(processingOutput instanceof HeatedProcessingOutput customProcessingOutput){
                ItemStack outputItem = customProcessingOutput.getStack();
                if (outputItem != null && !outputItem.isEmpty()) {
                    boolean hasHeat = outputItem.getCapability(HeatCapability.CAPABILITY).isPresent();
                    if (!hasHeat) continue;
                    IHeat cap = outputItem.getCapability(HeatCapability.CAPABILITY).resolve().get();
                    cap.setTemperature(customProcessingOutput.getTemperature());
                }
            }
        }
        return this.results;
    }

    public List<ItemStack> rollResultsHeated(List<ProcessingOutput> rollableResults, ItemStack itemStackIn) {
        List<ItemStack> results = new ArrayList();

        for(int i = 0; i < rollableResults.size(); ++i) {
            ProcessingOutput output = rollableResults.get(i);
            ItemStack stack = i == 0 && this.forcedResult != null ? (ItemStack)this.forcedResult.get() : output.rollOutput();
            if (!stack.isEmpty()) {
                if(output instanceof HeatedProcessingOutput heatedProcessingOutput){
                    System.out.println("Instance of HeatedProcessingOutput");
                    if(heatedProcessingOutput.getCopyHeat()) {
                        IHeat heat = HeatCapability.get(itemStackIn);
                        if (heat != null) {
                            HeatCapability.setTemperature(stack, heat.getTemperature() - heatedProcessingOutput.getCooling());
                        }
                    }
                }
                results.add(stack);
            }
        }

        return results;
    }
}
