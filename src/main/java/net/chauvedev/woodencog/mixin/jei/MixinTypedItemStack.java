package net.chauvedev.woodencog.mixin.jei;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.library.ingredients.itemStacks.TypedItemStack;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.compat.jei.tfcGuiFix.HeatNormalizedTypedItemStack;
import net.chauvedev.woodencog.compat.jei.tfcGuiFix.HeatTypedItemStack;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TypedItemStack.class, remap = false)
public class MixinTypedItemStack {

    /**
     * @author Manwe
     * @reason Return new TypedItemStacks with temperature persistence
     */
    @Inject(
            method = "create",
            at = @At("HEAD"),
            cancellable = true)
    private static void create(ItemStack ingredient, CallbackInfoReturnable<ITypedIngredient<ItemStack>> cir) {
        ingredient.getCapability(HeatCapability.CAPABILITY).resolve().ifPresent(heat -> {
            WoodenCog.LOGGER.info("Call to create TypedItemStack, " + ingredient.getItem() + " temp: " + heat.getTemperature());
        });
        cir.setReturnValue(ingredient.getCount() == 1 ?
                new HeatNormalizedTypedItemStack(ingredient) :
                new HeatTypedItemStack(ingredient));
    }
}
