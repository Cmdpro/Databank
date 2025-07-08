package com.cmdpro.databank;

import com.cmdpro.databank.hidden.ClientHidden;
import com.cmdpro.databank.hidden.Hidden;
import com.cmdpro.databank.hidden.HiddenManager;
import com.cmdpro.databank.misc.RenderingUtil;
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
}
