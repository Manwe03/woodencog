package net.chauvedev.woodencog.mixin.recipes;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.heat.IHeat;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HeatableIngredient.class, remap = false)
public abstract class MixinHeatableIngredient {

    /**
     * @author
     * @reason
     */
    //@Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    //public void test(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
    //    // cir.getReturnValue() ya contiene el resultado original
    //    boolean originalResult = cir.getReturnValue();
//
    //    if (FoodCapability.has(stack) && originalResult && stack != null && !stack.isEmpty()) {
    //        cir.setReturnValue(true); // fuerza true
    //    }
    //}


}
