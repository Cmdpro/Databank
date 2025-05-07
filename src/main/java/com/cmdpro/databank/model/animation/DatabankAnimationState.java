package com.cmdpro.databank.model.animation;

import com.cmdpro.databank.model.DatabankEntityModel;
import com.cmdpro.databank.model.blockentity.BlockEntityKeyframeAnimations;
import com.mojang.blaze3d.Blaze3D;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DatabankAnimationState {
    public AnimationState state;
    public String defaultAnim;
    private DatabankAnimationDefinition anim;
    private HashMap<String, DatabankAnimationDefinition> anims;
    public DatabankAnimationState(String defaultAnim) {
        state = new AnimationState();
        anims = new HashMap<>();
        this.defaultAnim = defaultAnim;
    }
    public void updateAnimDefinitions(DatabankEntityModel model) {
        for (Map.Entry<String, DatabankAnimationDefinition> i : anims.entrySet()) {
            if (i.getValue().definition == null) {
                i.getValue().definition = model.animations.getOrDefault(i.getKey(), null).createAnimationDefinition();
            }
        }
    }
    public void update() {
        if (anim == null) {
            anim = anims.get(defaultAnim);
        }
        state.startIfStopped(getTime());
        if (isDone()) {
            anim.onEnd.call(this, anim);
        }
    }
    public void fastForward(int duration, float speed) {
        state.fastForward(duration, speed);
    }
    public long getAccumulatedTime() {
        return state.getAccumulatedTime();
    }
    public boolean isStarted() {
        return state.isStarted();
    }
    public void resetAnim() {
        if (anim != null) {
            state.stop();
            state.start(getTime());
            anim.onStart.call(this, anim);
        }
    }
    public void setAnim(String anim) {
        if (this.anim == null || !this.anim.id.equals(anim)) {
            state.stop();
            state.start(getTime());
            this.anim = anims.get(anim);
            this.anim.onStart.call(this, this.anim);
        }
    }
    public boolean isDone() {
        if (anim == null) {
            return true;
        }
        return BlockEntityKeyframeAnimations.isDone(anims.get(anim.id).definition, state.getAccumulatedTime());
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
    public DatabankAnimationState addAnim(DatabankAnimationDefinition definition) {
        anims.put(definition.id, definition);
        return this;
    }
    public DatabankAnimationState removeAnim(DatabankAnimationDefinition definition) {
        anims.remove(definition.id);
        return this;
    }
    private int getTime() {
        return (int)(Blaze3D.getTime() * (double)20.0F);
    }
}
