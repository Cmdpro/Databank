package com.cmdpro.cmdlib.hiddenblocks.conditions;

import com.cmdpro.cmdlib.hiddenblocks.HiddenBlockConditions;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class OrCondition extends HiddenBlockConditions.HiddenBlockCondition {
    public HiddenBlockConditions.HiddenBlockCondition conditionA;
    public HiddenBlockConditions.HiddenBlockCondition conditionB;
    public OrCondition(HiddenBlockConditions.HiddenBlockCondition conditionA, HiddenBlockConditions.HiddenBlockCondition conditionB) {
        this.conditionA = conditionA;
        this.conditionB = conditionB;
    }
    @Override
    public boolean isUnlocked(Player player) {
        return conditionA.isUnlocked(player) || conditionB.isUnlocked(player);
    }
    public static class OrConditionSerializer extends Serializer {
        @Override
        public HiddenBlockConditions.HiddenBlockCondition deserialize(JsonObject json) {
            ResourceLocation conditionA = ResourceLocation.tryParse(json.get("conditionA").getAsString());
            JsonObject conditionAData = json.get("conditionAData").getAsJsonObject();
            ResourceLocation conditionB = ResourceLocation.tryParse(json.get("conditionB").getAsString());
            JsonObject conditionBData = json.get("conditionBData").getAsJsonObject();
            return new OrCondition(HiddenBlockConditions.conditions.get(conditionA).deserialize(conditionAData), HiddenBlockConditions.conditions.get(conditionB).deserialize(conditionBData));
        }
    }
}
