package com.cmdpro.databank.model.animation;

import com.cmdpro.databank.model.DatabankAnimation;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;

import java.util.List;

public class DatabankAnimationDefinition {
    public String id;
    public DatabankAnimation animation;
    public DatabankAnimationDefinition(String id, DatabankAnimation animation) {
        this.id = id;
        this.animation = animation;
    }
}
