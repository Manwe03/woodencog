package net.chauvedev.woodencog.mixin.chains;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRenderer;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import net.chauvedev.woodencog.WoodenCog;
import net.chauvedev.woodencog.utils.ChainConveyorBlockEntityExtended;
import net.chauvedev.woodencog.utils.Color;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Metal;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(value = ChainConveyorRenderer.class, remap = false)
public abstract class MixinChainConveyorRenderer {

    private static final ResourceLocation CHAIN_BW_LOCATION = WoodenCog.asResource("textures/block/chain_bw.png");

    private static final Map<Item, Integer> CHAIN_ITEM_TO_METAL_COLOR = new HashMap<>(); //Metal cache

    @Unique
    private static int woodencog$getMetalColorFromChain(Item item) {
        Integer metalColor = CHAIN_ITEM_TO_METAL_COLOR.get(item); //Cached color
        if(metalColor != null) return metalColor;

        for (var entry : TFCBlocks.METALS.entrySet()) {
            var byType = entry.getValue();
            var maybeChain = byType.get(Metal.BlockType.CHAIN);
            if (maybeChain != null && maybeChain.get().asItem() == item) {
                int color = entry.getKey().getColor();
                CHAIN_ITEM_TO_METAL_COLOR.put(item,color); //Metal color
                return color;
            }
        }

        CHAIN_ITEM_TO_METAL_COLOR.put(item,0xFF252c3d); //Default color
        return 0xFF252c3d;
    }

    @Shadow @Final public static ResourceLocation CHAIN_LOCATION;

    @Inject(
            method = "renderChains",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorRenderer;renderChain(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FFIIZ)V"
            ),
            locals = LocalCapture.CAPTURE_FAILSOFT
    )
    void beforeRenderChain(ChainConveyorBlockEntity be, PoseStack ms, MultiBufferSource buffer,
                           int light, int overlay, CallbackInfo ci, float time, float animation,
                           Iterator var8, BlockPos blockPos, ChainConveyorBlockEntity.ConnectionStats stats,
                           Vec3 diff, double yaw, double pitch, Level level, BlockPos tilePos, Vec3 startOffset,
                           PoseTransformStack chain, int light1, int light2, boolean far){

        Map<BlockPos, ItemLike> chainMap = ((ChainConveyorBlockEntityExtended)be).getConnectionsChain();
        ItemLike chainItem = chainMap.get(blockPos);

        int chainColor = woodencog$getMetalColorFromChain(chainItem.asItem());

        woodencog$renderChain(ms,buffer,animation, stats.chainLength(), light1, light2, far, Color.modify(chainColor,1.5f,10));
    }

    @Redirect(
            method = "renderChains",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/content/kinetics/chainConveyor/ChainConveyorRenderer;renderChain(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;FFIIZ)V"
            )
    )
    void cancelRenderChain(PoseStack ms, MultiBufferSource buffer, float animation, float length, int light1, int light2, boolean far){
        //Remove default execution
    }

    @Unique
    private static void woodencog$renderChain(PoseStack ms, MultiBufferSource buffer, float animation, float length, int light1, int light2, boolean far, int chainColor) {
        float radius = far ? 1f / 16f : 1.5f / 16f;
        float minV = far ? 0 : animation;
        float maxV = far ? 1 / 16f : length + minV;
        float minU = far ? 3 / 16f : 0;
        float maxU = far ? 4 / 16f : 3 / 16f;

        ms.pushPose();
        ms.translate(0.5D, 0.0D, 0.5D);

        VertexConsumer vc = buffer.getBuffer(RenderTypes.chain(CHAIN_BW_LOCATION));
        woodencog$renderPart(ms, vc, length, 0.0F, radius, radius, 0.0F, -radius, 0.0F, 0.0F, -radius, minU, maxU, minV, maxV,
                light1, light2, far, chainColor);

        ms.popPose();
    }

    private static void woodencog$renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pMaxY, float pX0, float pZ0,
                                   float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV,
                                   float pMaxV, int light1, int light2, boolean far, int chainColor) {
        PoseStack.Pose posestack$pose = pPoseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();

        float uO = far ? 0f : 3 / 16f;
        woodencog$renderQuad(matrix4f, matrix3f, pConsumer, 0, pMaxY, pX0, pZ0, pX3, pZ3, pMinU, pMaxU, pMinV, pMaxV, light1,
                light2, chainColor);
        woodencog$renderQuad(matrix4f, matrix3f, pConsumer, 0, pMaxY, pX3, pZ3, pX0, pZ0, pMinU, pMaxU, pMinV, pMaxV, light1,
                light2, chainColor);
        woodencog$renderQuad(matrix4f, matrix3f, pConsumer, 0, pMaxY, pX1, pZ1, pX2, pZ2, pMinU + uO, pMaxU + uO, pMinV, pMaxV,
                light1, light2, chainColor);
        woodencog$renderQuad(matrix4f, matrix3f, pConsumer, 0, pMaxY, pX2, pZ2, pX1, pZ1, pMinU + uO, pMaxU + uO, pMinV, pMaxV,
                light1, light2, chainColor);
    }

    private static void woodencog$renderQuad(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pMinY, float pMaxY,
                                   float pMinX, float pMinZ, float pMaxX, float pMaxZ, float pMinU, float pMaxU, float pMinV, float pMaxV,
                                   int light1, int light2, int chainColor) {
        woodencog$addVertex(pPose, pNormal, pConsumer, pMaxY, pMinX, pMinZ, pMaxU, pMinV, light2, chainColor);
        woodencog$addVertex(pPose, pNormal, pConsumer, pMinY, pMinX, pMinZ, pMaxU, pMaxV, light1, chainColor);
        woodencog$addVertex(pPose, pNormal, pConsumer, pMinY, pMaxX, pMaxZ, pMinU, pMaxV, light1, chainColor);
        woodencog$addVertex(pPose, pNormal, pConsumer, pMaxY, pMaxX, pMaxZ, pMinU, pMinV, light2, chainColor);
    }

    private static void woodencog$addVertex(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pY, float pX,
                                  float pZ, float pU, float pV, int light, int chainColor) {
        pConsumer.vertex(pPose, pX, pY, pZ)
                .color(chainColor)
                .uv(pU, pV)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pNormal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

}
