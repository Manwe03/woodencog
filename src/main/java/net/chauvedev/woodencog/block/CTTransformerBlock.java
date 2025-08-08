package net.chauvedev.woodencog.block;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CTTransformerBlock extends DirectionalKineticBlock implements IBE<CTTransformerBlockEntity> {

    public CTTransformerBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return ((Direction) blockState.getValue(FACING)).getAxis();
    }

    @Override
    public Class<CTTransformerBlockEntity> getBlockEntityClass() {
        return CTTransformerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CTTransformerBlockEntity> getBlockEntityType() {
        return (BlockEntityType) WoodencogBlockEntityTypes.CT_TRANSFORMER.get();
    }
}
