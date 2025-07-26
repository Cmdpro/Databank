package com.cmdpro.databank.impact;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.misc.FloatGradient;
import com.cmdpro.databank.misc.ResizeHelper;
import com.cmdpro.databank.mixin.client.BufferSourceMixin;
import com.cmdpro.databank.multiblock.MultiblockRenderer;
import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.databank.shaders.PostShaderManager;
import com.mojang.blaze3d.pipeline.MainTarget;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SequencedMap;

public class ImpactFrameHandler {
    static {
        ResizeHelper.addListener((width, height) -> {
            getImpactTarget().resize(width, height, Minecraft.ON_OSX);
            getFrozenImpactTarget().resize(width, height, Minecraft.ON_OSX);
        });
    }
    public static final ImpactShader defaultShader = new ImpactShader();
    public static ImpactFrame impactFrame;
    public static class ImpactFrame {
        public int startTicks;
        public int ticks;
        public ImpactShader shader;
        public FloatGradient alpha;
        protected List<ImpactData> impactData = new ArrayList<>();
        protected ImpactFrame(int startTicks, ImpactShader shader, FloatGradient alpha) {
            this.startTicks = startTicks;
            this.ticks = startTicks;
            this.shader = shader;
            this.alpha = alpha;
        }
        protected void tick() {
            ticks--;
        }
        public ImpactFrame withFlashes(float[] flashes, float flashTime) {
            ImpactFrameHandler.withFlashes(this, flashes, flashTime);
            return this;
        }
        public float getProgress(float partialTick) {
            float maxProgress = (float) ImpactFrameHandler.impactFrame.startTicks / 20f;
            return ((float)(startTicks-ticks)/20f) / maxProgress;
        }
    }
    public static ImpactFrame addImpact(int ticks, ImpactRender frozenRender, ImpactRender dynamicRender, FloatGradient alpha, boolean merge, ImpactShader shader) {
        impactData.add(new ImpactData(frozenRender, dynamicRender, merge));
        reset = false;
        if (impactFrame != null) {
            impactFrame.shader.setActive(false);
        }
        impactFrame = new ImpactFrame(ticks, shader, alpha);
        shader.setActive(true);
        return impactFrame;
    }
    public static ImpactFrame addImpact(int ticks, ImpactRender frozenRender, ImpactRender dynamicRender, FloatGradient alpha) {
        return addImpact(ticks, frozenRender, dynamicRender, alpha, false, defaultShader);
    }
    public static ImpactFrame addImpact(int ticks, ImpactRender frozenRender, ImpactRender dynamicRender, FloatGradient alpha, boolean merge) {
        return addImpact(ticks, frozenRender, dynamicRender, alpha, merge, defaultShader);
    }
    public static ImpactFrame addImpact(int ticks, ImpactRender frozenRender, ImpactRender dynamicRender, FloatGradient alpha, ImpactShader shader) {
        return addImpact(ticks, frozenRender, dynamicRender, alpha, false, shader);
    }
    public static ImpactFrame withFlashes(ImpactFrame original, float[] flashes, float flashTime) {
        withFlashes(original.alpha, original.startTicks, flashes, flashTime);
        return original;
    }
    public static FloatGradient withFlashes(FloatGradient original, float seconds, float[] flashes, float flashTime) {
        float flash = flashTime / seconds;
        for (float i : flashes) {
            float progress = i / seconds;
            float alpha = original.getValue(progress);
            original.addPoint(0f, progress, true);
            original.addPoint(alpha, progress + flash, true);
        }
        return original;
    }
    public static FloatGradient withFlashes(FloatGradient original, int ticks, float[] flashes, float flashTime) {
        return withFlashes(original, (float)ticks/20f, flashes, flashTime);
    }

