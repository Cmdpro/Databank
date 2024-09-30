package com.cmdpro.databank.model.item;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemKeyframeAnimations {
    public static void animate(DatabankItemModel<?> pModel, AnimationDefinition pAnimationDefinition, long pAccumulatedTime, float pScale, Vector3f pAnimationVecCache) {
        float f = getElapsedSeconds(pAnimationDefinition, pAccumulatedTime);

        for (Map.Entry<String, List<AnimationChannel>> entry : pAnimationDefinition.boneAnimations().entrySet()) {
            Optional<ModelPart> optional = pModel.getAnyDescendantWithName(entry.getKey());
            List<AnimationChannel> list = entry.getValue();
            optional.ifPresent(p_232330_ -> list.forEach(p_288241_ -> {
                Keyframe[] akeyframe = p_288241_.keyframes();
                int i = Math.max(0, Mth.binarySearch(0, akeyframe.length, p_232315_ -> f <= akeyframe[p_232315_].timestamp()) - 1);
                int j = Math.min(akeyframe.length - 1, i + 1);
                Keyframe keyframe = akeyframe[i];
                Keyframe keyframe1 = akeyframe[j];
                float f1 = f - keyframe.timestamp();
                float f2;
                if (j != i) {
                    f2 = Mth.clamp(f1 / (keyframe1.timestamp() - keyframe.timestamp()), 0.0F, 1.0F);
                } else {
                    f2 = 0.0F;
                }

                keyframe1.interpolation().apply(pAnimationVecCache, f2, akeyframe, i, j, pScale);
                p_288241_.target().apply(p_232330_, pAnimationVecCache);
            }));
        }
    }
    public static boolean isDone(AnimationDefinition definition, long time) {
        return getElapsedSeconds(definition, time) >= definition.lengthInSeconds();
    }
    private static float getElapsedSeconds(AnimationDefinition pAnimationDefinition, long pAccumulatedTime) {
        float f = (float)pAccumulatedTime / 1000.0F;
        return pAnimationDefinition.looping() ? f % pAnimationDefinition.lengthInSeconds() : f;
    }

    public static Vector3f posVec(float pX, float pY, float pZ) {
        return new Vector3f(pX, -pY, pZ);
    }

    public static Vector3f degreeVec(float pXDegrees, float pYDegrees, float pZDegrees) {
        return new Vector3f(pXDegrees * (float) (Math.PI / 180.0), pYDegrees * (float) (Math.PI / 180.0), pZDegrees * (float) (Math.PI / 180.0));
    }

    public static Vector3f scaleVec(double pXScale, double pYScale, double pZScale) {
        return new Vector3f((float)(pXScale - 1.0), (float)(pYScale - 1.0), (float)(pZScale - 1.0));
    }
}
