package net.chauvedev.woodencog.block.generator;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import net.chauvedev.woodencog.block.WoodencogBlocks;
import net.chauvedev.woodencog.utils.RotationUtil;
import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.WindmillBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.SourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.IItemHandler;

public class WoodenGeneratorBlockEntity extends GeneratingKineticBlockEntity {

    private final Direction facing;
    private float speed;
    private float generatedCapacity;
    private float stressMultiplyer = 1;

    public WoodenGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.facing = state.getValue(BlockStateProperties.FACING);
        setLazyTickRate(20);
    }

    @Override
    public void lazyTick() {
        super.lazyTick();

        Direction back = facing.getOpposite();
        BlockPos targetPos = worldPosition.relative(back);
        BlockPos oppositeGenerator = worldPosition.relative(back,2);

        BlockEntity be = level.getBlockEntity(targetPos);

        if(level.getBlockState(oppositeGenerator).getBlock() == WoodencogBlocks.WOODEN_GENERATOR.get()){
            level.destroyBlock(oppositeGenerator, true);
        }

        if(be instanceof RotatingBlockEntity rotatingBlock && rotatingBlock.getRotationNode() instanceof SourceNode sourceNode) {
            this.speed = - RotationUtil.toRPM(sourceNode.rotation().speed());
            if(rotatingBlock instanceof WindmillBlockEntity windmill){
                IItemHandler inventory = Helpers.getCapability(windmill, Capabilities.ITEM);
                int rusticWindmillCount = 0;
                for(int i = 0; i < inventory.getSlots(); i++){
                    ItemStack stack = inventory.getStackInSlot(i);
                    if(stack.getItem() == TFCItems.RUSTIC_WINDMILL_BLADE.get()) rusticWindmillCount++;
                }
                this.stressMultiplyer = rusticWindmillCount == 5 ? 8 : 4;
            } else {
                this.stressMultiplyer = 1;
            }
        } else {
            this.speed = 0;
        }

        updateGeneratedRotation();

        int discreteSpeed = (int) Math.ceil(Math.abs(this.speed))/4;
        this.generatedCapacity = discreteSpeed * 64 * stressMultiplyer;
    }

    @Override
    public float calculateAddedStressCapacity() {
        this.lastCapacityProvided = this.generatedCapacity;
        return this.generatedCapacity;
    }

    @Override
    public float getGeneratedSpeed() {
        if(getBlockState().getValue(BlockStateProperties.POWERED) || this.generatedCapacity <= 0.0F){
            return 0.0F;
        }
        return Math.copySign(8F,this.speed);
    }

    @Override
    public float calculateStressApplied() {
        return 0;
    }
}
