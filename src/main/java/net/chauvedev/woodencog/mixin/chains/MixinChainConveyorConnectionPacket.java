package net.chauvedev.woodencog.mixin.chains;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionPacket;
import net.chauvedev.woodencog.blockEntities.ChainConveyorBlockEntityExtended;
import net.chauvedev.woodencog.utils.CogUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChainConveyorConnectionPacket.class, remap = false)
public class MixinChainConveyorConnectionPacket {
    @Shadow private ItemStack chain;

    @Shadow private BlockPos targetPos;

    @Inject(
        method = "applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;addConnectionTo(Lnet/minecraft/core/BlockPos;)Z",
            ordinal = 0
        )
    )
    private void beforeClbeAddConnectionTo(ServerPlayer player, ChainConveyorBlockEntity be, CallbackInfo ci) {
        if(CogUtil.logConditional(be.getLevel() == null,this.getClass(),"block entity level is null")) return;

        if(!(be.getLevel().getBlockEntity(targetPos) instanceof ChainConveyorBlockEntity clbe)) return; //FAIL
        ((ChainConveyorBlockEntityExtended) clbe).addConnectionToWithChain(be.getBlockPos(), chain.getItem());
    }

    @Inject(
        method = "applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;addConnectionTo(Lnet/minecraft/core/BlockPos;)Z",
            ordinal = 1
        )
    )
    private void beforeBeAddConnectionTo(ServerPlayer player, ChainConveyorBlockEntity be, CallbackInfo ci) {
        ((ChainConveyorBlockEntityExtended) be).addConnectionToWithChain(targetPos, chain.getItem());
    }
}
