package net.chauvedev.woodencog.block.transformer;

import com.simibubi.create.content.kinetics.transmission.SplitShaftBlockEntity;
import net.chauvedev.woodencog.config.WoodenCogCommonConfigs;
import net.chauvedev.woodencog.utils.CogUtil;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.RotatingBlockEntity;
import net.dries007.tfc.util.rotation.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class CTTransformerBlockEntity extends SplitShaftBlockEntity implements RotatingBlockEntity {

    private final SourceNode node;
    private boolean invalid = false;
    private final Direction facing;
    private float speed;

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

    //WATER WHEEL BLOCK ENTITY
    @Override
    public void tick() {
        super.tick();
        serverTick();
    }

    public void serverTick() {
        this.checkForLastTickSync();

        clientTick();

        if (level != null && level.getGameTime() % 20 == 0) {
            this.speed = getBlockState().getValue(BlockStateProperties.POWERED) ? 0 : CogUtil.toRadPerTick(getSpeed());
            this.markForSync();
        }
    }

    public void clientTick() {
        final Rotation.Tickable rotation = this.node.rotation();
        rotation.tick();
        rotation.setSpeed(facing == Direction.SOUTH || facing == Direction.EAST || facing == Direction.DOWN ? - this.speed : this.speed);
    }

    protected void onLoadAdditional() {
        performNetworkAction(NetworkAction.ADD_SOURCE);
    }

    protected void onUnloadAdditional() {
        performNetworkAction(NetworkAction.REMOVE);
    }

    @Override
    public void markAsInvalidInNetwork() {
        invalid = true;
    }

    @Override
    public boolean isInvalidInNetwork() {
        return invalid;
    }

    @Override
    public Node getRotationNode() {
        return node;
    }
    // WATER WHEEL BLOCK ENTITY

    //TICKABLE BLOCKENTITY
    protected boolean needsClientUpdate;
    protected boolean isDirty;

    public void checkForLastTickSync() {
        if (needsClientUpdate) {
            // only sync further down when we actually request it to be synced
            needsClientUpdate = false;
            this.tfc_be_markForSync();
        }
        if (isDirty) {
            isDirty = false;
            this.tfc_be_markDirty();
        }
    }

    public void markForSync() {
        needsClientUpdate = true;
    }

    public void markDirty() {
        isDirty = true;
    }
    //TICKABLE BLOCKENTITY

    //TFC BLOCK ENTITY
    @Override
    public final void invalidate() { //"setRemoved" to "invalidate"
        super.invalidate();
        onUnloadAdditional();
    }

    @Override
    public final void onChunkUnloaded() {
        super.onChunkUnloaded();
        onUnloadAdditional();
    }

    @Override
    public final void onLoad() {
        requestModelDataUpdate();
        onLoadAdditional();
    }

    /**
     * Syncs the block entity data to client via means of a block update.
     * Use for stuff that is updated infrequently, for data that is analogous to changing the state.
     */
    public void markForBlockUpdate() {
        if (level != null)
        {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
        }
    }

    /**
     * Marks a block entity for syncing without sending a block update, also internally marks dirty.
     * Use preferentially over {@link InventoryBlockEntity#markForBlockUpdate()} if there's no reason to have a block update.
     */
    public void tfc_be_markForSync() {
        sendVanillaUpdatePacket();
        setChanged();
    }

    /**
     * Marks a block entity as dirty, without updating the comparator output. Use preferentially for updates that want to mark themselves as dirty every tick, and don't require updating comparator output.
     * Reimplements {@link net.minecraft.world.level.Level#blockEntityChanged(BlockPos)} due to trying to avoid comparator updates, called due to MinecraftForge#9169
     */
    @SuppressWarnings("deprecation")
    public void tfc_be_markDirty() {
        if (level != null && level.hasChunkAt(worldPosition)) {
            level.getChunkAt(worldPosition).setUnsaved(true);
        }
    }

    public void sendVanillaUpdatePacket() {
        final ClientboundBlockEntityDataPacket packet = getUpdatePacket();
        final BlockPos pos = getBlockPos();
        if (packet != null && level instanceof ServerLevel serverLevel)
        {
            serverLevel.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false).forEach(e -> e.connection.send(packet));
        }
    }
    //TFC BLOCK ENTITY

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
