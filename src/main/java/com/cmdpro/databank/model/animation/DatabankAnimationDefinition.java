package com.cmdpro.databank.model.animation;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;

public class DatabankAnimationDefinition {
    public String id;
    public AnimationDefinition definition;
    public AnimationEvent onStart;
    public AnimationEvent onEnd;
    public DatabankAnimationDefinition(String id, AnimationDefinition definition, AnimationEvent onStart, AnimationEvent onEnd) {
        this.id = id;
        this.definition = definition;
        this.onStart = onStart;
        this.onEnd = onEnd;
    }
    public DatabankAnimationDefinition(String id, AnimationEvent onStart, AnimationEvent onEnd) {
        this.definition = null;
        this.id = id;
        this.onStart = onStart;
        this.onEnd = onEnd;
    }
    public interface AnimationEvent {
        void call(DatabankAnimationState state, DatabankAnimationDefinition definition);
    }
}
