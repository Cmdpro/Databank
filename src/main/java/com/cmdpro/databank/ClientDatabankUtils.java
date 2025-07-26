package com.cmdpro.databank;

import com.cmdpro.databank.config.DatabankClientConfig;
import com.cmdpro.databank.misc.RenderingUtil;
import com.cmdpro.databank.mixin.client.BufferSourceMixin;
import com.cmdpro.databank.mixin.client.RenderBuffersMixin;
import com.cmdpro.databank.multiblock.MultiblockRenderer;
import com.cmdpro.databank.rendering.ShaderHelper;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Function3;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
    public interface BufferSourceCreation {
        MultiBufferSource.BufferSource create(SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers, ByteBufferBuilder sharedBuffer);
    }
}
