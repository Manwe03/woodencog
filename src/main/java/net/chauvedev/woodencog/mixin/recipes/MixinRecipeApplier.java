package net.chauvedev.woodencog.mixin.recipes;

import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.recipes.IMixinProcessingRecipe;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.AllAdvancedRecipeTypes;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.baseRecipes.SetItemStackProvider;
import net.dries007.tfc.common.recipes.outputs.CopyHeatModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = RecipeApplier.class, remap = false)
public abstract class MixinRecipeApplier {
    /**
     * @author DeltaAnto - Manwe
     * @reason Replace method to allow usage of current item not referenced item
     */
    @Inject(
            method = "applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;)Ljava/util/List;",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true
    )
    private static void onApplyRecipeOnAtReturn(Level level, ItemStack stackIn, Recipe<?> recipe, CallbackInfoReturnable<List<ItemStack>> cir, List stacks, ItemStack out) {
        //Handles the recipe if (advanced recipe)
        if (recipe instanceof ProcessingRecipe<?> pr) {
            boolean is_advanced_recipe = AllAdvancedRecipeTypes.CACHES.containsKey(pr.getId().toString());
            if (is_advanced_recipe) {
                ArrayList<ItemStack> newStacks = new ArrayList<>();

                SetItemStackProvider provider = AllAdvancedRecipeTypes.CACHES.get(pr.getId().toString());
                stacks.forEach(o -> {
                    newStacks.add(provider.onResultStackSingle(stackIn, (ItemStack) o));
                });
                cir.setReturnValue(newStacks);
                cir.cancel();//cancel - if it is an advanced recipe this should be the only mixin that handles it, so we cancel.
            }

            List<ProcessingOutput> outputs = pr instanceof ManualApplicationRecipe mar ? mar.getRollableResults() : pr.getRollableResults();
        }
    }
}