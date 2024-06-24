package com.cmdpro.cmdlib.hiddenblocks;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class HiddenBlockConditions {
    public static Map<ResourceLocation, HiddenBlockCondition.Serializer> conditions = new HashMap<>();
    public static abstract class HiddenBlockCondition {
        public abstract boolean isUnlocked(Player player);
        public static abstract class Serializer {
            public abstract HiddenBlockCondition deserialize(JsonObject json);
        }
    }
}