    private static final List<ImpactData> impactData = new ArrayList<>();
    private static RenderTarget impactTarget;
    protected static RenderTarget getImpactTarget() {
        if (impactTarget == null) {
            impactTarget = new MainTarget(Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height);
        }
        return impactTarget;
    }
    private static RenderTarget frozenImpactTarget;
    protected static RenderTarget getFrozenImpactTarget() {
        if (frozenImpactTarget == null) {
            frozenImpactTarget = new MainTarget(Minecraft.getInstance().getMainRenderTarget().width, Minecraft.getInstance().getMainRenderTarget().height);
        }
        return frozenImpactTarget;
    }
    private static MultiBufferSource.BufferSource bufferSource;
    private static MultiBufferSource.BufferSource initBuffers(MultiBufferSource.BufferSource original) {
        BufferSourceMixin mixin = (BufferSourceMixin)original;
        var fallback = mixin.getSharedBuffer();
        var fixedBuffers = mixin.getFixedBuffers();
        return MultiBufferSource.immediateWithBuffers(fixedBuffers, fallback);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    protected static class GameEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Pre event) {
            if (impactFrame != null) {
                impactFrame.tick();
                if (impactFrame.ticks <= 0) {
                    impactFrame.shader.setActive(false);
                    impactFrame = null;
                    reset = false;
                }
            }
        }
        @SubscribeEvent
        public static void onRender(RenderLevelStageEvent event) {
            if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
                if (impactFrame != null) {
                    if (!impactData.isEmpty()) {
                        getFrozenImpactTarget().clear(Minecraft.ON_OSX);
                        for (ImpactData data : impactData) {
                            renderData(data, false, event);
                        }
                        impactFrame.impactData.addAll(impactData);
                        impactData.clear();
                    }
                    if (!impactFrame.shader.isActive()) {
                        impactFrame.shader.setActive(true);
                    }
                    if (impactFrame.alpha.getValue(impactFrame.shader.getTime()) <= 0.1) {
                        reset = true;
                    } else if (reset) {
                        getFrozenImpactTarget().clear(Minecraft.ON_OSX);
                        for (ImpactData data : impactData) {
                            renderData(data, false, event);
                        }
                        reset = false;
                    }
                    getImpactTarget().clear(Minecraft.ON_OSX);
                    getImpactTarget().copyDepthFrom(frozenImpactTarget);
                    for (ImpactData i : impactFrame.impactData) {
                        renderData(i, true, event);
                    }
                }
            }
        }
    }
    @EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    protected static class ModEvents {
        @SubscribeEvent
        public static void doSetup(FMLClientSetupEvent event) {
            PostShaderManager.addShader(defaultShader);
        }
    }
    public interface ImpactRender {
        void renderFrame(RenderTarget target, MultiBufferSource bufferSource, PoseStack poseStack, DeltaTracker tracker, Camera camera, Frustum frustum, int renderTicks, float progress);
    }
    protected static void renderData(ImpactData data, boolean dynamic, RenderLevelStageEvent event) {
        RenderTarget impactTarget = dynamic ? getImpactTarget() : getFrozenImpactTarget();
        if (!data.merge) {
            impactTarget.clear(Minecraft.ON_OSX);
        }
        impactTarget.bindWrite(true);
        event.getPoseStack().pushPose();
        Vec3 pos = event.getCamera().getPosition();
        event.getPoseStack().pushPose();
        event.getPoseStack().translate(-pos.x, -pos.y, -pos.z);
        if (bufferSource == null) {
            bufferSource = initBuffers(Minecraft.getInstance().renderBuffers().bufferSource());
        }
        (dynamic ? data.renderDynamic : data.renderFrozen).renderFrame(
                impactTarget,
                bufferSource,
                event.getPoseStack(),
                event.getPartialTick(),
                event.getCamera(),
                event.getFrustum(),
                event.getRenderTick(),
                impactFrame.getProgress(event.getPartialTick().getGameTimeDeltaPartialTick(true))
        );
        bufferSource.endBatch();
        event.getPoseStack().popPose();
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
    }
    protected record ImpactData(ImpactRender renderFrozen, ImpactRender renderDynamic, boolean merge) {}
    private static boolean reset;
}
