package com.cmdpro.databank.rendering;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.mixin.client.BufferSourceMixin;
import com.cmdpro.databank.mixin.client.RenderBuffersMixin;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
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
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

import java.util.ArrayList;
import java.util.List;
import java.util.SequencedMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class RenderProjectionUtil {
    private static final RenderTargetPool pool = new RenderTargetPool();
    @SubscribeEvent(priority = EventPriority.HIGHEST)
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
    public static void project(Consumer<GuiGraphics> graphics, MultiBufferSource.BufferSource source, Vec3 topLeft, Vec3 topRight, Vec3 bottomRight, Vec3 bottomLeft, int width, int height) {
        project(graphics, (stack) -> {}, (stack) -> {}, source, null, topLeft, topRight, bottomRight, bottomLeft, width, height, true);
    }
    public static void project(Consumer<GuiGraphics> graphics, Consumer<PoseStack> applyPoseStackTransformations, Consumer<PoseStack> undoPoseStackTransformations, MultiBufferSource.BufferSource source, Vec3 topLeft, Vec3 topRight, Vec3 bottomRight, Vec3 bottomLeft, int width, int height) {
        project(graphics, applyPoseStackTransformations, undoPoseStackTransformations, source, null, topLeft, topRight, bottomRight, bottomLeft, width, height, true);
    }
    public static void project(Consumer<GuiGraphics> graphics, Consumer<PoseStack> applyPoseStackTransformations, Consumer<PoseStack> undoPoseStackTransformations, MultiBufferSource.BufferSource source, PoseStack poseStack, Vec3 topLeft, Vec3 topRight, Vec3 bottomRight, Vec3 bottomLeft, int width, int height) {
        project(graphics, applyPoseStackTransformations, undoPoseStackTransformations, source, poseStack, topLeft, topRight, bottomRight, bottomLeft, width, height, true);
    }
    public static void project(Consumer<GuiGraphics> graphics, Consumer<PoseStack> applyPoseStackTransformations, Consumer<PoseStack> undoPoseStackTransformations, MultiBufferSource.BufferSource source, PoseStack poseStack, Vec3 topLeft, Vec3 topRight, Vec3 bottomRight, Vec3 bottomLeft, int width, int height, boolean queue) {
        ProjectionRender render = new ProjectionRender(graphics, applyPoseStackTransformations, undoPoseStackTransformations, source, topLeft, topRight, bottomRight, bottomLeft, width, height);
        if (queue) {
            queued.add(render);
        } else if (poseStack != null) {
            render.apply(poseStack);
        }
    }
    public static void renderTarget(RenderTarget target, MultiBufferSource.BufferSource source, PoseStack poseStack, Vec3 topLeft, Vec3 topRight, Vec3 bottomRight, Vec3 bottomLeft) {
        ShaderTypeHandler.SCREEN_PROJECTION.setSampler("ProjectedTarget", target.getColorTextureId());

        float tlX = (float)topLeft.x;
        float tlY = (float)topLeft.y;
        float tlZ = (float)topLeft.z;

        float trX = (float)topRight.x;
        float trY = (float)topRight.y;
        float trZ = (float)topRight.z;

        float brX = (float)bottomRight.x;
        float brY = (float)bottomRight.y;
        float brZ = (float)bottomRight.z;

        float blX = (float)bottomLeft.x;
        float blY = (float)bottomLeft.y;
        float blZ = (float)bottomLeft.z;
        VertexConsumer consumer = source.getBuffer(RenderTypeHandler.SCREEN_PROJECTION);

        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();
        consumer.addVertex(pose, tlX, tlY, tlZ).setUv(0, 0);
        consumer.addVertex(pose, trX, trY, trZ).setUv(1, 0);
        consumer.addVertex(pose, brX, brY, brZ).setUv(1, 1);
        consumer.addVertex(pose, blX, blY, blZ).setUv(0, 1);
        poseStack.popPose();

        source.endBatch(RenderTypeHandler.SCREEN_PROJECTION);
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
        Consumer<PoseStack> applyPoseStackTransformations;
        Consumer<PoseStack> undoPoseStackTransformations;
        MultiBufferSource.BufferSource source;
        Vec3 topLeft;
        Vec3 topRight;
        Vec3 bottomRight;
        Vec3 bottomLeft;
        int width;
        int height;
        public ProjectionRender(Consumer<GuiGraphics> graphics, Consumer<PoseStack> applyPoseStackTransformations, Consumer<PoseStack> undoPoseStackTransformations, MultiBufferSource.BufferSource source, Vec3 topLeft, Vec3 topRight, Vec3 bottomRight, Vec3 bottomLeft, int width, int height) {
            this.graphics = graphics;
            this.applyPoseStackTransformations = applyPoseStackTransformations;
            this.undoPoseStackTransformations = undoPoseStackTransformations;
            this.source = source;
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomRight = bottomRight;
            this.bottomLeft = bottomLeft;
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
            float fogStart = RenderSystem.getShaderFogStart();

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
            FogRenderer.setupNoFog();

            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            target.clear(Minecraft.ON_OSX);
            target.bindWrite(true);
            GuiGraphics guiGraphics = new GuiGraphics(Minecraft.getInstance(), projectionSource);
            graphics.accept(guiGraphics);
            guiGraphics.flush();
            Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
            RenderSystem.depthMask(false);

            RenderSystem.setShaderFogStart(fogStart);
            RenderSystem.setShader(() -> shader);
            RenderSystem.setShaderColor(color[0], color[1], color[2], color[3]);
            Lighting.setupLevel();
            RenderSystem.setProjectionMatrix(projMatrix, sorting);
            matrix4fstack.popMatrix();
            matrix4fstack.pushMatrix();
            matrix4fstack.set(mat);
            RenderSystem.applyModelViewMatrix();

            poseStack.pushPose();

            List<Vec3> vectors = new ArrayList<>();
            vectors.add(topLeft);
            vectors.add(topRight);
            vectors.add(bottomRight);
            vectors.add(bottomLeft);

            Vec3 combinedVec = new Vec3(0, 0, 0);
            for (Vec3 i : vectors) {
                combinedVec = combinedVec.add(i);
            }
            Vec3 middle = combinedVec.scale(1.0f/(float)vectors.size());
            Vec3 topLeft = middle.subtract(this.topLeft);
            Vec3 topRight = middle.subtract(this.topRight);
            Vec3 bottomRight = middle.subtract(this.bottomRight);
            Vec3 bottomLeft = middle.subtract(this.bottomLeft);

            poseStack.translate(middle.x, middle.y, middle.z);
            poseStack.pushPose();
            applyPoseStackTransformations.accept(poseStack);
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderProjectionUtil.renderTarget(target, source, poseStack, topLeft, topRight, bottomRight, bottomLeft);
            RenderSystem.depthMask(false);
            RenderSystem.disableDepthTest();
            undoPoseStackTransformations.accept(poseStack);
            poseStack.popPose();
            poseStack.popPose();

            pool.unmarkUse(target, use);
        }
    }
}
