package com.cmdpro.databank.model;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankExtraCodecs;
import com.cmdpro.databank.DatabankRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class DatabankPartDefinition {
    public static final Codec<DatabankPartDefinition> CODEC = DatabankPartData.CODEC.xmap(DatabankPartDefinition::new, (definition) -> definition.data);
    public DatabankPartDefinition(DatabankPartData data) {
        this.data = data;
        data.part = this;
        if (data.getChildren() != null) {
            for (DatabankPartDefinition i : data.getChildren()) {
                i.parent = this;
            }
        }
    }
    public DatabankPartDefinition parent;
    public DatabankPartData data;
    public CubeListBuilder createCubeListBuilder() {
        if (data instanceof DatabankPartData.DatabankCubePart cube) {
            return cube.createCubeListBuilder();
        }
        return CubeListBuilder.create();
    }
    public PartPose createPartPose() {
        return data.createPartPose();
    }
}
