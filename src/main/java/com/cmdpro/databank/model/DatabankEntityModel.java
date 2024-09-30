package com.cmdpro.databank.model;

import com.cmdpro.databank.DatabankExtraCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabankEntityModel {
    public static final Codec<DatabankEntityModel> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            DatabankPartDefinition.CODEC.listOf().fieldOf("parts").forGetter((model) -> model.parts),
            Codec.unboundedMap(Codec.STRING, DatabankAnimation.CODEC).fieldOf("animations").forGetter((model) -> model.animations),
            DatabankExtraCodecs.VECTOR2I.fieldOf("textureSize").forGetter((model) -> model.textureSize)
    ).apply(instance, DatabankEntityModel::new));
    public List<DatabankPartDefinition> parts;
    public Map<String, DatabankAnimation> animations;
    public Vector2i textureSize;
    public DatabankEntityModel(List<DatabankPartDefinition> parts, Map<String, DatabankAnimation> animations, Vector2i textureSize) {
        this.parts = parts;
        this.animations = animations;
        this.textureSize = textureSize;
    }
    public LayerDefinition createLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        goThroughChildren(partdefinition, parts);
        return LayerDefinition.create(meshdefinition, textureSize.x, textureSize.y);
    }
    private void goThroughChildren(PartDefinition partDefinition, List<DatabankPartDefinition> definitions) {
        for (DatabankPartDefinition i : definitions) {
            PartDefinition part = partDefinition.addOrReplaceChild(i.name, i.createCubeListBuilder(), i.createPartPose());
            if (i.children != null) {
                goThroughChildren(part, i.children);
            }
        }
    }
}
