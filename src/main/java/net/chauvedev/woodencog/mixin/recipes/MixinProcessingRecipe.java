package net.chauvedev.woodencog.mixin.recipes;

import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.CustomProcessingOutput;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin({ProcessingRecipe.class})
public abstract class MixinProcessingRecipe {

    @Shadow protected NonNullList<ProcessingOutput> results;
    @Shadow protected NonNullList<Ingredient> ingredients;

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public List<ProcessingOutput> getRollableResults() {
        if(!WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()) return this.results;
        System.out.println("GET ROLLABLE RESULTS");
        for (ProcessingOutput processingOutput : this.results) {
            if(processingOutput instanceof CustomProcessingOutput customProcessingOutput){
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
}
