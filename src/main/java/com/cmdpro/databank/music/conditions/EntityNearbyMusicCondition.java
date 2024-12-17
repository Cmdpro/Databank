package com.cmdpro.databank.music.conditions;

import com.cmdpro.databank.music.MusicConditions;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class EntityNearbyMusicCondition extends MusicConditions.MusicCondition {
    public EntityType<?> entityType;
    public EntityNearbyMusicCondition(EntityType<?> entityType) {
        this.entityType = entityType;
    }
    @Override
    public boolean isPlaying() {
        for (var i : Minecraft.getInstance().level.entitiesForRendering()) {
            if (i.getType().equals(entityType)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public Serializer getSerializer() {
        return NotMusicCondition.NotConditionSerializer.INSTANCE;
    }
    public static class EntityNearbyConditionSerializer extends Serializer {
        public static final EntityNearbyConditionSerializer INSTANCE = new EntityNearbyConditionSerializer();
        @Override
        public MusicConditions.MusicCondition deserialize(JsonObject json) {
            ResourceLocation entity = ResourceLocation.tryParse(json.get("entity").getAsString());
            return new EntityNearbyMusicCondition(BuiltInRegistries.ENTITY_TYPE.get(entity));
        }
    }
}

