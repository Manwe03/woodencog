package net.chauvedev.woodencog.block.generator;

import com.simibubi.create.content.kinetics.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.chauvedev.woodencog.block.WoodencogBlockEntityTypes;
import net.chauvedev.woodencog.block.transformer.CTTransformerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class WoodenGeneratorBlock extends DirectionalKineticBlock implements IBE<CTTransformerBlockEntity> {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public WoodenGeneratorBlock(Properties properties) {
        super(properties);
    }


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        super.createBlockStateDefinition(builder);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingDirection().getOpposite())
                .setValue(POWERED, false);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        boolean hasSignal = level.hasNeighborSignal(pos);
        if (hasSignal != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, hasSignal), 3);
        }
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(FACING).getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Class<CTTransformerBlockEntity> getBlockEntityClass() {
        return CTTransformerBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends CTTransformerBlockEntity> getBlockEntityType() {
        return (BlockEntityType) WoodencogBlockEntityTypes.WOODEN_GENERATOR.get();
    }
}
