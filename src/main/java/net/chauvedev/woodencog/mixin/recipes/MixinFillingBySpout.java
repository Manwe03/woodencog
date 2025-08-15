package net.chauvedev.woodencog.mixin.recipes;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.fluids.spout.FillingBySpout;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.AllAdvancedRecipeTypes;
import net.chauvedev.woodencog.recipes.advancedProcessingRecipe.baseRecipes.SetItemStackProvider;
import net.dries007.tfc.common.capabilities.MoldLike;
import net.dries007.tfc.util.Metal;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.*;

@Mixin(value = FillingBySpout.class, remap = false)
public class MixinFillingBySpout {
    /**
     * @author ChauveDev
     * @reason Some items from tfc store data as nbt and filling does not check nbt information on recipe
     */
    @Redirect(
            method = "canItemBeFilled",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/AllRecipeTypes;find(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Ljava/util/Optional;"))
    private static <C extends Container, T extends Recipe<C>> Optional<T> canItemBeFilled(AllRecipeTypes instance, C inv, Level world, Level world2, ItemStack stack) {
        if (instance.find(inv, world).isPresent()){
            FillingRecipe recipe = (FillingRecipe) instance.find(inv, world).get();

            boolean is_advanced_recipe = AllAdvancedRecipeTypes.CACHES.containsKey(recipe.getId().toString());
            if(is_advanced_recipe && !(Objects.equals(recipe.getIngredients().get(0).getItems()[0].getTag(), stack.getTag())
                    || stack.getTag() == null || stack.getTag().isEmpty())) {
                return Optional.empty();
            }
        }
        return instance.find(inv, world);
    }

    /**
     * @author ChauveDev
     * @reason Allow advanced recipe on spout filling
     */
    @Redirect(method = "fillItem",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/fluids/transfer/FillingRecipe;rollResults()Ljava/util/List;"))
    private static List<ItemStack> fillItem(FillingRecipe fillingRecipe, Level world, int requiredAmount, ItemStack stack, FluidStack availableFluid) {
        List<ItemStack> results = fillingRecipe.rollResults();

        boolean is_advanced_recipe = AllAdvancedRecipeTypes.CACHES.containsKey(fillingRecipe.getId().toString());
        if(is_advanced_recipe) {
            ArrayList<ItemStack> newStacks = new ArrayList<>();

            FluidStack toFill = availableFluid.copy();
            toFill.setAmount(requiredAmount);

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

            return newStacks;
        }
        return results;
    }
}
