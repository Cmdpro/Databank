package com.cmdpro.databank.music.conditions;

import com.cmdpro.databank.music.MusicCondition;
import com.cmdpro.databank.music.MusicSerializer;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class EntityNearbyMusicCondition extends MusicCondition {
    public EntityType entityType;
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
        return EntityNearbyConditionSerializer.INSTANCE;
    }
    public static class EntityNearbyConditionSerializer extends Serializer {
        public static final EntityNearbyConditionSerializer INSTANCE = new EntityNearbyConditionSerializer();
        public static final MapCodec<EntityNearbyMusicCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                ResourceKey.codec(Registries.ENTITY_TYPE).fieldOf("entity").xmap(BuiltInRegistries.ENTITY_TYPE::get, (i) -> BuiltInRegistries.ENTITY_TYPE.getResourceKey(i).orElseThrow()).forGetter((condition) -> condition.entityType)
        ).apply(instance, EntityNearbyMusicCondition::new));
        @Override
        public MapCodec<EntityNearbyMusicCondition> codec() {
            return CODEC;
        }
    }
}

