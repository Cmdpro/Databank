package com.cmdpro.databank.music;

import net.minecraft.sounds.SoundEvent;

public abstract class MusicController {
    public abstract int getPriority();
    public abstract SoundEvent getMusic();
}
