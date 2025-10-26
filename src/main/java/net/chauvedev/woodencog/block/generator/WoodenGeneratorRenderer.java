package net.chauvedev.woodencog.block.generator;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

public class WoodenGeneratorRenderer extends KineticBlockEntityRenderer<WoodenGeneratorBlockEntity> {

    public WoodenGeneratorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(WoodenGeneratorBlockEntity blockEntity, float partialTicks, PoseStack ms,
                              MultiBufferSource buffer, int light, int overlay) {
        Direction direction = blockEntity.getBlockState().getValue(FACING);
        VertexConsumer vb = buffer.getBuffer(RenderType.cutoutMipped());

        SuperByteBuffer shaftHalf = CachedBuffers.partialFacing(AllPartialModels.SHAFT_HALF, blockEntity.getBlockState(), direction.getOpposite());

        int lightBehind = 0;
        if(blockEntity.getLevel() != null) lightBehind = LevelRenderer.getLightColor(blockEntity.getLevel(), blockEntity.getBlockPos().relative(direction.getOpposite()));
        standardKineticRotationTransform(shaftHalf, blockEntity, lightBehind).renderInto(ms, vb);
    }
}