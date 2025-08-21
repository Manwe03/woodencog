package net.chauvedev.woodencog.mixin.heat;

import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.fluid.SmartFluidTankBehaviour;
import net.chauvedev.woodencog.mixin.SmartBlockEntityAccessor;
import net.chauvedev.woodencog.recipes.heatedRecipes.ItemHeatingBehaviour;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = BasinBlockEntity.class, remap = false)
public abstract class MixinBasinBlockEntity {

    @Shadow public SmartFluidTankBehaviour inputTank;
    @Shadow private boolean contentsChanged;

    public MixinBasinBlockEntity() {}

    /**
     * @author Manwe
     * AddHeatingBehaviour to basinBlock
     */
    @Inject(method = "<init>",at = @At("RETURN"))
    private void onInit(BlockEntityType type, BlockPos pos, BlockState state, CallbackInfo ci){
        BasinBlockEntity blockEntity = (BasinBlockEntity) (Object) this;
        Map<BehaviourType<?>, BlockEntityBehaviour> behaviours = ((SmartBlockEntityAccessor) blockEntity).getBehaviours();
        ItemHeatingBehaviour itemHeatingBehaviour = new ItemHeatingBehaviour(blockEntity,blockEntity.inputInventory);
        behaviours.put(itemHeatingBehaviour.getType(), itemHeatingBehaviour); //A little hack, directly access behaviour map and add behaviour
    }

    /**
     * @author chauveDev
     * Change input tank Behaviour to handle 4 fluids
     */
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
