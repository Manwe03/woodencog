package net.chauvedev.woodencog.mixin.blockEnitites;

import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(value = MechanicalPressBlockEntity.class, remap = false)
public abstract class MixinMechanicalPressBlockEntity {

    /**
     * @author Manwe
     * @reason Also match heated compacting
     */
    @Inject( method = "matchStaticFilters", at = @At("RETURN"), cancellable = true)
    protected <C extends Container> void matchStaticFilters(RecipeHolder<? extends Recipe<?>> recipe, CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue() && recipe.value().getType() == AllHeatedRecipeTypes.HEATED_COMPACTING.getType()) cir.setReturnValue(true);
    }

    /**
     * @author Manwe
     * @implNote More general method, return pressing and heatedPressing recipes
     */
    @Inject( method = "getRecipe", at = @At("RETURN"), cancellable = true)
    public void getRecipe(ItemStack item, CallbackInfoReturnable<Optional<RecipeHolder<PressingRecipe>>> cir) {

        Level level = ((BlockEntity) (Object) this).getLevel();
        if (level != null) {
            Optional<RecipeHolder<Recipe<SingleRecipeInput>>> heatedPressingRecipe = AllHeatedRecipeTypes.HEATED_PRESSING.find(new SingleRecipeInput(item), level);
            if (heatedPressingRecipe.isPresent())
                cir.setReturnValue((Optional<RecipeHolder<PressingRecipe>>) (Object) heatedPressingRecipe);
        }
    }
}
