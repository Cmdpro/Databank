package com.cmdpro.databank.model;

import com.cmdpro.databank.DatabankExtraCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabankPartDefinition {
    public static final Codec<DatabankPartDefinition> CODEC = Codec.recursive(DatabankPartDefinition.class.getSimpleName(), recursedCodec -> RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("name").forGetter((part) -> part.name),
            ExtraCodecs.VECTOR3F.fieldOf("rotation").forGetter((part) -> part.rotation),
            ExtraCodecs.VECTOR3F.fieldOf("offset").forGetter((part) -> part.offset),
            recursedCodec.listOf().optionalFieldOf("children", new ArrayList<>()).forGetter((part) -> part.children),
            Codec.BOOL.optionalFieldOf("isCube", false).forGetter((part) -> part.isCube),
            Codec.BOOL.optionalFieldOf("mirror", false).forGetter((part) -> part.mirror),
            DatabankExtraCodecs.VECTOR2I.optionalFieldOf("texOffset", new Vector2i()).forGetter((part) -> part.texOffset),
            ExtraCodecs.VECTOR3F.optionalFieldOf("origin", new Vector3f()).forGetter((part) -> part.origin),
            ExtraCodecs.VECTOR3F.optionalFieldOf("dimensions", new Vector3f()).forGetter((part) -> part.dimensions),
            Codec.FLOAT.optionalFieldOf("inflate", 0f).forGetter((part) -> part.inflate)
    ).apply(instance, DatabankPartDefinition::new)));
    public DatabankPartDefinition(String name, Vector3f rotation, Vector3f offset, List<DatabankPartDefinition> children) {
        this(name, rotation, offset, children, false, false, null, null, null, 0);
    }
    public DatabankPartDefinition(String name, Vector3f rotation, Vector3f offset, List<DatabankPartDefinition> children, boolean mirror, Vector2i texOffset, Vector3f origin, Vector3f dimensions, float inflate) {
        this(name, rotation, offset, children, true, mirror, texOffset, origin, dimensions, inflate);
    }
    public DatabankPartDefinition(String name, Vector3f rotation, Vector3f offset, List<DatabankPartDefinition> children, boolean isCube, boolean mirror, Vector2i texOffset, Vector3f origin, Vector3f dimensions, float inflate) {
        this.name = name;
        this.rotation = rotation;
        this.offset = offset;
        this.children = children;
        this.isCube = isCube;
        this.texOffset = texOffset;
        this.origin = origin;
        this.dimensions = dimensions;
        this.inflate = inflate;
    }
    public String name;
    public Vector3f rotation;
    public Vector3f offset;
    public List<DatabankPartDefinition> children;
    public boolean isCube;
    public boolean mirror;
    public Vector2i texOffset;
    public Vector3f origin;
    public Vector3f dimensions;
    public float inflate;
    public CubeListBuilder createCubeListBuilder() {
        if (!isCube) {
            return CubeListBuilder.create();
        }
        return CubeListBuilder.create().texOffs(texOffset.x, texOffset.y).mirror(mirror).addBox(origin.x, origin.y, origin.z, dimensions.x, dimensions.y, dimensions.z, new CubeDeformation(inflate));
    }
    public PartPose createPartPose() {
        return PartPose.offsetAndRotation(offset.x, offset.y, offset.z, rotation.x, rotation.y, rotation.z);
    }
}
