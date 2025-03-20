package net.chauvedev.woodencog.mixin.jei;

import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.library.ingredients.itemStacks.NormalizedTypedItemStack;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.compat.jei.tfcGuiFix.HeatNormalizedTypedItemStack;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = NormalizedTypedItemStack.class, remap = false)
public class MixinNormalizedTypedItemStack {

    @Inject(
            method = "normalize",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void normalize(ITypedIngredient<ItemStack> typedIngredient, CallbackInfoReturnable<ITypedIngredient<ItemStack>> cir) {
        cir.setReturnValue(HeatNormalizedTypedItemStack.normalize(typedIngredient));
    }

}
