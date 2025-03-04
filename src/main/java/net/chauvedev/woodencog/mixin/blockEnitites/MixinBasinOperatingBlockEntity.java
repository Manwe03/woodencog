package net.chauvedev.woodencog.mixin.blockEnitites;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.basin.BasinRecipe;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.recipe.RecipeFinder;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.recipes.heatedRecipes.recipes.HeatedBasinRecipe;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mixin(value = BasinOperatingBlockEntity.class, remap = false)
public abstract class MixinBasinOperatingBlockEntity extends KineticBlockEntity {
    public MixinBasinOperatingBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
    }

    /**
     * @author ChauveDev
     * @reason This function didn't take in account the fluid ingredients which is pretty bad in a basin
     */
    @Overwrite
    protected List<Recipe<?>> getMatchingRecipes() {
        if (this.getBasin().map(BasinBlockEntity::isEmpty).orElse(true)) {
            return new ArrayList();
        } else {
            System.out.println(this.getRecipeCacheKey());
            List<Recipe<?>> list = RecipeFinder.get(this.getRecipeCacheKey(), this.level, this::matchStaticFilters);
            System.out.println("GETMATCHIN RECIPES");
            System.out.println(list.size());
            list = list.stream().filter(this::matchBasinRecipe).sorted(this::woodencog$testFluids).collect(Collectors.toList());
            System.out.println(list);
            return list;
        }
    }

    //Add heated and normal test
    @Unique
    private int woodencog$testFluids(Recipe<?> r1, Recipe<?> r2) {
        int r1Size = 0;
        int r2Size = 0;

        if (r1 instanceof BasinRecipe basinR1 && r2 instanceof BasinRecipe basinR2){
            r1Size = (basinR1.getIngredients().size() + basinR1.getFluidIngredients().size());
            r2Size = (basinR2.getIngredients().size() + basinR2.getFluidIngredients().size());
            WoodenCog.LOGGER.info("testFluids BasinRecipe ->"+(r2Size - r1Size));
            return r2Size - r1Size;
        }
        if(r1 instanceof HeatedBasinRecipe basinR1 && r2 instanceof HeatedBasinRecipe basinR2){
            r1Size = (basinR1.getHeatedIngredients().size() + basinR1.getFluidIngredients().size());
            r2Size = (basinR2.getHeatedIngredients().size() + basinR2.getFluidIngredients().size());
            WoodenCog.LOGGER.info("testFluids HeatedBasinRecipe ->"+(r2Size - r1Size));
            return r2Size - r1Size;
        }
        return 0;
    }

    /**
     * @author Manwe
     * @implNote Add call to HeatedBasinRecipe.apply() if necessary, if not continue
     */
    @Inject(
            method = "applyBasinRecipe",
            at = @At("HEAD"),
            cancellable = true
    )
    protected void applyBasinRecipe(CallbackInfo ci) {
        WoodenCog.LOGGER.info("applyBasinRecipe");
        if (this.currentRecipe != null && this.currentRecipe instanceof HeatedBasinRecipe heatedBasinRecipe) {
            Optional<BasinBlockEntity> optionalBasin = this.getBasin();
            if (optionalBasin.isPresent()) {
                BasinBlockEntity basin = (BasinBlockEntity)optionalBasin.get();
                boolean wasEmpty = basin.canContinueProcessing();
                if (HeatedBasinRecipe.apply(basin, heatedBasinRecipe)) {
                    this.invokeGetProceddedRecipeTrigger().ifPresent(this::award);
                    basin.inputTank.sendDataImmediately();
                    if (wasEmpty && this.matchBasinRecipe(heatedBasinRecipe)) {
                        this.invokeContinueWithPreviousRecipe();
                        this.sendData();
                    }

                    basin.notifyChangeOfContents();
                }
            }
            ci.cancel();
        }
    }

    @Invoker("getProcessedRecipeTrigger")
    protected abstract Optional<CreateAdvancement> invokeGetProceddedRecipeTrigger();

    @Invoker("continueWithPreviousRecipe")
    public abstract boolean invokeContinueWithPreviousRecipe();

    @Shadow protected Recipe<?> currentRecipe;

    @Shadow() abstract boolean matchStaticFilters(Recipe<?> recipe);

    /**
     * @author Manwe
     * @reason Mange BasinRecipe.match and HeatedBasinRecipe.match calls
     */
    @Overwrite
    protected <C extends Container> boolean matchBasinRecipe(Recipe<C> recipe) {
        if (recipe == null) {
            return false;
        } else {
            Optional<BasinBlockEntity> basin = this.getBasin();
            if(!basin.isPresent()) return false;
            if (recipe instanceof HeatedBasinRecipe){
                //WoodenCog.LOGGER.info("HeatedBasinRecipe matchBasinRecipe");
                return HeatedBasinRecipe.match(basin.get(),recipe);
            }
            if(recipe instanceof BasinRecipe){
                //WoodenCog.LOGGER.info("BasinRecipe matchBasinRecipe");
                return BasinRecipe.match(basin.get(),recipe);
            }
            return false;
            //return !basin.isPresent() ? false : BasinRecipe.match((BasinBlockEntity)basin.get(), recipe);
        }
    }

    @Shadow() abstract Object getRecipeCacheKey();

    @Shadow() abstract Optional<BasinBlockEntity> getBasin();
}
