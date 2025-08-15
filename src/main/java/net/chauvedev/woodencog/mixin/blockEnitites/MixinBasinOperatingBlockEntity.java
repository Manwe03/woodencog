package net.chauvedev.woodencog.mixin.blockEnitites;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedBasinRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Comparator;
import java.util.Optional;

@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public abstract class MixinBasinOperatingBlockEntity extends KineticBlockEntity {
    public MixinBasinOperatingBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    /**
     * @author ChauveDev - yahvk
     * @reason This function didn't take in account the fluid ingredients which is pretty bad in a basin
     */
    @ModifyArg(method = "getMatchingRecipes",
            at = @At(value = "INVOKE", target = "Ljava/util/List;sort(Ljava/util/Comparator;)V"),
            index = 0)
    protected Comparator<? super Recipe<?>> getMatchingRecipes(Comparator<? super Recipe<?>> comparator) {
        return this::woodencog$sort;
    }

    @Unique
    private int woodencog$sort(Recipe<?> r1, Recipe<?> r2) {
        return woodencog$countAllIngredients(r2) - woodencog$countAllIngredients(r1);
    }

    @Unique
    private int woodencog$countAllIngredients(Recipe<?> r) {
        if(r instanceof BasinRecipe recipe) {
            return recipe.getIngredients().size() + recipe.getFluidIngredients().size();
        } else if(r instanceof HeatedBasinRecipe recipe) {
            return recipe.getIngredients().size() + recipe.getFluidIngredients().size();
        }
        return 0;
    }

    /**
     * @author Manwe - yahvk
     * @implNote Add call to HeatedBasinRecipe.apply() if necessary, if not continue
     */
    @Redirect(
            method = "applyBasinRecipe",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;apply(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z")
    )
    protected boolean applyBasinRecipe(BasinBlockEntity basin, Recipe<?> recipe) {
        if (recipe instanceof HeatedBasinRecipe) {
            return HeatedBasinRecipe.apply(basin, recipe);
        } else {
            return BasinRecipe.apply(basin, recipe);
        }
    }

    /**
     * @author Manwe - yahvk
     * @reason Mange BasinRecipe.match and HeatedBasinRecipe.match calls
     */
    @Inject(method = "matchBasinRecipe",
            at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/processing/basin/BasinRecipe;match(Lcom/simibubi/create/content/processing/basin/BasinBlockEntity;Lnet/minecraft/world/item/crafting/Recipe;)Z"),
            cancellable = true)
    private <C extends Container> void matchHeatBasinRecipe(Recipe<C> recipe, CallbackInfoReturnable<Boolean> cir) {
        Optional<BasinBlockEntity> basin = this.getBasin();
        assert basin.isPresent();
        if (recipe instanceof HeatedBasinRecipe){
            cir.setReturnValue(HeatedBasinRecipe.match(basin.get(),recipe));
        }
    }

    @Shadow()
    protected abstract Optional<BasinBlockEntity> getBasin();
}
