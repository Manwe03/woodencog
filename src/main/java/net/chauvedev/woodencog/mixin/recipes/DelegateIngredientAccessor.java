package net.chauvedev.woodencog.mixin.recipes;

import net.dries007.tfc.common.recipes.ingredients.DelegateIngredient;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = DelegateIngredient.class, remap = false)
public interface DelegateIngredientAccessor {
    @Accessor("delegate")
    Ingredient getDelegate();
}
