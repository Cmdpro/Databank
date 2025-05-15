package com.cmdpro.databank.megastructures.block.renderers;

import com.cmdpro.databank.megastructures.block.MegastructureSaveBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.StructureBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.StructureUtils;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.awt.*;

public class MegastructureSaveRenderer implements BlockEntityRenderer<MegastructureSaveBlockEntity> {
    EntityRenderDispatcher renderDispatcher;
    public MegastructureSaveRenderer(BlockEntityRendererProvider.Context rendererProvider) {
        renderDispatcher = rendererProvider.getEntityRenderer();
    }
    @Override
    public void render(MegastructureSaveBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockPos corner1 = blockEntity.corner1;
        BlockPos corner2 = blockEntity.corner2;
        BlockPos center = blockEntity.center;
        Color color = Color.GREEN;
        Color centerColor = Color.WHITE;
        boolean complete = true;
        if (corner1 == null) {
            corner1 = blockEntity.getBlockPos();
            color = Color.RED;
            complete = false;
        }
        if (corner2 == null) {
            corner2 = corner1;
            color = Color.YELLOW;
            complete = false;
        }
        BlockPos minBlock = new BlockPos(
                Math.min(corner1.getX(), corner2.getX()),
                Math.min(corner1.getY(), corner2.getY()),
                Math.min(corner1.getZ(), corner2.getZ())
        );
        BlockPos maxBlock = new BlockPos(
                Math.max(corner1.getX(), corner2.getX()),
                Math.max(corner1.getY(), corner2.getY()),
                Math.max(corner1.getZ(), corner2.getZ())
        );
        Vec3 renderOffset = blockEntity.getBlockPos().getCenter().subtract(0.5f, 0.5f, 0.5f).scale(-1);
        Vec3 min = minBlock.getCenter().add(-0.5f, -0.5f, -0.5f).add(renderOffset);
        Vec3 max = maxBlock.getCenter().add(0.5f, 0.5f, 0.5f).add(renderOffset);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, vertexconsumer, min.x, min.y, min.z, max.x, max.y, max.z, (float)color.getRed()/255f, (float)color.getGreen()/255f, (float)color.getBlue()/255f, 1.0F, ((float)color.getRed()/255f)/2f, ((float)color.getGreen()/255f)/2f, ((float)color.getBlue()/255f)/2f);
        if (complete) {
            if (center != null) {
                Vec3 centerMin = center.getCenter().add(-0.5f, -0.5f, -0.5f).add(renderOffset);
                Vec3 centerMax = center.getCenter().add(0.5f, 0.5f, 0.5f).add(renderOffset);
                LevelRenderer.renderLineBox(poseStack, vertexconsumer, centerMin.x, centerMin.y, centerMin.z, centerMax.x, centerMax.y, centerMax.z, (float)centerColor.getRed()/255f, (float)centerColor.getGreen()/255f, (float)centerColor.getBlue()/255f, 1.0F, ((float)centerColor.getRed()/255f)/2f, ((float)centerColor.getGreen()/255f)/2f, ((float)centerColor.getBlue()/255f)/2f);
            }
        }
        if (bufferSource instanceof MultiBufferSource.BufferSource source) {
            source.endBatch(RenderType.lines());
        }
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        if (complete) {
            renderInvisibleBlocks(blockEntity, bufferSource, poseStack);
        }
    }
    private void renderInvisibleBlocks(MegastructureSaveBlockEntity blockEntity, MultiBufferSource bufferSource, PoseStack poseStack) {
        BlockGetter blockgetter = blockEntity.getLevel();
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RenderType.lines());
        BlockPos blockpos = blockEntity.getBlockPos();
        BlockPos corner1 = blockEntity.corner1;
        BlockPos corner2 = blockEntity.corner2;

        for (BlockPos i : BlockPos.betweenClosed(corner1, corner2)) {
            BlockState blockstate = blockgetter.getBlockState(i);
            boolean structureVoid = blockstate.is(Blocks.STRUCTURE_VOID);
            boolean barrier = blockstate.is(Blocks.BARRIER);
            boolean light = blockstate.is(Blocks.LIGHT);
            if (structureVoid || barrier || light) {
                double d0 = (double)((float)(i.getX() - blockpos.getX()) + 0.45F);
                double d1 = (double)((float)(i.getY() - blockpos.getY()) + 0.45F);
                double d2 = (double)((float)(i.getZ() - blockpos.getZ()) + 0.45F);
                double d3 = (double)((float)(i.getX() - blockpos.getX()) + 0.55F);
                double d4 = (double)((float)(i.getY() - blockpos.getY()) + 0.55F);
                double d5 = (double)((float)(i.getZ() - blockpos.getZ()) + 0.55F);
                if (structureVoid) {
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer, d0, d1, d2, d3, d4, d5, 1.0F, 0.75F, 0.75F, 1.0F, 1.0F, 0.75F, 0.75F);
                } else if (barrier) {
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer, d0, d1, d2, d3, d4, d5, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F);
                } else if (light) {
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer, d0, d1, d2, d3, d4, d5, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F);
                }
            }
        }
    }
}
