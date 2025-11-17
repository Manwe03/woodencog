package net.chauvedev.woodencog.mixin.recipes;

import net.dries007.tfc.common.recipes.ingredients.HeatableIngredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = HeatableIngredient.class, remap = false)
public interface HeatableIngredientAccessor {
    @Accessor("minTemp") int getMinTemp();
    @Accessor("maxTemp") int getMaxTemp();
}
