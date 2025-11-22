package com.cmdpro.databank;

import com.cmdpro.databank.config.DatabankClientConfig;
import com.cmdpro.databank.misc.RenderingUtil;
import com.cmdpro.databank.mixin.client.BufferSourceMixin;
import com.cmdpro.databank.mixin.client.RenderBuffersMixin;
import com.cmdpro.databank.multiblock.MultiblockRenderer;
import com.cmdpro.databank.rendering.RenderTargetPool;
import com.cmdpro.databank.rendering.ShaderHelper;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.datafixers.util.Function3;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;

import java.awt.*;
import java.util.Map;
import java.util.SequencedMap;

public class ClientDatabankUtils {
    public static void updateWorld() {
        if (Minecraft.getInstance().level == null) {
            return;
        }
        if (ShaderHelper.isSodiumOrSimilarActive() || DatabankClientConfig.forceAlternateChunkReload) {
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
    @Deprecated
    public static void renderAdvancedBeaconBeam(PoseStack pPoseStack, MultiBufferSource pBufferSource, ResourceLocation pBeamLocation, float pPartialTick, float pTextureScale, long pGameTime, Vec3 pointA, Vec3 pointB, Color color, float pBeamRadius, float pGlowRadius) {
        RenderingUtil.renderAdvancedBeaconBeam(pPoseStack, pBufferSource, pBeamLocation, pPartialTick, pTextureScale, pGameTime, pointA, pointB, color, pBeamRadius, pGlowRadius);
    }
    @Deprecated
    public static void rotateStackToPoint(PoseStack pPoseStack, Vec3 pointA, Vec3 pointB) {
        RenderingUtil.rotateStackToPoint(pPoseStack, pointA, pointB);
    }

    @Deprecated
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
        RenderingUtil.renderItemWithColor(pItemStack, pDisplayContext, pLeftHand, pPoseStack, pBufferSource, pCombinedLight, pCombinedOverlay, color, pLevel);
    }
    public static void blitStretched(GuiGraphics graphics, ResourceLocation texture, int blitOffset, int x, int y, int u, int v, int width, int height, int screenWidth, int screenHeight, int textureWidth, int textureHeight) {
        RenderSystem.enableBlend();
        int x2 = x+screenWidth;
        int y2 = y+screenHeight;
        float minU = (u + 0.0F) / (float)textureWidth;
        float maxU = (u + (float)width) / (float)textureWidth;
        float minV = (v + 0.0F) / (float)textureHeight;
        float maxV = (v + (float)height) / (float)textureHeight;
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = graphics.pose().last().pose();
        BufferBuilder bufferbuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y, (float)blitOffset).setUv(minU, minV);
        bufferbuilder.addVertex(matrix4f, (float)x, (float)y2, (float)blitOffset).setUv(minU, maxV);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y2, (float)blitOffset).setUv(maxU, maxV);
        bufferbuilder.addVertex(matrix4f, (float)x2, (float)y, (float)blitOffset).setUv(maxU, minV);
        BufferUploader.drawWithShader(bufferbuilder.buildOrThrow());
        RenderSystem.disableBlend();
    }
    public static void blitStretched(GuiGraphics graphics, ResourceLocation texture, int x, int y, int u, int v, int width, int height, int screenWidth, int screenHeight) {
        blitStretched(graphics, texture, 0, x, y, u, v, width, height, screenWidth, screenHeight, 256, 256);
    }
    public static MultiBufferSource.BufferSource createBufferSourceCopy(BufferSourceCreation create, MultiBufferSource.BufferSource original) {
        BufferSourceMixin mixin = (BufferSourceMixin)original;
        SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers = mixin.getFixedBuffers();
        ByteBufferBuilder sharedBuffer = mixin.getSharedBuffer();
        return create.create(fixedBuffers, sharedBuffer);
    }
    public static MultiBufferSource.BufferSource createBufferSourceCopyFrom(MultiBufferSource.BufferSource original) {
        return createBufferSourceCopy(MultiBufferSource::immediateWithBuffers, original);
    }
    public static MultiBufferSource.BufferSource createMainBufferSourceCopy() {
        RenderBuffers renderBuffers = Minecraft.getInstance().renderBuffers();
        return createBufferSourceCopyFrom(ShaderHelper.needsBufferWorkaround() ? ((RenderBuffersMixin)renderBuffers).getBufferSource() : renderBuffers.bufferSource());
    }
    public static MultiBufferSource.BufferSource createMainBufferSourceCopy(BufferSourceCreation create) {
        RenderBuffers renderBuffers = Minecraft.getInstance().renderBuffers();
        return createBufferSourceCopy(create, ShaderHelper.needsBufferWorkaround() ? ((RenderBuffersMixin)renderBuffers).getBufferSource() : renderBuffers.bufferSource());
    }
    public static int getDrawFrameBufferId() {
        return GL11.glGetInteger(GL33.GL_DRAW_FRAMEBUFFER_BINDING);
    }
    public static int getReadFrameBufferId() {
        return GL11.glGetInteger(GL33.GL_READ_FRAMEBUFFER_BINDING);
    }
    public static boolean isDrawRenderTarget(RenderTarget target) {
        return target.frameBufferId == getDrawFrameBufferId();
    }
    public static boolean isReadRenderTarget(RenderTarget target) {
        return target.frameBufferId == getReadFrameBufferId();
    }
    public interface BufferSourceCreation {
        MultiBufferSource.BufferSource create(SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers, ByteBufferBuilder sharedBuffer);
    }
}
