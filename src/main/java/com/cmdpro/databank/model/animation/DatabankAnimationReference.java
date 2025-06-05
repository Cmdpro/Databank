package com.cmdpro.databank.model.animation;

import com.cmdpro.databank.model.DatabankAnimation;

import java.util.List;

public class DatabankAnimationReference {
    public String id;
    public AnimationEvent onStart;
    public AnimationEvent onEnd;
    public DatabankAnimationReference(String id, AnimationEvent onStart, AnimationEvent onEnd) {
        this.id = id;
        this.onStart = onStart;
        this.onEnd = onEnd;
    }
    public interface AnimationEvent {
        void call(DatabankAnimationState state, DatabankAnimationDefinition definition);
    }
}
