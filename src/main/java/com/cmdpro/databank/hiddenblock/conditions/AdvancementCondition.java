package com.cmdpro.databank.hiddenblock.conditions;

import com.cmdpro.databank.hiddenblock.HiddenBlockConditions;
import com.google.gson.JsonObject;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class AdvancementCondition extends HiddenBlockConditions.HiddenBlockCondition {
    public ResourceLocation advancement;
    public AdvancementCondition(ResourceLocation advancement) {
        this.advancement = advancement;
    }
    @Override
    public boolean isUnlocked(Player player) {
        AdvancementHolder advancement2 = ServerLifecycleHooks.getCurrentServer().getAdvancements().get(advancement);
        if (advancement2 != null) {
            return ((ServerPlayer) player).getAdvancements().getOrStartProgress(advancement2).isDone();
        } else {
            return false;
        }
    }
    @Override
    public Serializer getSerializer() {
        return AdvancementConditionSerializer.INSTANCE;
    }
    public static class AdvancementConditionSerializer extends Serializer {
        public static final AdvancementConditionSerializer INSTANCE = new AdvancementConditionSerializer();
        @Override
        public HiddenBlockConditions.HiddenBlockCondition deserialize(JsonObject json) {
            ResourceLocation advancement = ResourceLocation.tryParse(json.get("advancement").getAsString());
            return new AdvancementCondition(advancement);
        }
    }
}
