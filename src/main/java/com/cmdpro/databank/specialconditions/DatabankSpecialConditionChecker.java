package com.cmdpro.databank.specialconditions;

import com.cmdpro.databank.DatabankUtils;
import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabankSpecialConditionChecker {
    public ResourceLocation id;
    private DatabankSpecialCondition condition;
    public Map<String, Object> params;
    public DatabankSpecialConditionChecker(ResourceLocation id, Map<String, Object> params) {
        this.id = id;
        this.params = params;
    }
    public DatabankSpecialCondition getCondition(boolean forceReset) {
        if (condition == null || forceReset) {
            condition = DatabankSpecialConditionManager.getEvent(id);
        }
        return condition;
    }
    public DatabankSpecialCondition getCondition() {
        return getCondition(false);
    }
    public boolean check() {
        DatabankSpecialCondition condition = getCondition();
        return condition.isActiveParams(params);
    }
    public static final Codec<DatabankSpecialConditionChecker> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter((obj) -> obj.id),
            Codec.unboundedMap(Codec.STRING, ExtraCodecs.JAVA).fieldOf("params").forGetter((obj) -> obj.params)
    ).apply(instance, DatabankSpecialConditionChecker::new));
}
