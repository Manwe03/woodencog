package net.chauvedev.woodencog.interaction;

import com.simibubi.create.Create;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.blocks.devices.CrucibleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class CustomArmInteractionPointTypes {
    public static final CrucibleType CRUCIBLE = register("crucible", CrucibleType::new);
    public static final CharcoalForgeType CHARCOAL_FORGE = register("charcoal_forge", CharcoalForgeType::new);

    public CustomArmInteractionPointTypes() {
    }

    private static <T extends ArmInteractionPointType> T register(String id, Function<ResourceLocation, T> factory) {
        T type = (T) factory.apply(Create.asResource(id));
        ArmInteractionPointType.register(type);
        return type;
    }

    public static void registerAll() {
    }

    public static class CrucibleType extends ArmInteractionPointType {
        public CrucibleType(ResourceLocation id) {
            super(id);
        }

        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return state.getBlock() instanceof CrucibleBlock;
        }

        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new ArmInteractionPoint(this, level, pos, state);
        }
    }

    public static class CharcoalForgeType extends ArmInteractionPointType {
        public CharcoalForgeType(ResourceLocation id) {
            super(id);
        }

        public boolean canCreatePoint(Level level, BlockPos pos, BlockState state) {
            return state.getBlock() instanceof CharcoalForgeBlock;
        }

        public ArmInteractionPoint createPoint(Level level, BlockPos pos, BlockState state) {
            return new CharcoalForgePoint(this, level, pos, state);
        }
    }

    public static class CharcoalForgePoint extends ArmInteractionPoint {
        public CharcoalForgePoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state) {
            super(type, level, pos, state);
        }

        @Override
        public ItemStack extract(int slot, int amount, boolean simulate) {
            getHandler();
            return super.extract(slot, amount, simulate);
        }

        @Override
        public ItemStack insert(ItemStack stack, boolean simulate) {
            return super.insert(stack, simulate);
        }

    }
}
