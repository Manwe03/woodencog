package net.chauvedev.woodencog.mixin;

import com.simibubi.create.compat.jei.category.CreateRecipeCategory;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.dries007.tfc.util.Metal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({CreateRecipeCategory.class})
public abstract class MixinCreateRecipeCategory {
    public MixinCreateRecipeCategory() {
    }

    @Inject(method = {"getResultItem(Lnet/minecraft/world/item/crafting/Recipe;)Lnet/minecraft/world/item/ItemStack;"}, at = {@At(value = "RETURN")}, cancellable = true, remap = false)
    private static void woodencog$getResultItem(Recipe<?> recipe, CallbackInfoReturnable<ItemStack> cir) {
        var output = cir.getReturnValue();
        if (!output.isEmpty()) {
            if (recipe instanceof FillingRecipe fillingRecipe && fillingRecipe.getId().toString().startsWith("woodencog:advanced_filling/")) {
                var fluidStack = fillingRecipe.getRequiredFluid().getMatchingFluidStacks().get(0);
                var metal = Metal.get(fluidStack.getFluid());
                if (metal != null) {
                    var mold = ItemStackProvider.of(output).getSingleStack(ItemStack.EMPTY);
                    mold.getCapability(HeatCapability.CAPABILITY).ifPresent(cap -> cap.setTemperature(metal.getMeltTemperature()));
                    mold.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).ifPresent(cap -> {
                        if (cap.getFluidInTank(0).getAmount() < fluidStack.getAmount()) {
                            cap.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                        }
                    });
                    cir.setReturnValue(mold);
                }
            }
        }
    }
}
