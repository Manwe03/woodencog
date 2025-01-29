package net.chauvedev.woodencog.mixin.recipes;

import com.simibubi.create.content.kinetics.deployer.ManualApplicationRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingOutput;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.AllAdvancedRecipeTypes;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.CustomProcessingOutput;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.baseRecipes.SetItemStackProvider;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.outputs.CopyHeatModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemHandlerHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(value = RecipeApplier.class, remap = false)
public class MixinRecipeApplier {

    /**
     * @author Manwe - DeltaAnto
     * @reason Replace method to allow usage of current item not referenced item
     *//*
    @Inject(
        method = "applyRecipeOn(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/crafting/Recipe;)Ljava/util/List;",
        at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/recipe/ProcessingRecipe;rollResults(Ljava/util/List;)Ljava/util/List;"),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private static void injectBeforeRollResults(Level level, ItemStack stackIn, Recipe<?> recipe, CallbackInfoReturnable<List<ItemStack>> cir,List<Object> outputs){
        System.out.println("INJECTION");
        System.out.println(outputs);
        for (Object output : outputs){
            System.out.println("For loop"); //No entra aqui que mierda???
            if (output instanceof CustomProcessingOutput processingOutput){
                System.out.println("CUSTOM PROCESSING OUTPUT");
                if(processingOutput.getCopyHeat()){
                    System.out.println("CopyHeat");
                    boolean hasHeat = stackIn.getCapability(HeatCapability.CAPABILITY).isPresent();
                    if (!hasHeat) continue;
                    CopyHeatModifier.INSTANCE.apply(processingOutput.getStack(),stackIn);
                }
            }
        }
    }*/

    /**
     * @author Manwe - DeltaAnto
     * @reason Replace method to allow usage of current item not referenced item
     *//*
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
    }*/

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static List<ItemStack> applyRecipeOn(Level level, ItemStack stackIn, Recipe<?> recipe) {
        List<ItemStack> stacks;

        if (recipe instanceof ProcessingRecipe<?> pr) {
            stacks = new ArrayList<>();
            for (int i = 0; i < stackIn.getCount(); i++) {
                List<ProcessingOutput> outputs = pr instanceof ManualApplicationRecipe mar ? mar.getRollableResults() : pr.getRollableResults();
                for (ItemStack stack : pr.rollResults(outputs)) {
                    CopyHeatModifier.INSTANCE.apply(stack,stackIn);
                    for (ItemStack previouslyRolled : stacks) {
                        if (stack.isEmpty())
                            continue;
                        if (!ItemHandlerHelper.canItemStacksStack(stack, previouslyRolled))
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
        } else {
            ItemStack out = recipe.getResultItem(level.registryAccess())
                    .copy();
            stacks = ItemHelper.multipliedOutput(stackIn, out);
        }

        if (recipe instanceof ProcessingRecipe<?> pr) {
            boolean is_advanced_recipe = AllAdvancedRecipeTypes.CACHES.containsKey(pr.getId().toString());
            if (is_advanced_recipe) {
                ArrayList<ItemStack> newStacks = new ArrayList<>();

                SetItemStackProvider provider = AllAdvancedRecipeTypes.CACHES.get(pr.getId().toString());
                stacks.forEach(o -> {
                    newStacks.add(provider.onResultStackSingle(stackIn, (ItemStack) o));
                });
                return newStacks;
            }

            List<ProcessingOutput> outputs = pr instanceof ManualApplicationRecipe mar ? mar.getRollableResults() : pr.getRollableResults();
        }

        return stacks;
    }


}