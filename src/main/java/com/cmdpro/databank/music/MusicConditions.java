package com.cmdpro.databank.music;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class MusicConditions {
    public static Map<ResourceLocation, MusicCondition.Serializer> conditions = new HashMap<>();
    public static abstract class MusicCondition {
        public abstract boolean isPlaying();
        public abstract Serializer getSerializer();
        public SoundEvent getMusicOverride(MusicController controller) { return null; }
        public static abstract class Serializer {
            public abstract MusicCondition deserialize(JsonObject json);
        }
    }
}
