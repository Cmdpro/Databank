package com.cmdpro.databank.misc;


import com.cmdpro.databank.Databank;
import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class TrailLeftoverHandler {
    private static final HashMap<TrailRender, ExtraTrailData> trailLeftovers = new HashMap<>();
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            poseStack.translate(-event.getCamera().getPosition().x, -event.getCamera().getPosition().y, -event.getCamera().getPosition().z);
            for (Map.Entry<TrailRender, ExtraTrailData> i : new HashMap<>(trailLeftovers).entrySet()) {
                TrailRender trail = i.getKey();
                ExtraTrailData data = i.getValue();
                if (trail.getPositionCount() <= 0) {
                    trailLeftovers.remove(trail);
                    trail.stopTicking();
                } else {
                    trail.render(poseStack, data.bufferSource, data.packedLight, data.gradient);
                }
            }
            poseStack.popPose();
        }
    }
    public static void addTrail(TrailRender trail, MultiBufferSource bufferSource, int packedLight, ColorGradient gradient) {
        if (!trailLeftovers.containsKey(trail)) {
            trailLeftovers.put(trail, new ExtraTrailData(bufferSource, packedLight, gradient));
            trail.startTicking();
        }
    }
    public static void addTrail(TrailRender trail, MultiBufferSource bufferSource, int packedLight, Color color) {
        addTrail(trail, bufferSource, packedLight, ColorGradient.singleColor(color));
    }
    public static void addTrail(TrailRender trail, MultiBufferSource bufferSource, int packedLight, int color) {
        addTrail(trail, bufferSource, packedLight, new Color(color));
    }
    private record ExtraTrailData(MultiBufferSource bufferSource, int packedLight, ColorGradient gradient) {}
}
