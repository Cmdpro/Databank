package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.music.MusicCondition;
import com.cmdpro.databank.music.conditions.AndMusicCondition;
import com.cmdpro.databank.music.conditions.EntityNearbyMusicCondition;
import com.cmdpro.databank.music.conditions.NotMusicCondition;
import com.cmdpro.databank.music.conditions.OrMusicCondition;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MusicConditionRegistry {
    public static final DeferredRegister<MusicCondition.Serializer<?>> MUSIC_CONDITIONS = DeferredRegister.create(DatabankRegistries.MUSIC_CONDITION_REGISTRY_KEY, Databank.MOD_ID);

    public static final Supplier<MusicCondition.Serializer<?>> AND = register("and", () -> AndMusicCondition.AndConditionSerializer.INSTANCE);
    public static final Supplier<MusicCondition.Serializer<?>> OR = register("or", () -> OrMusicCondition.OrConditionSerializer.INSTANCE);
    public static final Supplier<MusicCondition.Serializer<?>> NOT = register("not", () -> NotMusicCondition.NotConditionSerializer.INSTANCE);
    public static final Supplier<MusicCondition.Serializer<?>> ENTITY_NEARBY = register("entity_nearby", () -> EntityNearbyMusicCondition.EntityNearbyConditionSerializer.INSTANCE);
    private static <T extends MusicCondition.Serializer<?>> Supplier<T> register(final String name, final Supplier<T> item) {
        return MUSIC_CONDITIONS.register(name, item);
    }
}
