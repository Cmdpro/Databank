package com.cmdpro.databank.hiddenblock;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class HiddenBlockConditions {
    public static Map<ResourceLocation, HiddenBlockCondition.Serializer> conditions = new HashMap<>();
    public static abstract class HiddenBlockCondition {
        public abstract boolean isUnlocked(Player player);
        public abstract Serializer getSerializer();
        public static abstract class Serializer {
            public abstract HiddenBlockCondition deserialize(JsonObject json);
        }
    }
}
