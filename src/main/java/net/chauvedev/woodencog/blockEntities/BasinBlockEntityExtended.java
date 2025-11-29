package net.chauvedev.woodencog.blockEntities;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmBlock;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.CenteredSideValueBoxTransform;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.INamedIconOptions;
import com.simibubi.create.foundation.blockEntity.behaviour.scrollValue.ScrollOptionBehaviour;
import com.simibubi.create.foundation.gui.AllIcons;
import net.chauvedev.woodencog.mixin.blockEnitites.accessors.BasinBlockEntityAccessor;
import net.createmod.catnip.lang.Lang;
import net.createmod.catnip.math.AngleHelper;
import net.createmod.catnip.math.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public interface BasinBlockEntityExtended {
    ScrollOptionBehaviour getSelectionMode();
    float getHeatSourceTemperature();

    void autoChainRecipes();

    public class SelectionModeValueBox extends CenteredSideValueBoxTransform {
        public SelectionModeValueBox() {
            super((blockState, direction) -> {
                return !direction.getAxis().isVertical();
            });
        }

        public Vec3 getLocalOffset(LevelAccessor level, BlockPos pos, BlockState state) {
            Vec3 location = VecHelper.voxelSpace(8.0, 6, 15.5);
            location = VecHelper.rotateCentered(location, AngleHelper.horizontalAngle(this.getSide()), Direction.Axis.Y);
            return location;
        }

        public float getScale() {
            return super.getScale();
        }
    }

    public static enum SelectionMode implements INamedIconOptions {
        NORMAL(AllIcons.I_NONE),
        AUTO_FEED(AllIcons.I_REFRESH);

        private final String translationKey;
        private final AllIcons icon;

        private SelectionMode(AllIcons icon) {
            this.icon = icon;
            this.translationKey = "woodencog.basin.selection_mode." + Lang.asId(this.name());
        }

        public AllIcons getIcon() {
            return this.icon;
        }

        public String getTranslationKey() {
            return this.translationKey;
        }
    }


}
