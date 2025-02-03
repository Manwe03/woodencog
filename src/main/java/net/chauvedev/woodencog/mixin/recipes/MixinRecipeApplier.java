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

import java.util.ArrayList;
import java.util.List;

@Mixin(value = RecipeApplier.class, remap = false)
public class MixinRecipeApplier {

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

    @Overwrite
    public static void applyRecipeOn(ItemEntity entity, Recipe<?> recipe) {
        List<ItemStack> stacks = applyRecipeOn(entity.level(), entity.getItem(), recipe);
        if (stacks == null)
            return;
        if (stacks.isEmpty()) {
            entity.discard();
            return;
        }
        ItemStack itemStack = stacks.remove(0);
        if(WoodenCogCommonConfigs.HANDLE_TEMPERATURE.get()){
            CopyHeatModifier.INSTANCE.apply(itemStack,entity.getItem());
        }
        entity.setItem(itemStack);
        for (ItemStack additional : stacks) {
            ItemEntity entityIn = new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), additional);
            entityIn.setDeltaMovement(entity.getDeltaMovement());
            entity.level().addFreshEntity(entityIn);
        }
    }

    /**
     * @author Manwe
     * @reason Adds call to custom rollResults method that take into account the item input temperature if specified
     * in the recipe as copyHeat = true
     */
    @Overwrite
    public static List<ItemStack> applyRecipeOn(Level level, ItemStack stackIn, Recipe<?> recipe) {
        List<ItemStack> stacks = null;
        if (recipe instanceof ProcessingRecipe<?> pr) {
            stacks = new ArrayList<>();
            for (int i = 0; i < stackIn.getCount(); i++) {
                List<ProcessingOutput> outputs = pr instanceof ManualApplicationRecipe mar ? mar.getRollableResults() : pr.getRollableResults();
                List<ItemStack> rollResultsItemStacks = ((IMixinProcessingRecipe) pr).rollResultsHeated(outputs, stackIn, recipe);
                for (ItemStack stack : rollResultsItemStacks) {
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

            boolean is_advanced_recipe = AllAdvancedRecipeTypes.CACHES.containsKey(pr.getId().toString());
            if (is_advanced_recipe) {
                ArrayList<ItemStack> newStacks = new ArrayList<>();

                SetItemStackProvider provider = AllAdvancedRecipeTypes.CACHES.get(pr.getId().toString());
                stacks.forEach(o -> {
                    newStacks.add(provider.onResultStackSingle(stackIn, (ItemStack) o));
                });
                return newStacks;
            }
        } else {
            ItemStack out = recipe.getResultItem(level.registryAccess())
                    .copy();
            stacks = ItemHelper.multipliedOutput(stackIn, out);
        }

        return stacks;
    }


}