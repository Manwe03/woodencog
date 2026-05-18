package net.chauvedev.woodencog.mixin.recipes;

import net.dries007.tfc.common.recipes.ingredients.HeatIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = HeatIngredient.class, remap = false)
public interface HeatableIngredientAccessor {
    @Accessor("MIN") float getMinTemp();
    @Accessor("MAX") float getMaxTemp();
}
