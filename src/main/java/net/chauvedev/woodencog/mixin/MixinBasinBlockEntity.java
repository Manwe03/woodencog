package net.chauvedev.woodencog.mixin;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BasinBlockEntity.class, remap = false)
public abstract class MixinBasinBlockEntity {

    @Shadow public SmartFluidTankBehaviour inputTank;
    @Shadow private boolean contentsChanged;

    public MixinBasinBlockEntity() {
    }


    @Inject(method="addBehaviours", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/foundation/blockEntity/behaviour/fluid/SmartFluidTankBehaviour;forbidInsertion()Lcom/simibubi/create/foundation/blockEntity/behaviour/fluid/SmartFluidTankBehaviour;"))
    public void addBehaviours(List<BlockEntityBehaviour> behaviours, CallbackInfo ci) {
        this.inputTank = (new SmartFluidTankBehaviour(SmartFluidTankBehaviour.INPUT, (BasinBlockEntity)(Object)this, 4, 1000, true)).whenFluidUpdates(() -> {
            this.contentsChanged = true;
        });
    }

    @Inject(
            method = {"getHeatLevelOf"},
            at = {@At("HEAD")},
            cancellable = true
    )
    private static void getHeatLevelOf(BlockState state, CallbackInfoReturnable<BlazeBurnerBlock.HeatLevel> cir) {
        if (state.getBlock() instanceof CharcoalForgeBlock) {
            int heat = state.getValue(CharcoalForgeBlock.HEAT);
            if (heat >= 7) {
                cir.setReturnValue(BlazeBurnerBlock.HeatLevel.SEETHING);
            } else if (heat >= 3) {
                cir.setReturnValue(BlazeBurnerBlock.HeatLevel.KINDLED);
            } else {
                cir.setReturnValue(BlazeBurnerBlock.HeatLevel.NONE);
            }
        }

    }
}
