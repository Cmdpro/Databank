package com.cmdpro.databank.music;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.commands.PlaySoundCommand;
import net.minecraft.sounds.SoundEvent;

public class MusicController {
    public MusicController(MusicCondition condition, ResourceKey<SoundEvent> music, int priority) {
        this.condition = condition;
        this.music = music;
        this.priority = priority;
    }
    public MusicCondition condition;
    public ResourceKey<SoundEvent> music;
    public int priority;
    private SoundEvent musicSoundEvent;
    public SoundEvent getMusic() {
        if (musicSoundEvent == null) {
            musicSoundEvent = SoundEvent.createVariableRangeEvent(music.location());
        }
        return musicSoundEvent;
    }
}
