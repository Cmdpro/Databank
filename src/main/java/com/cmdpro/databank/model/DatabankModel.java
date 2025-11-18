package com.cmdpro.databank.model;

import com.cmdpro.databank.DatabankExtraCodecs;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabankModel {
    public static final Codec<DatabankModel> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            DatabankPartDefinition.CODEC.listOf().fieldOf("parts").forGetter((model) -> model.parts),
            Codec.unboundedMap(Codec.STRING, DatabankAnimation.CODEC).fieldOf("animations").forGetter((model) -> model.animations),
            DatabankExtraCodecs.VECTOR2I.fieldOf("textureSize").forGetter((model) -> model.textureSize)
    ).apply(instance, DatabankModel::new));
    public List<DatabankPartDefinition> parts;
    public Map<String, DatabankAnimation> animations;
    public Vector2i textureSize;
    public HashMap<String, DatabankPartData.DatabankBonePart> allBones = new HashMap<>();
    public DatabankModel(List<DatabankPartDefinition> parts, Map<String, DatabankAnimation> animations, Vector2i textureSize) {
        this.parts = parts;
        this.animations = animations;
        this.textureSize = textureSize;
        allBones = findBones(parts);
    }
    private HashMap<String, DatabankPartData.DatabankBonePart> findBones(List<DatabankPartDefinition> parts) {
        HashMap<String, DatabankPartData.DatabankBonePart> bones = new HashMap<>();
        for (DatabankPartDefinition i : parts) {
            if (i.data instanceof DatabankPartData.DatabankBonePart bone) {
                bones.put(bone.name, bone);
            }
            if (i.data.getChildren() != null) {
                bones.putAll(findBones(i.data.getChildren()));
            }
        }
        return bones;
    }
    private void goThroughChildren(PartDefinition partDefinition, List<DatabankPartDefinition> definitions) {
        for (DatabankPartDefinition i : definitions) {
            PartDefinition part = partDefinition.addOrReplaceChild(i.data.name, i.createCubeListBuilder(), i.createPartPose());
            if (i.data.getChildren() != null) {
                goThroughChildren(part, i.data.getChildren());
            }
        }
    }
    public ModelPose createModelPose() {
        HashMap<String, ModelPose.ModelPosePart> stringToPart = new HashMap<>();
        List<ModelPose.ModelPosePart> children = new ArrayList<>();
        for (DatabankPartDefinition i : parts) {
            Vector3f offset = new Vector3f(i.data.getOffset());
            Vector3f rotation = new Vector3f(i.data.getRotation()).mul(-1, -1, 1);
            ModelPose.ModelPosePart part = new ModelPose.ModelPosePart(i, goThroughChildrenForModelPose(stringToPart, i), offset, rotation, new Vector3f(i.data.getDimensions()));
            children.add(part);
            stringToPart.put(i.data.name, part);
        }
        return new ModelPose(children, stringToPart);
    }
    private List<ModelPose.ModelPosePart> goThroughChildrenForModelPose(HashMap<String, ModelPose.ModelPosePart> stringToPart, DatabankPartDefinition parent) {
        List<ModelPose.ModelPosePart> children = new ArrayList<>();
        if (parent.data.getChildren() == null) {
            return children;
        }
        for (DatabankPartDefinition i : parent.data.getChildren()) {
            Vector3f offset = new Vector3f(i.data.getOffset());
            Vector3f rotation = new Vector3f(i.data.getRotation()).mul(-1, -1, 1);
            ModelPose.ModelPosePart part = new ModelPose.ModelPosePart(i, goThroughChildrenForModelPose(stringToPart, i), offset, rotation, new Vector3f(i.data.getDimensions()));
            children.add(part);
            stringToPart.put(i.data.name, part);
        }
        return children;
    }
}
