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


    private final SourceNode node;
    private boolean invalid = false;
    private final Direction facing;

    public CTTransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.facing = state.getValue(BlockStateProperties.FACING);

        // Creamos un nodo TFC para la red
        this.node = new SourceNode(pos, Node.ofAxis(facing.getAxis()), facing, 0.0F) {
            @Override
            public String toString() {
                return "CT_Transformer[pos=%s, axis=%s]".formatted(this.pos(), facing.getAxis());
            }
        };
    }

    // --------------------
    // Create integration
    // --------------------
    @Override
    public float getRotationSpeedModifier(Direction face) {
        return this.hasSource() && face != this.getSourceFacing() && getBlockState().getValue(BlockStateProperties.POWERED) ? 0.0F : 1.0F;
    }

    @Override
    public float calculateStressApplied() {
        return getBlockState().getValue(BlockStateProperties.POWERED) ? 0.0F : WoodenCogCommonConfigs.CT_TRANSFORMER_IMPACT.get();
    }

    // --------------------
    // TFC integration
    // --------------------
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

    // --------------------
    // Tick / Rotation logic
    // --------------------
    @Override
    public void tick() {
        super.tick();
        updateRotationNode();
    }

    private void updateRotationNode() {
        if(invalid) return;
        if(node.network() == -1L) return;

        if (getBlockState().getValue(BlockStateProperties.POWERED)) {
            node.rotation().setSpeed(0);
        } else {
            // Convertimos la velocidad de Create a rad/s para TFC
            node.rotation().setSpeed((float) (getSpeed() * (2 * Math.PI / 1200)));
        }
        node.rotation().tick();

        /*
        // Actualizamos la red y destruimos el bloque si algo falla
        if (!RotationNetworkManager.get(level).update(node)) {
            level.destroyBlock(getBlockPos(), true);
        }*/
    }

    // --------------------
    // Carga / descarga
    // --------------------
    @Override
    public void onLoad() {
        super.onLoad();
        performNetworkAction(NetworkAction.ADD_SOURCE);
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        //unloadNode();
    }

    @Override
    public void destroy() {
        super.destroy();
        unloadNode();
    }

    public void unloadNode() {
        if (invalid) return;
        invalid = true;

        RotationNetworkManager manager = RotationNetworkManager.get(level);

        manager.remove(node);
        performNetworkAction(NetworkAction.REMOVE);
        manager.update(node);
        setChanged();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return super.getUpdatePacket();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return super.getUpdateTag();
    }

    // --------------------
    // Guardado / carga
    // --------------------
    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        node.rotation().saveToTag(compound);
        compound.putBoolean("invalid", invalid);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        node.rotation().loadFromTag(compound);
        invalid = compound.getBoolean("invalid");
    }
}
