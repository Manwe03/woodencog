package net.chauvedev.woodencog.mixin.chains;

import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.BlockEntityAccessor;
import net.chauvedev.woodencog.utils.ChainConveyorBlockEntityExtended;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Mixin(value = ChainConveyorBlockEntity.class, remap = false)
public abstract class MixinChainConveyorBlockEntity implements ChainConveyorBlockEntityExtended {

    @Shadow public abstract boolean forPointsAlongChains(BlockPos connection, int positions, Consumer<Vec3> callback);

    @Unique
    public Map<BlockPos, ItemLike> connectionsChain = new HashMap<>();

    public Map<BlockPos, ItemLike> getConnectionsChain(){
        return connectionsChain;
    }

    private static final ThreadLocal<ItemLike> CURRENT_CHAIN = new ThreadLocal<>();

    //Chain Destroyed
    @Inject(
            method = "chainDestroyed",
            at = @At("HEAD")
    )
    public void chainDestroyed(BlockPos target, boolean spawnDrops, boolean sendEffect, CallbackInfo ci) {
        CURRENT_CHAIN.set(connectionsChain.get(target));
    }

    @Redirect(
        method = "chainDestroyed",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/Block;popResource(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/item/ItemStack;)V"
        )
    )
    private void redirectPopResource(Level pLevel, BlockPos pPos, ItemStack pStack) {
        ItemLike replacement = CURRENT_CHAIN.get() != null ? CURRENT_CHAIN.get() : pStack.getItem();
        Block.popResource(pLevel, pPos, new ItemStack(replacement, pStack.getCount()));
    }

    @Redirect(
        method = "chainDestroyed",
        at = @At(
            value = "INVOKE",
            target = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorBlockEntity;forPointsAlongChains(Lnet/minecraft/core/BlockPos;ILjava/util/function/Consumer;)Z")
    )
    private boolean redirectLambdaChain(ChainConveyorBlockEntity instance, BlockPos target, int chainCount, Consumer<Vec3> vec3Consumer) {
        Level level = instance.getLevel();
        ItemLike replacement = CURRENT_CHAIN.get() != null ? CURRENT_CHAIN.get() : Items.CHAIN;
        return forPointsAlongChains(target,chainCount, vec -> level.addFreshEntity(new ItemEntity(level, vec.x, vec.y, vec.z, new ItemStack(replacement))));
    }
    //Chain Destroyed

    //Add Connection
    public void addConnectionToWithChain(BlockPos target, ItemLike chain) {
        BlockPos localTarget = target.subtract(((BlockEntityAccessor)this).getWorldPosition());
        connectionsChain.put(localTarget, chain);
        System.out.println(((BlockEntityAccessor)this).getWorldPosition() + "Add chain "+localTarget);
    }
    //Add Connection

    //Remove Connection
    @Inject(method = "removeConnectionTo", at = @At("HEAD"))
    public void removeConnectionTo(BlockPos target, CallbackInfoReturnable<Boolean> cir) {
        BlockPos localTarget = target.subtract(((BlockEntityAccessor)this).getWorldPosition());
        connectionsChain.remove(localTarget);
        System.out.println(((BlockEntityAccessor)this).getWorldPosition() + "Remove chain "+localTarget);
    }
    //Remove Connection

    @Inject(method = "write", at = @At("TAIL"))
    protected void write(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, ItemLike> entry : connectionsChain.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.put("Pos", NbtUtils.writeBlockPos(entry.getKey()));
            entryTag.putString("Item", ForgeRegistries.ITEMS.getKey(entry.getValue().asItem()).toString());
            list.add(entryTag);
        }
        compound.put("ChainConnections", list);
    }

    @Inject(method = "read", at = @At("TAIL"))
    protected void read(CompoundTag compound, boolean clientPacket, CallbackInfo ci) {
        connectionsChain.clear();
        ListTag list = compound.getList("ChainConnections", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(entryTag.getCompound("Pos"));
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(entryTag.getString("Item")));
            if (item != null) connectionsChain.put(pos, item);
        }
    }


}