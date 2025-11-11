package net.chauvedev.woodencog.mixin.chains;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRenderer;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import net.chauvedev.woodencog.utils.ChainConveyorBlockEntityExtended;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
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

    private static final Map<BlockItem,ResourceLocation> CHAIN_RS = new HashMap<>();

    @Shadow
    public static final ResourceLocation CHAIN_LOCATION = new ResourceLocation("textures/block/chain.png");

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
        Item item = chainItem.asItem();

        if (item instanceof BlockItem blockItem) {
            ResourceLocation chainTexture = CHAIN_RS.get(blockItem);
            if(chainTexture == null){
                Block block = blockItem.getBlock();
                ResourceLocation rs = level.registryAccess().registryOrThrow(Registries.BLOCK).getKey(block);
                chainTexture = ResourceLocation.tryBuild(rs.getNamespace(),"textures/block/"+rs.getPath()+".png");
                CHAIN_RS.put(blockItem,chainTexture);
            }
            woodencog$renderChain(ms,buffer,animation, stats.chainLength(), light1, light2, far, chainTexture);
        }else {
            woodencog$renderChain(ms,buffer,animation, stats.chainLength(), light1, light2, far, CHAIN_LOCATION);
        }
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
    private static void woodencog$renderChain(PoseStack ms, MultiBufferSource buffer, float animation, float length, int light1, int light2, boolean far, ResourceLocation chainTexture) {
        float radius = far ? 0.0625F : 0.09375F;
        float minV = far ? 0.0F : animation;
        float maxV = far ? 0.0625F : length + minV;
        float minU = far ? 0.1875F : 0.0F;
        float maxU = far ? 0.25F : 0.1875F;
        ms.pushPose();
        ms.translate(0.5, 0.0, 0.5);
        VertexConsumer vc = buffer.getBuffer(RenderTypes.chain(chainTexture));
        woodencog$renderPart(ms, vc, length, 0.0F, radius, radius, 0.0F, -radius, 0.0F, 0.0F, -radius, minU, maxU, minV, maxV, light1, light2, far);
        ms.popPose();
    }

    @Unique
    private static void woodencog$renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pMaxY, float pX0, float pZ0, float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV, float pMaxV, int light1, int light2, boolean far) {
        PoseStack.Pose posestack$pose = pPoseStack.last();
        Matrix4f matrix4f = posestack$pose.pose();
        Matrix3f matrix3f = posestack$pose.normal();
        float uO = far ? 0.0F : 0.1875F;
        woodencog$renderQuad(matrix4f, matrix3f, pConsumer, 0.0F, pMaxY, pX0, pZ0, pX3, pZ3, pMinU, pMaxU, pMinV, pMaxV, light1, light2);
        woodencog$renderQuad(matrix4f, matrix3f, pConsumer, 0.0F, pMaxY, pX3, pZ3, pX0, pZ0, pMinU, pMaxU, pMinV, pMaxV, light1, light2);
        woodencog$renderQuad(matrix4f, matrix3f, pConsumer, 0.0F, pMaxY, pX1, pZ1, pX2, pZ2, pMinU + uO, pMaxU + uO, pMinV, pMaxV, light1, light2);
        woodencog$renderQuad(matrix4f, matrix3f, pConsumer, 0.0F, pMaxY, pX2, pZ2, pX1, pZ1, pMinU + uO, pMaxU + uO, pMinV, pMaxV, light1, light2);
    }

    @Unique
    private static void woodencog$renderQuad(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pMinY, float pMaxY, float pMinX, float pMinZ, float pMaxX, float pMaxZ, float pMinU, float pMaxU, float pMinV, float pMaxV, int light1, int light2) {
        woodencog$addVertex(pPose, pNormal, pConsumer, pMaxY, pMinX, pMinZ, pMaxU, pMinV, light2);
        woodencog$addVertex(pPose, pNormal, pConsumer, pMinY, pMinX, pMinZ, pMaxU, pMaxV, light1);
        woodencog$addVertex(pPose, pNormal, pConsumer, pMinY, pMaxX, pMaxZ, pMinU, pMaxV, light1);
        woodencog$addVertex(pPose, pNormal, pConsumer, pMaxY, pMaxX, pMaxZ, pMinU, pMinV, light2);
    }

    @Unique
    private static void woodencog$addVertex(Matrix4f pPose, Matrix3f pNormal, VertexConsumer pConsumer, float pY, float pX, float pZ, float pU, float pV, int light) {
        pConsumer.vertex(pPose, pX, pY, pZ).color(1.0F, 1.0F, 1.0F, 1.0F).uv(pU, pV).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(pNormal, 0.0F, 1.0F, 0.0F).endVertex();
    }
}
