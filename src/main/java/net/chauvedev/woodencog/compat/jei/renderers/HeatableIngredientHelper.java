package net.chauvedev.woodencog.compat.jei.renderers;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class HeatableIngredientHelper implements IIngredientHelper<ItemStack> {

    @Override
    public IIngredientType<ItemStack> getIngredientType() {
        return null;
    }

    @Override
    public String getDisplayName(ItemStack itemStack) {
        return null;
    }

    @Override
    public String getUniqueId(ItemStack itemStack, UidContext uidContext) {
        return null;
    }

    @Override
    public ResourceLocation getResourceLocation(ItemStack itemStack) {
        return null;
    }

    @Override
    public ItemStack copyIngredient(ItemStack itemStack) {
        return itemStack.copy();
    }

    @Override
    public String getErrorInfo(@Nullable ItemStack itemStack) {
        return null;
    }
}
