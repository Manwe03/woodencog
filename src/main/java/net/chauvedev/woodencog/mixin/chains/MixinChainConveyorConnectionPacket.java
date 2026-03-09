package net.chauvedev.woodencog.mixin.chains;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorConnectionPacket;
import net.chauvedev.woodencog.blockEntities.ChainConveyorBlockEntityExtended;
import net.chauvedev.woodencog.utils.CogUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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

    //Case: Shift right click chain with Wrench
    @Inject(
        method = "applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;placeItemBackInInventory(Lnet/minecraft/world/item/ItemStack;)V",
            ordinal = 0
        ),
        locals = LocalCapture.CAPTURE_FAILSOFT
    )
    private void beforePlaceItemBackInInventory(ServerPlayer player, ChainConveyorBlockEntity be, CallbackInfo ci, ChainConveyorBlockEntity clbe, int chainCost){
        ItemLike chain = ((ChainConveyorBlockEntityExtended) be).getConnectionsChain().get(targetPos.subtract(be.getBlockPos()));
        player.getInventory().placeItemBackInInventory(new ItemStack(chain == null ? Items.CHAIN : chain, Math.min(chainCost, 64)));
    }

    @Redirect(
        method = "applySettings(Lnet/minecraft/server/level/ServerPlayer;Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/player/Inventory;placeItemBackInInventory(Lnet/minecraft/world/item/ItemStack;)V",
            ordinal = 0
        )
    )
    private void redirectPlaceItemBackInInventory(Inventory instance, ItemStack pStack){
        //Cancel Default Execution
    }
}
