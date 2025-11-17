package net.chauvedev.woodencog.mixin.recipes;

import net.dries007.tfc.common.recipes.ingredients.DelegateIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Objects;

@Mixin(value = DelegateIngredient.class, remap = false)
public abstract class MixinDelegateIngredient {

    @Shadow private ItemStack @Nullable [] cachedItemStacks;

    @Shadow @Final @Nullable protected Ingredient delegate;

    @Shadow protected abstract @Nullable ItemStack testDefaultItem(ItemStack stack);

    @Shadow protected abstract ItemStack[] getDefaultItems();

    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"))
    public void test(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        System.out.println("DelegateIngredient test "+stack.getItem()+" : "+cir.getReturnValue());
    }

    /*
    @Overwrite
    public final ItemStack[] getItems() {
        if (this.cachedItemStacks == null) {
            if (this.delegate != null) {
                System.out.println("Get Items - delegate: "+Arrays.toString(this.delegate.getItems()));
                try {
                    System.out.println(this.delegate.toJson());
                } catch (Exception ignore){}
                this.cachedItemStacks = (ItemStack[]) Arrays.stream(this.delegate.getItems())
                        .map(ItemStack::copy)
                        .map(this::testDefaultItem)
                        .filter(Objects::nonNull)
                        .toArray(ItemStack[]::new);
            } else {
                this.cachedItemStacks = this.getDefaultItems();
                System.out.println("Get Items - default: "+Arrays.toString(cachedItemStacks));
            }
        }

        return this.cachedItemStacks;
    }*/
}
