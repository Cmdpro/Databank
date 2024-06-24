package com.cmdpro.cmdlib.hiddenblocks.conditions;

import com.cmdpro.cmdlib.hiddenblocks.HiddenBlockConditions;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.server.ServerLifecycleHooks;

public class AdvancementCondition extends HiddenBlockConditions.HiddenBlockCondition {
    public ResourceLocation advancement;
    public AdvancementCondition(ResourceLocation advancement) {
        this.advancement = advancement;
    }
    @Override
    public boolean isUnlocked(Player player) {
        Advancement advancement2 = ServerLifecycleHooks.getCurrentServer().getAdvancements().getAdvancement(advancement);
        if (advancement2 != null) {
            return ((ServerPlayer) player).getAdvancements().getOrStartProgress(advancement2).isDone();
        } else {
            return false;
        }
    }
    public static class AdvancementConditionSerializer extends Serializer {
        @Override
        public HiddenBlockConditions.HiddenBlockCondition deserialize(JsonObject json) {
            ResourceLocation advancement = ResourceLocation.tryParse(json.get("advancement").getAsString());
            return new AdvancementCondition(advancement);
        }
    }
}
