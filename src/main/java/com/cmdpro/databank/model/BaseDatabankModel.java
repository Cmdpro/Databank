package com.cmdpro.databank.model;

import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public abstract class BaseDatabankModel<T> {
    private static final Vector3f VECTOR_CACHE = new Vector3f();
    public void renderPartAndChildren(T obj, float partialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay, int pColor, ModelPose.ModelPosePart part, Vec3 normalMult) {
        pPoseStack.pushPose();
        if (!part.part.isCube && !part.part.isMesh) {
            pPoseStack.translate((part.pos.x/16f), (part.pos.y/16f), (part.pos.z/16f));
            pPoseStack.mulPose(new Quaternionf().rotationZYX(part.rotation.z, -part.rotation.y, -part.rotation.x));
            pPoseStack.scale(part.scale.x, part.scale.y, part.scale.z);
        }
        if (part.part.isCube || part.part.isMesh) {
            VertexConsumer consumer = pBuffer.getBuffer(getRenderType(obj, part));
            part.render(getModel(), partialTick, pPoseStack, consumer, pPackedLight, pPackedOverlay, pColor, normalMult);
        }
        for (ModelPose.ModelPosePart i : part.children) {
            renderPartAndChildren(obj, partialTick, pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pColor, i, normalMult);
        }
        pPoseStack.popPose();
    }
    public RenderType getRenderType(T obj, ModelPose.ModelPosePart part) {
        return getRenderType(obj);
    }
    public RenderType getRenderType(T obj) {
        return RenderType.entityCutoutNoCull(getTextureLocation());
    }
    public abstract ResourceLocation getTextureLocation();
    public abstract void setupModelPose(T obj, float partialTick);
    public abstract DatabankModel getModel();

    protected ModelPose modelPose;
    protected void animate(DatabankAnimationState state) {
        ModelPose pose = getModel().createModelPose();
        state.update();
        state.getAnim().animation.animationParts.forEach(i -> {
            HashMap<DatabankAnimation.AnimationKeyframe, Keyframe> keyframes = new HashMap<>();
            for (DatabankAnimation.AnimationKeyframe j : i.keyframes) {
                keyframes.put(j, j.createKeyframe());
            }
            Keyframe[] keyframeArray = keyframes.values().stream().sorted(Comparator.comparingDouble(Keyframe::timestamp)).toList().toArray(new Keyframe[0]);
            List<DatabankAnimation.AnimationKeyframe> databankKeyframes = i.keyframes.stream().sorted(Comparator.comparingDouble((animFrame) -> animFrame.timestamp)).toList();
            DatabankAnimation.AnimationKeyframe current = null;
            for (DatabankAnimation.AnimationKeyframe j : databankKeyframes) {
                if (j.timestamp <= state.getProgress()) {
                    current = j;
                }
            }
            boolean forceNextToCurrent = false;
            if (current == null) {
                if (!databankKeyframes.isEmpty()) {
                    current = databankKeyframes.getFirst();
                    forceNextToCurrent = true;
                }
            }
            if (current != null) {
                int currentIndex = databankKeyframes.indexOf(current);
                int nextIndex = forceNextToCurrent ? currentIndex : keyframes.size() > currentIndex + 1 ? currentIndex + 1 : currentIndex;
                DatabankAnimation.AnimationKeyframe next = forceNextToCurrent ? current : databankKeyframes.get(nextIndex);
                Keyframe keyframe = keyframes.get(current);
                Keyframe nextKeyframe = keyframes.get(next);
                AnimationChannel.Interpolation interpolation = keyframe.interpolation();
                float delta = 0f;
                if (currentIndex != nextIndex) {
                    delta = (float) ((state.getProgress() - current.timestamp) / (next.timestamp - current.timestamp));
                }
                interpolation.apply(VECTOR_CACHE, delta, keyframeArray, currentIndex, nextIndex, 1.0f);
                current.targetChannel.apply(pose.stringToPart.get(i.bone), VECTOR_CACHE);
            }
        });
        modelPose = pose;
    }
}
