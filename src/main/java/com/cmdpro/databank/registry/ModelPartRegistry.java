package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.model.DatabankPartData;
import com.cmdpro.databank.music.MusicCondition;
import com.cmdpro.databank.music.conditions.AndMusicCondition;
import com.cmdpro.databank.music.conditions.EntityNearbyMusicCondition;
import com.cmdpro.databank.music.conditions.NotMusicCondition;
import com.cmdpro.databank.music.conditions.OrMusicCondition;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModelPartRegistry {
    public static final DeferredRegister<MapCodec<? extends DatabankPartData>> MODEL_PART_TYPES = DeferredRegister.create(DatabankRegistries.MODEL_PART_TYPE_REGISTRY_KEY, Databank.MOD_ID);

    public static final Supplier<MapCodec<? extends DatabankPartData>> GROUP = register("group", () -> DatabankPartData.DatabankGroupPart.CODEC);
    public static final Supplier<MapCodec<? extends DatabankPartData>> CUBE = register("cube", () -> DatabankPartData.DatabankCubePart.CODEC);
    public static final Supplier<MapCodec<? extends DatabankPartData>> MESH = register("mesh", () -> DatabankPartData.DatabankMeshPart.CODEC);
    public static final Supplier<MapCodec<? extends DatabankPartData>> ARMATURE = register("armature", () -> DatabankPartData.DatabankArmaturePart.CODEC);
    public static final Supplier<MapCodec<? extends DatabankPartData>> BONE = register("bone", () -> DatabankPartData.DatabankBonePart.CODEC);
    private static <T extends MapCodec<? extends DatabankPartData>> Supplier<T> register(final String name, final Supplier<T> item) {
        return MODEL_PART_TYPES.register(name, item);
    }
}
