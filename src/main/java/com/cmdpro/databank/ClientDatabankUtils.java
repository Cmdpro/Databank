package com.cmdpro.databank;

import com.cmdpro.databank.hidden.ClientHidden;
import com.cmdpro.databank.hidden.Hidden;
import com.cmdpro.databank.hidden.HiddenManager;
import com.cmdpro.databank.mixin.ItemRendererAccessor;
import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.databank.rendering.ShaderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.mojang.math.MatrixUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class ClientDatabankUtils {
    public static void updateWorld() {
        if (ShaderHelper.isSodiumActive()) {
            int viewDistance = Minecraft.getInstance().options.renderDistance().get();
            int max = Minecraft.getInstance().level.getMaxSection();
            int min = Minecraft.getInstance().level.getMinSection();
            ChunkPos playerChunkPos = Minecraft.getInstance().player.chunkPosition();
            for (int x = -viewDistance; x < viewDistance; x++) {
                for (int z = -viewDistance; z < viewDistance; z++) {
                    if (Minecraft.getInstance().level.hasChunk(playerChunkPos.x+x, playerChunkPos.z+z)) {
                        for (int y = min; y < max; y++) {
                            Minecraft.getInstance().levelRenderer.setSectionDirty(playerChunkPos.x+x, y, playerChunkPos.z+z);
                        }
                    }
                }
            }
        } else {
            for (SectionRenderDispatcher.RenderSection i : Minecraft.getInstance().levelRenderer.viewArea.sections) {
                i.setDirty(false);
            }
        }
    }
    public static void renderAdvancedBeaconBeam(PoseStack pPoseStack, MultiBufferSource pBufferSource, ResourceLocation pBeamLocation, float pPartialTick, float pTextureScale, long pGameTime, Vec3 pointA, Vec3 pointB, Color color, float pBeamRadius, float pGlowRadius) {
        float height = (float)pointA.distanceTo(pointB);
        float i = height;
        pPoseStack.pushPose();
        pPoseStack.translate(pointA.x, pointA.y, pointA.z);
        float f = (float)Math.floorMod(pGameTime, 40) + pPartialTick;
        float f1 = height < 0 ? f : -f;
        float f2 = Mth.frac(f1 * 0.2F - (float)Mth.floor(f1 * 0.1F));
        float f3 = (float)color.getRed()/255f;
        float f4 = (float)color.getGreen()/255f;
        float f5 = (float)color.getBlue()/255f;
        pPoseStack.pushPose();
        rotateStackToPoint(pPoseStack, pointA, pointB);
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.YP.rotationDegrees(f * 2.25F - 45.0F));
        float f6 = 0.0F;
        float f8 = 0.0F;
        float f9 = -pBeamRadius;
        float f10 = 0.0F;
        float f11 = 0.0F;
        float f12 = -pBeamRadius;
        float f13 = 0.0F;
        float f14 = 1.0F;
        float f15 = -1.0F + f2;
        float f16 = (float)height * pTextureScale * (0.5F / pBeamRadius) + f15;
        Internals.renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, false)), f3, f4, f5, 1.0F, 0, i, 0.0F, pBeamRadius, pBeamRadius, 0.0F, f9, 0.0F, 0.0F, f12, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
        f6 = -pGlowRadius;
        float f7 = -pGlowRadius;
        f8 = -pGlowRadius;
        f9 = -pGlowRadius;
        f13 = 0.0F;
        f14 = 1.0F;
        f15 = -1.0F + f2;
        f16 = (float)height * pTextureScale + f15;
        Internals.renderPart(pPoseStack, pBufferSource.getBuffer(RenderType.beaconBeam(pBeamLocation, true)), f3, f4, f5, 0.125F, 0, i, f6, f7, pGlowRadius, f8, f9, pGlowRadius, pGlowRadius, pGlowRadius, 0.0F, 1.0F, f16, f15);
        pPoseStack.popPose();
        pPoseStack.popPose();
    }

    public static void rotateStackToPoint(PoseStack pPoseStack, Vec3 pointA, Vec3 pointB) {
        double dX = pointA.x - pointB.x;
        double dY = pointA.y - pointB.y;
        double dZ = pointA.z - pointB.z;
        double yAngle = Math.atan2(0 - dX, 0 - dZ);
        yAngle = yAngle * (180 / Math.PI);
        yAngle = yAngle < 0 ? 360 - (-yAngle) : yAngle;
        pPoseStack.mulPose(Axis.YP.rotationDegrees((float) yAngle + 90));
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));

        double angle = Math.atan2(dY, Math.sqrt(dX * dX + dZ * dZ));
        angle = angle * (180 / Math.PI);
        angle = angle < 0 ? 360 - (-angle) : angle;
        pPoseStack.mulPose(Axis.ZP.rotationDegrees(90 - (float) angle));
    }

    private static final ModelResourceLocation TRIDENT_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("trident"));
    private static final ModelResourceLocation SPYGLASS_MODEL = ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("spyglass"));
    public static void renderItemWithColor(
            ItemStack pItemStack,
            ItemDisplayContext pDisplayContext,
            boolean pLeftHand,
            PoseStack pPoseStack,
            MultiBufferSource pBufferSource,
            int pCombinedLight,
            int pCombinedOverlay,
            Color color,
            Level pLevel
    ) {
        if (!pItemStack.isEmpty()) {
            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            BakedModel pModel = itemRenderer.getModel(pItemStack, pLevel, null, 0);
            pPoseStack.pushPose();
            boolean flag = pDisplayContext == ItemDisplayContext.GUI || pDisplayContext == ItemDisplayContext.GROUND || pDisplayContext == ItemDisplayContext.FIXED;
            if (flag) {
                if (pItemStack.is(Items.TRIDENT)) {
                    pModel = itemRenderer.getItemModelShaper().getModelManager().getModel(TRIDENT_MODEL);
                } else if (pItemStack.is(Items.SPYGLASS)) {
                    pModel = itemRenderer.getItemModelShaper().getModelManager().getModel(SPYGLASS_MODEL);
                }
            }

            pModel = net.neoforged.neoforge.client.ClientHooks.handleCameraTransforms(pPoseStack, pModel, pDisplayContext, pLeftHand);
            pPoseStack.translate(-0.5F, -0.5F, -0.5F);
            if (!pModel.isCustomRenderer() && (!pItemStack.is(Items.TRIDENT) || flag)) {
                boolean flag1;
                if (pDisplayContext != ItemDisplayContext.GUI && !pDisplayContext.firstPerson() && pItemStack.getItem() instanceof BlockItem blockitem) {
                    Block block = blockitem.getBlock();
                    flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
                } else {
                    flag1 = true;
                }

                for (var model : pModel.getRenderPasses(pItemStack, flag1)) {
                    for (var rendertype : model.getRenderTypes(pItemStack, flag1)) {
                        VertexConsumer vertexconsumer;
                        if (ItemRendererAccessor.callHasAnimatedTexture(pItemStack) && pItemStack.hasFoil()) {
                            PoseStack.Pose posestack$pose = pPoseStack.last().copy();
                            if (pDisplayContext == ItemDisplayContext.GUI) {
                                MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.5F);
                            } else if (pDisplayContext.firstPerson()) {
                                MatrixUtil.mulComponentWise(posestack$pose.pose(), 0.75F);
                            }

                            vertexconsumer = itemRenderer.getCompassFoilBuffer(pBufferSource, rendertype, posestack$pose);
                        } else if (flag1) {
                            vertexconsumer = itemRenderer.getFoilBufferDirect(pBufferSource, rendertype, true, pItemStack.hasFoil());
                        } else {
                            vertexconsumer = itemRenderer.getFoilBuffer(pBufferSource, rendertype, true, pItemStack.hasFoil());
                        }

                        Internals.renderModelLists(model, pItemStack, pCombinedLight, pCombinedOverlay, pPoseStack, vertexconsumer, color);
                    }
                }
            } else {
                net.neoforged.neoforge.client.extensions.common.IClientItemExtensions.of(pItemStack).getCustomRenderer().renderByItem(pItemStack, pDisplayContext, pPoseStack, pBufferSource, pCombinedLight, pCombinedOverlay);
            }

            pPoseStack.popPose();
        }
    }
    private static class Internals {
        public static void renderModelLists(BakedModel pModel, ItemStack pStack, int pCombinedLight, int pCombinedOverlay, PoseStack pPoseStack, VertexConsumer pBuffer, Color color) {
            RandomSource randomsource = RandomSource.create();
            long i = 42L;

            for (Direction direction : Direction.values()) {
                randomsource.setSeed(42L);
                renderQuadList(pPoseStack, pBuffer, pModel.getQuads(null, direction, randomsource), pStack, pCombinedLight, pCombinedOverlay, color);
            }

            randomsource.setSeed(42L);
            renderQuadList(pPoseStack, pBuffer, pModel.getQuads(null, null, randomsource), pStack, pCombinedLight, pCombinedOverlay, color);
        }
        public static void renderQuadList(PoseStack pPoseStack, VertexConsumer pBuffer, List<BakedQuad> pQuads, ItemStack pItemStack, int pCombinedLight, int pCombinedOverlay, Color color) {
            boolean flag = !pItemStack.isEmpty();
            PoseStack.Pose posestack$pose = pPoseStack.last();

            for (BakedQuad bakedquad : pQuads) {
                int i = -1;
                if (flag && bakedquad.isTinted()) {
                    i = ((ItemRendererAccessor)Minecraft.getInstance().getItemRenderer()).getItemColors().getColor(pItemStack, bakedquad.getTintIndex());
                }
                int[] rgb1 = ColorUtil.hexToRGB(i);
                int[] rgb2 = ColorUtil.hexToRGB(ColorUtil.RGBtoHex(color));
                float[] floatRGB1 = new float[] { rgb1[0]/255f, rgb1[1]/255f, rgb1[2]/255f, rgb1[3]/255f };
                float[] floatRGB2 = new float[] { rgb2[0]/255f, rgb2[1]/255f, rgb2[2]/255f, rgb2[3]/255f };
                i = ColorUtil.RGBtoHex(new Color(floatRGB1[0]*floatRGB2[0], floatRGB1[1]*floatRGB2[1], floatRGB1[2]*floatRGB2[2],floatRGB1[3]*floatRGB2[3]));

                float f = (float) FastColor.ARGB32.alpha(i) / 255.0F;
                float f1 = (float)FastColor.ARGB32.red(i) / 255.0F;
                float f2 = (float)FastColor.ARGB32.green(i) / 255.0F;
                float f3 = (float)FastColor.ARGB32.blue(i) / 255.0F;
                pBuffer.putBulkData(posestack$pose, bakedquad, f1, f2, f3, f, pCombinedLight, pCombinedOverlay, true);
            }
        }
        public static void renderPart(PoseStack pPoseStack, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, float pMinY, float pMaxY, float pX0, float pZ0, float pX1, float pZ1, float pX2, float pZ2, float pX3, float pZ3, float pMinU, float pMaxU, float pMinV, float pMaxV) {
            PoseStack.Pose posestack$pose = pPoseStack.last();
            renderQuad(posestack$pose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX0, pZ0, pX1, pZ1, pMinU, pMaxU, pMinV, pMaxV);
            renderQuad(posestack$pose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX3, pZ3, pX2, pZ2, pMinU, pMaxU, pMinV, pMaxV);
            renderQuad(posestack$pose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX1, pZ1, pX3, pZ3, pMinU, pMaxU, pMinV, pMaxV);
            renderQuad(posestack$pose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxY, pX2, pZ2, pX0, pZ0, pMinU, pMaxU, pMinV, pMaxV);
        }

        public static void renderQuad(PoseStack.Pose pPose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, float pMinY, float pMaxY, float pMinX, float pMinZ, float pMaxX, float pMaxZ, float pMinU, float pMaxU, float pMinV, float pMaxV) {
            addVertex(pPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMinX, pMinZ, pMaxU, pMinV);
            addVertex(pPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMinX, pMinZ, pMaxU, pMaxV);
            addVertex(pPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMinY, pMaxX, pMaxZ, pMinU, pMaxV);
            addVertex(pPose, pConsumer, pRed, pGreen, pBlue, pAlpha, pMaxY, pMaxX, pMaxZ, pMinU, pMinV);
        }

        public static void addVertex(PoseStack.Pose pose, VertexConsumer pConsumer, float pRed, float pGreen, float pBlue, float pAlpha, float pY, float pX, float pZ, float pU, float pV) {
            pConsumer.addVertex(pose, pX, (float) pY, pZ).setColor(pRed, pGreen, pBlue, pAlpha).setUv(pU, pV).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0.0F, 1.0F, 0.0F);
        }
    }
}
