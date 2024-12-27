package com.cmdpro.databank.rendering;

import com.cmdpro.databank.Databank;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.SequencedMap;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class RenderHandler {
    public static Matrix4f matrix4f;
    public static float fogStart;
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_PARTICLES)) {
            matrix4f = new Matrix4f(RenderSystem.getModelViewMatrix());
            fogStart = RenderSystem.getShaderFogStart();
        }
        if (ShaderHelper.shouldUseAlternateRendering()) {
            if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_LEVEL)) {
                RenderSystem.getModelViewStack().pushMatrix().set(RenderHandler.matrix4f);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setShaderFogStart(fogStart);
                for (RenderType i : RenderTypeHandler.normalRenderTypes) {
                    createBufferSource().endBatch(i);
                }
                for (RenderType i : RenderTypeHandler.particleRenderTypes) {
                    createBufferSource().endBatch(i);
                }
                FogRenderer.setupNoFog();
                RenderSystem.getModelViewStack().popMatrix();
                RenderSystem.applyModelViewMatrix();
            }
        } else {
            if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER)) {
                for (RenderType i : RenderTypeHandler.normalRenderTypes) {
                    createBufferSource().endBatch(i);
                }
                for (RenderType i : RenderTypeHandler.particleRenderTypes) {
                    createBufferSource().endBatch(i);
                }
            }
        }
    }
    static MultiBufferSource.BufferSource bufferSource = null;
    public static MultiBufferSource.BufferSource createBufferSource() {
        if (bufferSource == null) {
            SequencedMap<RenderType, ByteBufferBuilder> buffers = new Object2ObjectLinkedOpenHashMap<>();
            for (RenderType i : RenderTypeHandler.renderTypes) {
                buffers.put(i, new ByteBufferBuilder(i.bufferSize));
            }
            bufferSource = MultiBufferSource.immediateWithBuffers(buffers, new ByteBufferBuilder(256));
        }
        return bufferSource;
    }
}