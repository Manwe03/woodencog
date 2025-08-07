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

    @ModifyArg(method = "getMatchingRecipes",
            at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;sorted(Ljava/util/Comparator;)Ljava/util/stream/Stream;"),
            index = 0)
    protected Comparator<? super Recipe<?>> getMatchingRecipes(Comparator<? super Recipe<?>> comparator) {
        return this::woodencog$testFluids;
    }

    //Add heated and normal test
    @Unique
    private int woodencog$testFluids(Recipe<?> r1, Recipe<?> r2) {
        int r1Size;
        int r2Size;

        if (r1 instanceof BasinRecipe basinR1 && r2 instanceof BasinRecipe basinR2){
            r1Size = (basinR1.getIngredients().size() + basinR1.getFluidIngredients().size());
            r2Size = (basinR2.getIngredients().size() + basinR2.getFluidIngredients().size());
            //WoodenCog.LOGGER.info("testFluids BasinRecipe ->"+(r2Size - r1Size));
            return r2Size - r1Size;
        }
        if(r1 instanceof HeatedBasinRecipe basinR1 && r2 instanceof HeatedBasinRecipe basinR2){
            r1Size = (basinR1.getHeatedIngredients().size() + basinR1.getFluidIngredients().size());
            r2Size = (basinR2.getHeatedIngredients().size() + basinR2.getFluidIngredients().size());
            //WoodenCog.LOGGER.info("testFluids HeatedBasinRecipe ->"+(r2Size - r1Size));
            return r2Size - r1Size;
        }
        return 0;
    }

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

    @Shadow protected Recipe<?> currentRecipe;

    @Shadow protected abstract boolean matchStaticFilters(Recipe<?> recipe);

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
    protected abstract Object getRecipeCacheKey();

    @Shadow()
    protected abstract Optional<BasinBlockEntity> getBasin();
}
