package com.cmdpro.databank.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class DatabankAnimation {
    public static final Codec<DatabankAnimation> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("length").forGetter((animation) -> animation.length),
            Codec.BOOL.fieldOf("looping").forGetter((animation) -> animation.looping),
            AnimationPart.CODEC.listOf().fieldOf("animation").forGetter((animation) -> animation.animationParts)
    ).apply(instance, DatabankAnimation::new));
    public float length;
    public boolean looping;
    public List<AnimationPart> animationParts;
    public DatabankAnimation(float length, boolean looping, List<AnimationPart> animationParts) {
        this.length = length;
        this.looping = looping;
        this.animationParts = animationParts;
    }
    public AnimationDefinition createAnimationDefinition() {
        AnimationDefinition.Builder anim = AnimationDefinition.Builder.withLength(length);
        if (looping) {
            anim.looping();
        }
        for (AnimationPart i : animationParts) {
            anim.addAnimation(i.bone, i.createAnimationChannel());
        }
        return anim.build();
    }
    public static class AnimationPart {
        public static final Codec<AnimationPart> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.fieldOf("bone").forGetter((part) -> part.bone),
                Codec.STRING.fieldOf("target").forGetter((part) -> part.targetString),
                AnimationKeyframe.CODEC.listOf().fieldOf("keyframes").forGetter((part) -> part.keyframes)
        ).apply(instance, AnimationPart::new));
        public String bone;
        public String targetString;
        public AnimationChannel.Target target;
        public List<AnimationKeyframe> keyframes;
        public AnimationPart(String bone, String target, List<AnimationKeyframe> keyframes) {
            this.bone = bone;
            this.targetString = target;
            this.keyframes = keyframes;
            if (target.equalsIgnoreCase("POSITION")) {
                this.target = AnimationChannel.Targets.POSITION;
            }
            if (target.equalsIgnoreCase("ROTATION")) {
                this.target = AnimationChannel.Targets.ROTATION;
            }
            if (target.equalsIgnoreCase("SCALE")) {
                this.target = AnimationChannel.Targets.SCALE;
            }
            for (AnimationKeyframe i : keyframes) {
                i.targetChannel = this.target;
            }
        }
        public AnimationChannel createAnimationChannel() {
            List<Keyframe> keyframes = new ArrayList<>();
            for (AnimationKeyframe i : this.keyframes) {
                keyframes.add(i.createKeyframe());
            }
            return new AnimationChannel(target, keyframes.toArray(new Keyframe[0]));
        }
    }
    public static class AnimationKeyframe {
        public static final Codec<AnimationKeyframe> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.FLOAT.fieldOf("timestamp").forGetter((keyframe) -> keyframe.timestamp),
                ExtraCodecs.VECTOR3F.fieldOf("target").forGetter((keyframe) -> keyframe.target),
                Codec.STRING.fieldOf("interpolation").forGetter((keyframe) -> keyframe.interpolation)
        ).apply(instance, AnimationKeyframe::new));
        public float timestamp;
        public Vector3f target;
        public String interpolation;
        public AnimationKeyframe(float timestamp, Vector3f target, String interpolation) {
            this.timestamp = timestamp;
            this.target = target;
            this.interpolation = interpolation;
        }
        public AnimationChannel.Target targetChannel;
        public Keyframe createKeyframe() {
            Vector3f target = getTarget();
            AnimationChannel.Interpolation interpolation = getInterpolation();
            assert interpolation != null;
            return new Keyframe(timestamp, target, interpolation);
        }
        private AnimationChannel.Interpolation getInterpolation() {
            if (this.interpolation.equalsIgnoreCase("LINEAR")) {
                return AnimationChannel.Interpolations.LINEAR;
            }
            if (this.interpolation.equalsIgnoreCase("SMOOTH")) {
                return AnimationChannel.Interpolations.CATMULLROM;
            }
            return null;
        }
        private Vector3f getTarget() {
            Vector3f target = this.target;
            if (targetChannel == AnimationChannel.Targets.POSITION) {
                target = KeyframeAnimations.posVec(target.x, target.y, target.z);
            }
            if (targetChannel == AnimationChannel.Targets.ROTATION) {
                target = KeyframeAnimations.degreeVec(target.x, target.y, target.z);
            }
            if (targetChannel == AnimationChannel.Targets.SCALE) {
                target = KeyframeAnimations.scaleVec(target.x, target.y, target.z);
            }
            return target;
        }
    }
}
