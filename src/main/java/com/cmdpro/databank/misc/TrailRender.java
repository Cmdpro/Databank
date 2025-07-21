package com.cmdpro.databank.misc;

import com.cmdpro.databank.rendering.ColorUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TrailRender {
    public Vec3 offset = new Vec3(0, 0, 0);
    public Vec3 position;
    public int segments;
    public int time;
    public float size;
    public ResourceLocation texture;
    public RenderType renderType;
    private final List<Vec3> positions = new ArrayList<>();
    private boolean shrink;
    public TrailRender(Vec3 position, int segments, int time, float size, ResourceLocation texture) {
        this(position, segments, time, size, texture, RenderType.ENTITY_CUTOUT);
    }
    public TrailRender(Vec3 position, int segments, int time, float size, ResourceLocation texture, Function<ResourceLocation, RenderType> renderType) {
        this(position, segments, time, size, texture, renderType.apply(texture));
    }
    public TrailRender(Vec3 position, int segments, int time, float size, ResourceLocation texture, RenderType renderType) {
        this.position = position;
        this.segments = segments;
        this.time = time;
        this.size = size;
        this.texture = texture;
        this.renderType = renderType;
    }
    public TrailRender setShrink(boolean shrink) {
        this.shrink = shrink;
        return this;
    }
    public void render(PoseStack pPoseStack, MultiBufferSource pBufferSource, int packedLight, Gradient gradient) {
        if (positions.isEmpty()) {
            return;
        }
        List<Vector3f> segs = new ArrayList<>();
        segs.add(positions.getFirst().add(offset).toVector3f());
        for (int i = 1; i < segments; i++) {
            segs.add(positions.get((int)(positions.size()*((float)i/(float)segments))).add(offset).toVector3f());
        }
        segs.add(positions.getLast().add(offset).toVector3f());
        VertexConsumer consumer = pBufferSource.getBuffer(renderType);
        int highestSeg = segs.size()-1;
        for (int i = 0; i < segs.size(); i++) {
            Vector3f seg = segs.get(i);
            Vector3f nextSeg = segs.size() > i+1 ? segs.get(i+1) : null;
            if (nextSeg != null) {
                Vector3f segAfterNext = segs.size() > i+2 ? segs.get(i+2) : nextSeg.add(new Vector3f(nextSeg).sub(seg).normalize());
                float wCur = shrink ? 1f-((float)i / (float)highestSeg) : 1f;
                float wNext = shrink ? 1f-((float)(i+1) / (float)highestSeg) : 1f;
                Vector3f currentTrailUpper = getTrailPos(seg, nextSeg, size*wCur);
                Vector3f currentTrailLower = getTrailPos(seg, nextSeg, -size*wCur);
                Vector3f nextTrailUpper = getTrailPos(nextSeg, segAfterNext, size*wNext);
                Vector3f nextTrailLower = getTrailPos(nextSeg, segAfterNext, -size*wNext);
                float uCur = ((float)i / (float)highestSeg);
                float uNext = ((float)(i+1) / (float)highestSeg);
                int colorCur = gradient.getColor((float)i / (float)highestSeg).getRGB();
                int colorNext = gradient.getColor((float)(i+1) / (float)highestSeg).getRGB();
                addVertex(consumer, pPoseStack, currentTrailUpper, uCur, 0f+((1f-wCur)/2f), colorCur, packedLight);
                addVertex(consumer, pPoseStack, nextTrailUpper, uNext, 0f+((1f-wNext)/2f), colorNext, packedLight);
                addVertex(consumer, pPoseStack, nextTrailLower, uNext, 1f-((1f-wNext)/2f), colorNext, packedLight);
                addVertex(consumer, pPoseStack, currentTrailLower, uCur, 1f-((1f-wCur)/2f), colorCur, packedLight);
            }
        }
    }
    public void tick() {
        positions.addFirst(position);
        while (positions.size() > time) {
            positions.removeLast();
        }
    }
    private Vector3f getTrailPos(Vector3f trailCenter, Vector3f nextCenter, float size) {
        float sizeDiff = size/2f;
        Quaternionf quaternionf = new Quaternionf();
        //Vec2 rot = calculateRotationVector(new Vec3(trailCenter.x, trailCenter.y, trailCenter.z), new Vec3(nextCenter.x, nextCenter.y, nextCenter.z));
        //quaternionf.rotateZ((float) Math.toRadians(-rot.y + 180));
        //quaternionf.rotateY((float)Math.toRadians(-rot.x));
        return new Vector3f(trailCenter).add(new Vector3f(0, sizeDiff, 0).rotate(quaternionf));
    }
    private VertexConsumer addVertex(VertexConsumer consumer, PoseStack stack, Vector3f pos, float u, float v, int color, int packedLight) {
        return consumer.addVertex(stack.last(), pos)
                .setColor(color)
                .setUv(u, v)
                .setLight(packedLight)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setNormal(0, 1, 0);
    }/*
    private static Vec2 calculateRotationVector(Vec3 pVec, Vec3 pTarget) {
        double d0 = pTarget.x - pVec.x;
        double d1 = pTarget.y - pVec.y;
        double d2 = pTarget.z - pVec.z;
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        return new Vec2(
                Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))),
                Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F)
        );
    }*/
}
