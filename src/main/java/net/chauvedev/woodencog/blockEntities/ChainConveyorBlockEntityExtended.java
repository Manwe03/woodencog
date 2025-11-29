package net.chauvedev.woodencog.blockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ItemLike;

import java.util.Map;

public interface ChainConveyorBlockEntityExtended {
    void addConnectionToWithChain(BlockPos pos, ItemLike chain);
    Map<BlockPos, ItemLike> getConnectionsChain();
}
