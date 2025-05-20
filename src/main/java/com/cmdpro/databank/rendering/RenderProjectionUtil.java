package com.cmdpro.databank.rendering;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.mixin.client.BufferSourceMixin;
import com.cmdpro.databank.mixin.client.RenderBuffersMixin;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.ShaderHelper;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SequencedMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class RenderProjectionUtil {
    private static final RenderTargetPool pool = new RenderTargetPool();
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (ShaderHelper.shouldUseAlternateRendering()) {
            if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_LEVEL)) {
                RenderSystem.getModelViewStack().pushMatrix().set(RenderHandler.matrix4f);
                RenderSystem.applyModelViewMatrix();
                RenderSystem.setShaderFogStart(RenderHandler.fogStart);
                doEffectRendering(event);
                FogRenderer.setupNoFog();
                RenderSystem.getModelViewStack().popMatrix();
                RenderSystem.applyModelViewMatrix();
            }
        } else {
            if (event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_WEATHER)) {
                doEffectRendering(event);
            }
        }
    }
    private static void doEffectRendering(RenderLevelStageEvent event) {
        event.getPoseStack().pushPose();
        event.getPoseStack().translate(-event.getCamera().getPosition().x, -event.getCamera().getPosition().y, -event.getCamera().getPosition().z);
        for (ProjectionRender i : queued) {
            i.apply(event.getPoseStack());
        }
        queued.clear();
        event.getPoseStack().popPose();
    }
    private static final List<ProjectionRender> queued = new ArrayList<>();
    public static void project(Consumer<GuiGraphics> graphics, MultiBufferSource.BufferSource source, PoseStack poseStack, Vec3 from, Vec3 to, int width, int height) {
        project(graphics, source, poseStack, from, to, width, height, true);
    }
    public static void project(Consumer<GuiGraphics> graphics, MultiBufferSource.BufferSource source, PoseStack poseStack, Vec3 from, Vec3 to, int width, int height, boolean queue) {
        ProjectionRender render = new ProjectionRender(graphics, source, from, to, width, height);
        if (queue) {
            queued.add(render);
        } else {
            render.apply(poseStack);
        }
    }
    static MultiBufferSource.BufferSource projectionBufferSource = null;
    private static MultiBufferSource.BufferSource createProjectionBufferSource() {
        if (projectionBufferSource == null) {
            RenderBuffers renderBuffers = Minecraft.getInstance().renderBuffers();
            MultiBufferSource.BufferSource source = ShaderHelper.needsBufferWorkaround() ? ((RenderBuffersMixin)renderBuffers).getBufferSource() : renderBuffers.bufferSource();
            BufferSourceMixin mixin = (BufferSourceMixin)source;
            SequencedMap<RenderType, ByteBufferBuilder> fixedBuffers = mixin.getFixedBuffers();
            ByteBufferBuilder sharedBuffer = mixin.getSharedBuffer();
            projectionBufferSource = MultiBufferSource.immediateWithBuffers(fixedBuffers, sharedBuffer);
        }
        return projectionBufferSource;
    }
    private static class ProjectionRender {
        Consumer<GuiGraphics> graphics;
        MultiBufferSource.BufferSource source;
        Vec3 from;
        Vec3 to;
        int width;
        int height;
        public ProjectionRender(Consumer<GuiGraphics> graphics, MultiBufferSource.BufferSource source, Vec3 from, Vec3 to, int width, int height) {
            this.graphics = graphics;
            this.source = source;
            this.from = from;
            this.to = to;
            this.width = width;
            this.height = height;
        }
        public void apply(PoseStack poseStack) {
            ShaderInstance shader = RenderSystem.getShader();
            float[] color = RenderSystem.getShaderColor();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            Supplier<RenderTarget> targetCreationSupplier = () -> {
                RenderTarget target = new MainTarget(width, height);
                target.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                return target;
            };
            RenderTarget target = pool.getTarget((renderTarget) -> renderTarget.width == width && renderTarget.height == height, targetCreationSupplier);
            ResourceLocation use = pool.generateRandomUseId(Databank.MOD_ID);
            target.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            pool.markUse(target, use);
            MultiBufferSource.BufferSource projectionSource = createProjectionBufferSource();

            Matrix4f projMatrix = new Matrix4f(RenderSystem.getProjectionMatrix());
            VertexSorting sorting = RenderSystem.getVertexSorting();

            Matrix4f matrix4f = new Matrix4f()
                    .setOrtho(
                            0.0F,
                            width,
                            height,
                            0.0F,
                            1000.0F,
                            net.neoforged.neoforge.client.ClientHooks.getGuiFarPlane()
                    );
            RenderSystem.setProjectionMatrix(matrix4f, VertexSorting.ORTHOGRAPHIC_Z);
            Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
            Matrix4f mat = new Matrix4f(matrix4fstack);
            matrix4fstack.popMatrix();
            matrix4fstack.pushMatrix();
            matrix4fstack.translation(0.0F, 0.0F, 10000F - net.neoforged.neoforge.client.ClientHooks.getGuiFarPlane());
            RenderSystem.applyModelViewMatrix();
            Lighting.setupFor3DItems();

            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            target.clear(Minecraft.ON_OSX);
            target.bindWrite(true);
            GuiGraphics guiGraphics = new GuiGraphics(Minecraft.getInstance(), projectionSource);
            graphics.accept(guiGraphics);
            guiGraphics.flush();
            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
            RenderSystem.depthMask(false);

            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
            Lighting.setupLevel();
            RenderSystem.setProjectionMatrix(projMatrix, sorting);
            matrix4fstack.popMatrix();
            matrix4fstack.pushMatrix();
            matrix4fstack.set(mat);
            RenderSystem.applyModelViewMatrix();

            ShaderTypeHandler.SCREEN_PROJECTION.setSampler("ProjectedTarget", target.getColorTextureId());

            Vec3 minPos = new Vec3(Math.min(from.x, to.x), Math.min(from.y, to.y), Math.min(from.z, to.z));
            Vec3 maxPos = new Vec3(Math.max(from.x, to.x), Math.max(from.y, to.y), Math.max(from.z, to.z));
            float minX = (float)minPos.x;
            float minY = (float)minPos.y;
            float minZ = (float)minPos.z;
            float maxX = (float)maxPos.x;
            float maxY = (float)maxPos.y;
            float maxZ = (float)maxPos.z;
            VertexConsumer consumer = source.getBuffer(RenderTypeHandler.SCREEN_PROJECTION);

            poseStack.pushPose();
            PoseStack.Pose pose = poseStack.last();
            consumer.addVertex(pose, minX, maxY, minZ).setUv(0, 0);
            consumer.addVertex(pose, maxX, maxY, maxZ).setUv(1, 0);
            consumer.addVertex(pose, maxX, minY, maxZ).setUv(1, 1);
            consumer.addVertex(pose, minX, minY, minZ).setUv(0, 1);

            consumer.addVertex(pose, minX, minY, minZ).setUv(0, 1);
            consumer.addVertex(pose, maxX, minY, maxZ).setUv(1, 1);
            consumer.addVertex(pose, maxX, maxY, maxZ).setUv(1, 0);
            consumer.addVertex(pose, minX, maxY, minZ).setUv(0, 0);
            poseStack.popPose();

            source.endBatch(RenderTypeHandler.SCREEN_PROJECTION);
            pool.unmarkUse(target, use);
        }
    }
}
