package com.cmdpro.databank.instanceddimension;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class PlayerLastLocationData {
    public static final Codec<PlayerLastLocationData> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceKey.codec(Registries.DIMENSION).fieldOf("level").forGetter((obj) -> obj.level),
            Codec.DOUBLE.listOf(3, 3).xmap((val) -> new Vec3(val.get(0), val.get(1), val.get(2)), (val) -> List.of(val.x, val.y, val.z)).fieldOf("pos").forGetter((obj) -> obj.pos)
    ).apply(instance, PlayerLastLocationData::new));
    public ResourceKey<Level> level;
    public Vec3 pos;
    public PlayerLastLocationData(ResourceKey<Level> level, Vec3 pos) {
        this.level = level;
        this.pos = pos;
    }
}
