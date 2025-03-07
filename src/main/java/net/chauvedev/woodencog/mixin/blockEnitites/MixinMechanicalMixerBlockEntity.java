package net.chauvedev.woodencog.mixin.blockEnitites;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.content.kinetics.mixer.MechanicalMixerBlockEntity;
import com.simibubi.create.content.kinetics.press.MechanicalPressBlockEntity;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import com.simibubi.create.foundation.utility.Couple;
import com.simibubi.create.infrastructure.config.AllConfigs;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.BasinOperatingBlockEntityAccessor;
import net.chauvedev.woodencog.recipes.heatedRecipes.AllHeatedRecipeTypes;
import net.chauvedev.woodencog.recipes.heatedRecipes.HeatedProcessingRecipe;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.common.crafting.IShapedRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(value = MechanicalMixerBlockEntity.class, remap = false)
public abstract class MixinMechanicalMixerBlockEntity {

    /**
     * @author Manwe
     * @reason Also match for heatedMixing
     */
    @Overwrite
    protected <C extends Container> boolean matchStaticFilters(Recipe<C> r) {
        //WoodenCog.LOGGER.info("matchStaticFilters");
        return (woodencog$checkFilters(r) ||
                (r.getType() == AllRecipeTypes.MIXING.getType() || r.getType() == AllHeatedRecipeTypes.HEATED_MIXING.getType()));
    }

    @Unique
    private <C extends Container> boolean woodencog$checkFilters(Recipe<C> r){
        if(r instanceof CraftingRecipe && !(r instanceof IShapedRecipe<?>) && AllConfigs.server().recipes.allowShapelessInMixer.get() && !MechanicalPressBlockEntity.canCompress(r)){
            if(r instanceof HeatedProcessingRecipe<C> heatedProcessingRecipe){
                return heatedProcessingRecipe.getHeatedIngredients().size() > 1 && !AllHeatedRecipeTypes.shouldIgnoreInAutomation(r);
            }
            return r.getIngredients().size() > 1 && !AllRecipeTypes.shouldIgnoreInAutomation(r);
        }
        return false;
    }

    /**
     * @author Manwe
     * @reason Modify tick logic for heated recipes
     */
    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/processing/basin/BasinOperatingBlockEntity;tick()V",
            shift = At.Shift.AFTER
        ),
        cancellable = true)
    private void tick(CallbackInfo ci){
        MechanicalMixerBlockEntity thisInstance = ((MechanicalMixerBlockEntity)(Object)this);

        if (thisInstance.runningTicks >= 40) {
            thisInstance.running = false;
            thisInstance.runningTicks = 0;
            thisInstance.basinChecker.scheduleUpdate();
        } else {
            float speed = Math.abs(thisInstance.getSpeed());
            if (thisInstance.running && thisInstance.getLevel() != null) {
                if (thisInstance.getLevel().isClientSide && thisInstance.runningTicks == 20) {
                    thisInstance.renderParticles();
                }

                if ((!thisInstance.getLevel().isClientSide || thisInstance.isVirtual()) && thisInstance.runningTicks == 20) {
                    if (thisInstance.processingTicks < 0) {
                        float recipeSpeed = woodencog$getRecipeSpeed();

                        thisInstance.processingTicks = Mth.clamp(Mth.log2((int)(512.0F / speed)) * Mth.ceil(recipeSpeed * 15.0F) + 1, 1, 512);
                        Optional<BasinBlockEntity> basin = ((BasinOperatingBlockEntityAccessor) this).invokeGetBasin();
                        if (basin.isPresent()) {
                            Couple<SmartFluidTankBehaviour> tanks = basin.get().getTanks();
                            if (!tanks.getFirst().isEmpty() || !tanks.getSecond().isEmpty()) {
                                thisInstance.getLevel().playSound(null, thisInstance.getBlockPos(), SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS, 0.75F, speed < 65.0F ? 0.75F : 1.5F);
                            }
                        }
                    } else {
                        --thisInstance.processingTicks;
                        if (thisInstance.processingTicks == 0) {
                            ++thisInstance.runningTicks;
                            thisInstance.processingTicks = -1;
                            ((BasinOperatingBlockEntityAccessor) this).invokeApplyBasinRecipe();
                            thisInstance.sendData();
                        }
                    }
                }

                if (thisInstance.runningTicks != 20) {
                    ++thisInstance.runningTicks;
                }
            }

        }
        ci.cancel();
    }

    @Unique
    private float woodencog$getRecipeSpeed() {
        float recipeSpeed = 1.0F;
        Recipe<?> currentRecipe = ((BasinOperatingBlockEntityAccessor) this).getCurrentRecipe();

        if (currentRecipe instanceof ProcessingRecipe<?> processingRecipe) {
            int t = processingRecipe.getProcessingDuration();
            if (t != 0) {
                recipeSpeed = (float)t / 100.0F;
            }
        }
        if (currentRecipe instanceof HeatedProcessingRecipe<?> processingRecipe){
            int t = processingRecipe.getProcessingDuration();
            if (t != 0) {
                recipeSpeed = (float)t / 100.0F;
            }
        }
        return recipeSpeed;
    }
}
