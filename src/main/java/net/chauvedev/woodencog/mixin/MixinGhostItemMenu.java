package net.chauvedev.woodencog.mixin;

import com.simibubi.create.foundation.gui.menu.GhostItemMenu;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GhostItemMenu.class,remap = false)
public class MixinGhostItemMenu {

    /**
     * Set item to never expire
     * @param instance original item
     * @return copy
     */
    @Redirect(method = "clicked",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack setStackInSlot(ItemStack instance) {
        ItemStack copy = instance.copy();
        FoodCapability.setCreationDate(copy,IFood.NEVER_DECAY_FLAG);
        return copy;
    }

    /**
     * Set item to never expire
     * @param instance original item
     * @return copy
     */
    @Redirect(method = "quickMoveStack",at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;"))
    public ItemStack quickMoveStackCopy(ItemStack instance){
        ItemStack copy = instance.copy();
        FoodCapability.setCreationDate(copy,IFood.NEVER_DECAY_FLAG);
        return copy;
    }
}
