package com.cmdpro.databank.music;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;

public class MusicController {
    public MusicController(MusicCondition condition, SoundEvent music, int priority) {
        this.condition = condition;
        this.music = music;
        this.priority = priority;
    }
    public MusicCondition condition;
    public SoundEvent music;
    public int priority;
}
