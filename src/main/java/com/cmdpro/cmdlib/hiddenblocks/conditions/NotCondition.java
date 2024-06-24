package com.cmdpro.cmdlib.hiddenblocks.conditions;

import com.cmdpro.cmdlib.hiddenblocks.HiddenBlockConditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class NotCondition extends HiddenBlockConditions.HiddenBlockCondition {
    public HiddenBlockConditions.HiddenBlockCondition condition;
    public NotCondition(HiddenBlockConditions.HiddenBlockCondition condition) {
        this.condition = condition;
    }
    @Override
    public boolean isUnlocked(Player player) {
        return !condition.isUnlocked(player);
    }
    public static class NotConditionSerializer extends Serializer {
        @Override
        public HiddenBlockConditions.HiddenBlockCondition deserialize(JsonObject json) {
            ResourceLocation condition = ResourceLocation.tryParse(json.get("condition").getAsString());
            JsonObject conditionData = json.get("conditionData").getAsJsonObject();
            return new NotCondition(HiddenBlockConditions.conditions.get(condition).deserialize(conditionData));
        }
    }
}
