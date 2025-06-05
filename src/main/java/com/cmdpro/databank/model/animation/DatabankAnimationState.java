package com.cmdpro.databank.model.animation;

import com.cmdpro.databank.model.DatabankModel;
import com.mojang.blaze3d.Blaze3D;

import java.util.HashMap;

public class DatabankAnimationState {
    public double startTime;
    public String defaultAnim;
    public double speed;
    private DatabankAnimationDefinition anim;
    private HashMap<String, DatabankAnimationDefinition> animDefinitions;
    private HashMap<String, DatabankAnimationReference> anims;
    public DatabankAnimationState(String defaultAnim) {
        animDefinitions = new HashMap<>();
        anims = new HashMap<>();
        this.defaultAnim = defaultAnim;
    }
    public void updateAnimDefinitions(DatabankModel model) {
        for (DatabankAnimationReference i : anims.values()) {
            animDefinitions.put(i.id, model.animations.getOrDefault(i.id, null).createAnimationDefinition(i.id));
        }
    }
    public double getProgress() {
        if (anim != null && anim.animation.looping) {
            return (getTime()-startTime) % anim.animation.length;
        }
        return getTime()-startTime;
    }
    public void start() {
        startTime = getTime();
    }
    public void update() {
        if (anim == null) {
            anim = animDefinitions.get(defaultAnim);
        }
        if (isDone()) {
            anims.get(anim.id).onEnd.call(this, anim);
        }
    }
    public void resetAnim() {
        if (anim != null) {
            start();
            anims.get(anim.id).onStart.call(this, anim);
        }
    }
    public void setAnim(String anim) {
        if (this.anim == null || !this.anim.id.equals(anim)) {
            start();
            this.anim = animDefinitions.get(anim);
            anims.get(anim).onStart.call(this, this.anim);
        }
    }
    public boolean isDone() {
        if (anim == null) {
            return true;
        }
        return anim.animation.length <= getProgress() && !anim.animation.looping;
    }
    public DatabankAnimationDefinition getAnim() {
        return anim;
    }
    public boolean isCurrentAnim(String anim) {
        if (this.anim == null) {
            return false;
        }
        return this.anim.id.equals(anim);
    }
    public DatabankAnimationState addAnim(DatabankAnimationReference reference) {
        anims.put(reference.id, reference);
        return this;
    }
    public DatabankAnimationState removeAnim(DatabankAnimationDefinition definition) {
        anims.remove(definition.id);
        return this;
    }
    private double getTime() {
        return Blaze3D.getTime();
    }
}
