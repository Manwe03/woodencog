package net.chauvedev.woodencog.mixin;

import com.simibubi.create.foundation.utility.BlockHelper;
import net.dries007.tfc.common.blockentities.BloomBlockEntity;
import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.CharcoalPileBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(value = BlockHelper.class, remap = false)
public class MixinBlockHelper {
    public MixinBlockHelper() {
    }

    @Redirect(
            method = {"destroyBlockAs(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;FLjava/util/function/Consumer;)V"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/Block;getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;"
            )
    )
    private static List<ItemStack> getDrops(BlockState pState, ServerLevel pLevel, BlockPos pPos, BlockEntity pBlockEntity, Entity pEntity, ItemStack pTool) {
        if (pState.getBlock() instanceof CharcoalPileBlock) {
            Iterator<ItemStack> drops = Block.getDrops(pState, pLevel, pPos, pBlockEntity, pEntity, pTool).iterator();
            int layerCount = pState.getValue(CharcoalPileBlock.LAYERS);
            List<ItemStack> tempList = new ArrayList();
            ItemStack itemStack = drops.next();
            itemStack.setCount(layerCount);
            tempList.add(itemStack);
            return tempList;
        } else {
            if (pState.getBlock() instanceof BloomBlock) {
                int layerCount = pState.getValue(BloomBlock.LAYERS);
                BloomBlockEntity bloomBlockEntity = (BloomBlockEntity)pLevel.getBlockEntity(pPos);
                if (bloomBlockEntity != null) {
                    for(int i = 0; i < layerCount; ++i) {
                        bloomBlockEntity.dropBloom();
                    }
                }
            }

            return Block.getDrops(pState, pLevel, pPos, pBlockEntity, pEntity, pTool);
        }
    }
}
