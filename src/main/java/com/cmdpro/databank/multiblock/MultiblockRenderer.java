package com.cmdpro.databank.multiblock;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.Databank;
import com.cmdpro.databank.mixin.client.BufferSourceMixin;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.*;
@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class MultiblockRenderer {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_PARTICLES)) {
            event.getPoseStack().pushPose();
            event.getPoseStack().translate(-event.getCamera().getPosition().x, -event.getCamera().getPosition().y, -event.getCamera().getPosition().z);
            MultiblockRenderer.renderCurrentMultiblock(event.getPoseStack(), event.getPartialTick());
            event.getPoseStack().popPose();
        }
    }
    @SubscribeEvent
    public static void onClientTickPost(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (MultiblockRenderer.multiblock != null) {
            if (MultiblockRenderer.multiblockPos != null) {
                if (MultiblockRenderer.multiblock.checkMultiblock(mc.level, MultiblockRenderer.multiblockPos, MultiblockRenderer.multiblockRotation)) {
                    MultiblockRenderer.multiblock = null;
                    MultiblockRenderer.multiblockPos = null;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            if (MultiblockRenderer.multiblock != null) {
                if (MultiblockRenderer.multiblockPos == null) {
                    MultiblockRenderer.multiblockPos = event.getHitVec().getBlockPos().relative(event.getHitVec().getDirection());
                    MultiblockRenderer.multiblockRotation = MultiblockRenderer.getRotation();
                }
            }
        }
    }
    public static Rotation multiblockRotation;
    public static BlockPos multiblockPos;
    public static Multiblock multiblock;

    public static MultiBufferSource.BufferSource buffers;
    public static void renderBlock(Multiblock multiblock, BlockState block, BlockPos pos, BlockPos worldPos, PoseStack stack, DeltaTracker partialTick) {
        if (buffers == null) {
            buffers = initBuffers(Minecraft.getInstance().renderBuffers().bufferSource());
        }
        renderBlock(multiblock, block, pos, worldPos, stack, partialTick, buffers);
    }
    public static void renderCurrentMultiblock(PoseStack stack, DeltaTracker partialTick) {
        if (multiblock != null) {
            if (multiblockPos == null) {
                if (Minecraft.getInstance().hitResult instanceof BlockHitResult result) {
                    renderMultiblock(multiblock, result.getBlockPos().relative(result.getDirection()), stack, partialTick, getRotation());
                }
            } else {
                renderMultiblock(multiblock, multiblockPos, stack, partialTick, multiblockRotation);
            }
        }
    }

    @NotNull
    public static Rotation getRotation() {
        Rotation rot = Rotation.NONE;
        if (Minecraft.getInstance().player.getDirection().equals(Direction.EAST)) {
            rot = Rotation.CLOCKWISE_90;
        }
        if (Minecraft.getInstance().player.getDirection().equals(Direction.SOUTH)) {
            rot = Rotation.CLOCKWISE_180;
        }
        if (Minecraft.getInstance().player.getDirection().equals(Direction.WEST)) {
            rot = Rotation.COUNTERCLOCKWISE_90;
        }
        return rot;
    }
    public static void renderMultiblock(Multiblock multiblock, BlockPos pos, PoseStack stack, DeltaTracker partialTick) {
        renderMultiblock(multiblock, pos, stack, partialTick, Rotation.NONE);
    }
    public static void renderMultiblock(Multiblock multiblock, BlockPos pos, PoseStack stack, DeltaTracker partialTick, Rotation rotation) {
        if (buffers == null) {
            buffers = initBuffers(Minecraft.getInstance().renderBuffers().bufferSource());
        }
        renderMultiblock(multiblock, pos, stack, partialTick, rotation, buffers);
    }
    public static void renderMultiblock(Multiblock multiblock, BlockPos pos, PoseStack stack, DeltaTracker partialTick, Rotation rotation, MultiBufferSource.BufferSource bufferSource) {
        for (List<List<Multiblock.PredicateAndPos>> i : multiblock.getStates()) {
            for (List<Multiblock.PredicateAndPos> j : i) {
                for (Multiblock.PredicateAndPos k : j) {
                    if (pos != null) {
                        BlockState state = Minecraft.getInstance().level.getBlockState(k.offset.rotate(rotation).offset(pos));
                        boolean stateMatches = k.predicate.isSame(state, rotation);
                        if (!stateMatches) {
                            renderBlock(multiblock, k.predicate.getVisual().rotate(Minecraft.getInstance().level, k.offset, rotation), k.offset, k.offset.rotate(rotation).offset(pos), stack, partialTick, bufferSource);
                        }
                    } else {
                        renderBlock(multiblock, k.predicate.getVisual().rotate(Minecraft.getInstance().level, k.offset, rotation), k.offset, k.offset.rotate(rotation), stack, partialTick, bufferSource);
                    }
                }
            }
        }
        bufferSource.endBatch();
    }
    public static void renderBlock(Multiblock multiblock, BlockState block, BlockPos pos, BlockPos worldPos, PoseStack stack, DeltaTracker partialTick, MultiBufferSource.BufferSource bufferSource) {
        stack.pushPose();
        stack.translate(worldPos.getX(), worldPos.getY(), worldPos.getZ());
        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        FluidState fluidState = block.getFluidState();
        if (!fluidState.isEmpty()) {
            RenderType layer = ItemBlockRenderTypes.getRenderLayer(fluidState);
            VertexConsumer buffer = bufferSource.getBuffer(layer);
            blockRenderer.renderLiquid(pos, multiblock, buffer, block, fluidState);
        }
        if (block.getRenderShape() != RenderShape.INVISIBLE) {
            BakedModel model = blockRenderer.getBlockModel(block);
            for (RenderType i : model.getRenderTypes(block, Minecraft.getInstance().level.random, ModelData.EMPTY)) {
                VertexConsumer hologramConsumer = bufferSource.getBuffer(i);
                blockRenderer.renderBatched(block, pos, multiblock, stack, hologramConsumer, false, Minecraft.getInstance().level.random, ModelData.EMPTY, i);
            }
        }
        if (block.getBlock() instanceof EntityBlock entityBlock) {
            var be = multiblock.blockEntityCache.computeIfAbsent(pos.immutable(), p -> entityBlock.newBlockEntity(p, block));
            if (be != null && !multiblock.erroredBlockEntities.contains(be)) {
                be.setLevel(Minecraft.getInstance().level);

                // fake cached state in case the renderer checks it as we don't want to query the actual world
                be.setBlockState(block);


                try {
                    BlockEntityRenderer<BlockEntity> renderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(be);
                    if (renderer != null) {
                        renderer.render(be, partialTick.getGameTimeDeltaTicks(), stack, bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY);
                    }
                } catch (Exception e) {
                    multiblock.erroredBlockEntities.add(be);
                    Databank.LOGGER.error("[DATABANK ERROR] Error rendering block entity", e);
                }
            }
        }
        stack.popPose();
    }
    private static MultiBufferSource.BufferSource initBuffers(MultiBufferSource.BufferSource original) {
        return ClientDatabankUtils.createMainBufferSourceCopy((fixedBuffers, sharedBuffer) -> {
            SequencedMap<RenderType, ByteBufferBuilder> remapped = new Object2ObjectLinkedOpenHashMap<>();
            for (Map.Entry<RenderType, ByteBufferBuilder> e : fixedBuffers.entrySet()) {
                remapped.put(HologramRenderType.remap(e.getKey()), e.getValue());
            }
            return new HologramBuffers(sharedBuffer, remapped);
        });
    }
    private static class HologramBuffers extends MultiBufferSource.BufferSource {
        protected HologramBuffers(ByteBufferBuilder fallback, SequencedMap<RenderType, ByteBufferBuilder> layerBuffers) {
            super(fallback, layerBuffers);
        }

        @Override
        public VertexConsumer getBuffer(RenderType type) {
            return super.getBuffer(HologramRenderType.remap(type));
        }
    }

    private static class HologramRenderType extends RenderType {
        private static final Map<RenderType, RenderType> remappedTypes = new IdentityHashMap<>();

        private HologramRenderType(RenderType original) {
            super(String.format("%s_%s_hologram", original.toString(), Databank.MOD_ID), original.format(), original.mode(), original.bufferSize(), original.affectsCrumbling(), true, () -> {
                original.setupRenderState();

                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1f, 1f, 1f, 0.5f);
            }, () -> {
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();

                original.clearRenderState();
            });
        }

        public static RenderType remap(RenderType in) {
            if (in instanceof HologramRenderType) {
                return in;
            } else {
                return remappedTypes.computeIfAbsent(in, HologramRenderType::new);
            }
        }
    }
}
