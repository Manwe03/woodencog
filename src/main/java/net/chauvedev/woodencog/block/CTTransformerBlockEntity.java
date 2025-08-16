package net.chauvedev.woodencog.block;

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.dries007.tfc.common.blockentities.TickableBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.common.blocks.DirectionPropertyBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.EnumSet;

public class CTTransformerBlockEntity extends SplitShaftBlockEntity implements RotatingBlockEntity {

    TickableBlockEntity TFCbe;
    private final SourceNode node;
    private final Direction facing;
    private boolean invalid;

    public CTTransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.invalid = false;
        this.facing = this.getBlockState().getValue(BlockStateProperties.FACING);

        this.node = new SourceNode(pos, Node.ofAxis(facing.getAxis()), facing, 0.0F) {
            public String toString() {
                return "CT_Transformer[pos=%s, axis=%s]".formatted(this.pos(), facing.getAxis());
            }
        };
    }

    public float getRotationSpeedModifier(Direction face) {
        return this.hasSource() && face != this.getSourceFacing() && (Boolean) this.getBlockState().getValue(BlockStateProperties.POWERED) ? 0.0F : 1.0F;
    }

    @Override
    public float calculateStressApplied() {
        if(this.getBlockState().getValue(BlockStateProperties.POWERED)){
            return 0.0F;
        }
        return WoodenCogCommonConfigs.CT_TRANSFORMER_IMPACT.get();
    }

    //TFC
    @Override
    public void markAsInvalidInNetwork() {
        this.invalid = true;
    }

    @Override
    public boolean isInvalidInNetwork() {
        return this.invalid;
    }

    @Override
    public Node getRotationNode() {
        return this.node;
    }

    @Override
    public void initialize() {
        super.initialize();
        //this.onLoadAdditional();
    }

    @Override
    public void tick() {
        super.tick();
        updateRotationNode();
    }

    private void updateRotationNode() {
        if(this.getBlockState().getValue(BlockStateProperties.POWERED)){
            this.node.rotation().setSpeed(0);
        }else {
            this.node.rotation().setSpeed((float) (this.getSpeed() * (2 * Math.PI / 1200)));
        }

        this.node.rotation().tick();

        if(!RotationNetworkManager.get(level).update(node)) {
            level.destroyBlock(getBlockPos(), true);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.onUnloadAdditional();
    }

    public final void onLoad() {
        super.onLoad();
        this.onLoadAdditional();
    }

    protected void onLoadAdditional() {
        this.performNetworkAction(NetworkAction.ADD_SOURCE);
    }

    protected void onUnloadAdditional() {
        this.performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public void destroy() {
        super.destroy();
        System.out.println("BLOCK ENTITY DESTROYED " + this);
        unloadNode();
    }

    protected void unloadNode(){
        if (invalid) return;
        invalid = true;
        System.out.println("UNLOADED " + this);
        RotationNetworkManager.get(level).remove(this.node);
        this.performNetworkAction(NetworkAction.REMOVE);

        RotationNetworkManager.get(level).update(this.node);
    }
}
