package net.chauvedev.woodencog.mixin.chains;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorBlockEntity;
import com.simibubi.create.content.kinetics.chainConveyor.ChainConveyorRenderer;
import com.simibubi.create.foundation.render.RenderTypes;
import dev.engine_room.flywheel.lib.transform.PoseTransformStack;
import net.chauvedev.woodencog.blockEntities.ChainConveyorBlockEntityExtended;
import net.chauvedev.woodencog.utils.CogUtil;
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
    public static final ResourceLocation CHAIN_LOCATION = ResourceLocation.withDefaultNamespace("textures/block/chain.png");

    @Shadow
    private static void renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pMaxY, float pX0, float pZ0,
                                   float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV,
                                   float pMaxV, int light1, int light2, boolean far) {}
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
                if(CogUtil.logConditional(rs == null,this.getClass(),"item: "+item+" not found in Registries.BLOCK")) return;
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
    private static void woodencog$renderChain(PoseStack ms, MultiBufferSource buffer, float animation, float length, int light1,
                                                int light2, boolean far, ResourceLocation chainTexture) {
        float radius = far ? 1f / 16f : 1.5f / 16f;
        float minV = far ? 0 : animation;
        float maxV = far ? 1 / 16f : length + minV;
        float minU = far ? 3 / 16f : 0;
        float maxU = far ? 4 / 16f : 3 / 16f;

        ms.pushPose();
        ms.translate(0.5D, 0.0D, 0.5D);

        VertexConsumer vc = buffer.getBuffer(RenderTypes.chain(chainTexture));
        renderPart(ms, vc, length, 0.0F, radius, radius, 0.0F, -radius, 0.0F, 0.0F, -radius, minU, maxU, minV, maxV,
                light1, light2, far);

        ms.popPose();
    }
    //Removed unnecessary implementation of texture dyeing
}
