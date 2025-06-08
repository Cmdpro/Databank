package com.cmdpro.databank.music;

import com.mojang.serialization.MapCodec;
import net.minecraft.sounds.SoundEvent;

public abstract class MusicCondition {
    public abstract boolean isPlaying();

    public abstract Serializer<?> getSerializer();

    public SoundEvent getMusicOverride(MusicController controller) {
        return null;
    }

    public static abstract class Serializer<T extends MusicCondition> {
        public abstract MapCodec<T> codec();
    }
}
