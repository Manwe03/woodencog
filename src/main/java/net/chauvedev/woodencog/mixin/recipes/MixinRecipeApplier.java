package net.chauvedev.woodencog.mixin.recipes;

import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.chauvedev.woodencog.recipes.heatedRecipes.output.DynamicProcessingOutput;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = RecipeApplier.class, remap = false)
public abstract class MixinRecipeApplier {

    /**
     * @author Manwe
     * @reason Inject handling heated recipes in world (pressing recipes)
     */
    @Inject(
            method = "applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;Z)Ljava/util/List;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void onApplyRecipeOnAtHead(Level level, ItemStack stackIn, Recipe<?> recipe, boolean returnProcessingRemainder, CallbackInfoReturnable<List<ItemStack>> cir) {
        List<ItemStack> stacks;
        if (recipe instanceof HeatedProcessingRecipe<?,?> pr) {
            float inputTemp = 0;
            if(HeatCapability.get(stackIn) != null){
                inputTemp = HeatCapability.get(stackIn).getTemperature();
            }

            stacks = new ArrayList<>();
            for (int i = 0; i < stackIn.getCount(); i++) {
                List<DynamicProcessingOutput<?>> outputs = pr.getRollableResults(); //get HeatedOutputs
                for (ItemStack stack : pr.rollResults(outputs,level.random, inputTemp)) {
                    for (ItemStack previouslyRolled : stacks) {
                        if (stack.isEmpty())
                            continue;
                        if (!ItemStack.isSameItemSameComponents(stack, previouslyRolled))
                            continue;
                        int amount = Math.min(previouslyRolled.getMaxStackSize() - previouslyRolled.getCount(),
                                stack.getCount());
                        previouslyRolled.grow(amount);
                        stack.shrink(amount);
                    }

                    if (stack.isEmpty())
                        continue;

                    stacks.add(stack);
                }
            }
            cir.setReturnValue(stacks);
            cir.cancel();
        }
    }


    /**
     * @author DeltaAnto - Manwe
     * @reason Replace method to allow usage of current item not referenced item
     *//*TODO support, rework or remove advanced recipes
    @Inject(
            method = "applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;Z)Ljava/util/List;",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true
    )
    private static void onApplyRecipeOnAtReturn(Level level, ItemStack stackIn, Recipe<?> recipe, boolean returnProcessingRemainder, CallbackInfoReturnable<List<ItemStack>> cir, List<ItemStack> stacks, ItemStack out) {
        //Handles the recipe if (advanced recipe)
        if (recipe instanceof ProcessingRecipe<?,?> pr) {
            boolean is_advanced_recipe = AllAdvancedRecipeTypes.CACHES.containsKey(pr.getId().toString());
            if (is_advanced_recipe) {
                ArrayList<ItemStack> newStacks = new ArrayList<>();

                SetItemStackProvider provider = AllAdvancedRecipeTypes.CACHES.get(pr.getId().toString());
                stacks.forEach(itemStack -> newStacks.add(provider.onResultStackSingle(stackIn, itemStack)));
                cir.setReturnValue(newStacks);
                cir.cancel();//cancel - if it is an advanced recipe this should be the only mixin that handles it, so we cancel.
            }
        }
    }*/
}