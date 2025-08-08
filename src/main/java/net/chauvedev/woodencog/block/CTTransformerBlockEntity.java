package net.chauvedev.woodencog.block;

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CTTransformerBlockEntity extends SplitShaftBlockEntity {

    public CTTransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public float getRotationSpeedModifier(Direction face) {
        return this.hasSource() && face != this.getSourceFacing() && (Boolean) this.getBlockState().getValue(BlockStateProperties.POWERED) ? 0.0F : 1.0F;
    }
}
