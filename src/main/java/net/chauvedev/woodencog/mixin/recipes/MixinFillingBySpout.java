package net.chauvedev.woodencog.mixin.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.fluids.transfer.GenericItemFilling;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.AllAdvancedRecipeTypes;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.baseRecipes.SetItemStackProvider;
import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.util.Metal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;
import java.util.function.Predicate;

@Mixin(value = FillingBySpout.class, remap = false)
public class MixinFillingBySpout {

    @Final
    @Shadow()
    private static RecipeWrapper WRAPPER;

    /**
     * @author ChauveDev
     * @reason Some items from tfc store data as nbt and filling does not check nbt informations on recipe
     */
    @Overwrite()
    public static boolean canItemBeFilled(Level world, ItemStack stack) {
        WRAPPER.setItem(0, stack);
        Optional<FillingRecipe> assemblyRecipe = SequencedAssemblyRecipe.getRecipe(world, WRAPPER, AllRecipeTypes.FILLING.getType(), FillingRecipe.class);
        if (assemblyRecipe.isPresent()) {
            return true;
        } else {

            if (AllRecipeTypes.FILLING.find(WRAPPER, world).isPresent()){

                FillingRecipe recipe = (FillingRecipe) AllRecipeTypes.FILLING.find(WRAPPER, world).get();

                boolean is_advanced_recipe = AllAdvancedRecipeTypes.CACHES.containsKey(recipe.getId().toString());
                if(is_advanced_recipe) {
                    return Objects.equals(recipe.getIngredients().get(0).getItems()[0].getTag(), stack.getTag())
                            || stack.getTag() == null || stack.getTag().isEmpty();
                }
            }


            return AllRecipeTypes.FILLING.find(WRAPPER, world).isPresent() ? true : GenericItemFilling.canItemBeFilled(world, stack);
        }
    }

    /**
     * @author ChauveDev
     * @reason  Allow advanced recipe on spout filling
     */
    @Overwrite()
    public static ItemStack fillItem(Level world, int requiredAmount, ItemStack stack, FluidStack availableFluid) {
        FluidStack toFill = availableFluid.copy();
        toFill.setAmount(requiredAmount);
        WRAPPER.setItem(0, stack);
        FillingRecipe fillingRecipe = (FillingRecipe) SequencedAssemblyRecipe.getRecipe(world, WRAPPER, AllRecipeTypes.FILLING.getType(), FillingRecipe.class, matchItemAndFluid(world, availableFluid)).filter((fr) -> {
            return fr.getRequiredFluid().test(toFill);
        }).orElseGet(() -> {
            Iterator var2 = world.getRecipeManager().getRecipesFor(AllRecipeTypes.FILLING.getType(), WRAPPER, world).iterator();

            FillingRecipe fr;
            FluidIngredient requiredFluid;
            do {
                if (!var2.hasNext()) {
                    return null;
                }

                Recipe<RecipeWrapper> recipe = (Recipe)var2.next();
                fr = (FillingRecipe)recipe;
                requiredFluid = fr.getRequiredFluid();
            } while(!requiredFluid.test(toFill));

            return fr;
        });

        if (fillingRecipe != null) {
            List<ItemStack> results = fillingRecipe.rollResults();

            boolean is_advanced_recipe = AllAdvancedRecipeTypes.CACHES.containsKey(fillingRecipe.getId().toString());
            if(is_advanced_recipe) {
                ArrayList<ItemStack> newStacks = new ArrayList<>();

                SetItemStackProvider provider = AllAdvancedRecipeTypes.CACHES.get(fillingRecipe.getId().toString());
                (results).forEach(o -> {
                    ItemStack baseItem = provider.onResultStackSingle(stack.copyWithCount(o.getCount()),o);
                    var mold = MoldLike.get(baseItem);
                    if (mold != null) {
                        mold.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
                        var metal = Metal.get(mold.getFluidInTank(0).getFluid());
                        if (metal != null) mold.setTemperature(metal.getMeltTemperature());
                    }
                    newStacks.add(baseItem);
                });

                results = newStacks;
            }

            availableFluid.shrink(requiredAmount);
            stack.shrink(1);
            return results.isEmpty() ? ItemStack.EMPTY : (ItemStack)results.get(0);
        } else {
            return GenericItemFilling.fillItem(world, requiredAmount, stack, availableFluid);
        }
    }

    /**
     * @author ChauveDev
     * @reason i hate this class, this was needed as it's a static method and class cannot be extended (need cleaning)
     */
    @Overwrite()
    private static Predicate<FillingRecipe> matchItemAndFluid(Level world, FluidStack availableFluid) {
        return (r) -> {
            return r.matches(WRAPPER, world) && r.getRequiredFluid().test(availableFluid);
        };
    }
}
