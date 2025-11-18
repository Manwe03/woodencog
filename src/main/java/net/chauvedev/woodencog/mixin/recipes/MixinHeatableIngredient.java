package net.chauvedev.woodencog.mixin.recipes;

import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = HeatableIngredient.class, remap = false)
public abstract class MixinHeatableIngredient {

    /**
     * @author Manwe
     * @reason Add test for foods without temperature capability
     */
    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    public void test(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (FoodCapability.has(stack) && !cir.getReturnValue() && !stack.isEmpty() && woodencog$test(stack)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "testDefaultItem", at=@At("RETURN"), cancellable = true)
    public void testDefault(ItemStack stack, CallbackInfoReturnable<ItemStack> cir){
        if(cir.getReturnValue() == null && FoodCapability.has(stack)){
            cir.setReturnValue(stack);
            cir.cancel();
        }
    }

    @Unique
    public boolean woodencog$test(@Nullable ItemStack stack) {
        return ((DelegateIngredientAccessor)this).getDelegate() == null || ((DelegateIngredientAccessor)this).getDelegate().test(stack);
    }
}
