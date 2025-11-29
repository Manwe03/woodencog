package net.chauvedev.woodencog.blockEntities;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface BasinBlockEntityExtended {
    float getHeatSourceTemperature();

    public class SelectionModeValueBox extends CenteredSideValueBoxTransform {
        public SelectionModeValueBox() {
            super((blockState, direction) -> {
                return !direction.getAxis().isVertical();
            });
        }

        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            int yPos = (Boolean)state.getValue(ArmBlock.CEILING) ? 13 : 3;
            Vec3 location = VecHelper.voxelSpace(8.0, (double)yPos, 15.5);
            location = VecHelper.rotateCentered(location, (double) AngleHelper.horizontalAngle(this.getSide()), Direction.Axis.Y);
            return location;
        }

        public float getScale() {
            return super.getScale();
        }
    }
}
