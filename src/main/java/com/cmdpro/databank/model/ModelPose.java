package com.cmdpro.databank.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelPose {
    public List<ModelPosePart> parts;
    public final HashMap<String, ModelPosePart> stringToPart;
    public ModelPose(List<ModelPosePart> parts, HashMap<String, ModelPosePart> stringToPart) {
        this.parts = parts;
        this.stringToPart = stringToPart;
        updatePose(parts);
        allBones = findBones(parts);
    }
    private void updatePose(List<ModelPosePart> parts) {
        for (ModelPosePart i : parts) {
            i.pose = this;
            updatePose(i.children);
        }
    }
    public HashMap<String, ModelPosePart> allBones = new HashMap<>();
    private HashMap<String, ModelPosePart> findBones(List<ModelPosePart> parts) {
        HashMap<String, ModelPosePart> bones = new HashMap<>();
        for (ModelPosePart i : parts) {
            if (i.part.data instanceof DatabankPartData.DatabankBonePart bone) {
                bones.put(bone.name, i);
            }
            if (i.part.data.getChildren() != null) {
                bones.putAll(findBones(i.children));
            }
        }
        return bones;
    }
    public static class ModelPosePart {
        public ModelPose pose;
        public DatabankPartDefinition part;
        public List<ModelPosePart> children;
        public ModelPosePart parent;
        public Vector3f pos;
        public Vector3f rotation;
        public Vector3f scale;
        public ModelPosePart(DatabankPartDefinition part, List<ModelPosePart> children, Vector3f pos, Vector3f rotation, Vector3f scale) {
            this.part = part;
            this.children = children;
            this.pos = pos;
            this.rotation = rotation;
            this.scale = scale;
            for (ModelPosePart i : children) {
                i.parent = this;
            }
        }
        public void offsetPosition(Vector3f offset) {
            pos.add(-offset.x, -offset.y, offset.z);
        }
        public void offsetRotation(Vector3f offset) {
            rotation.add(offset);
        }
        public void offsetScale(Vector3f offset) {
            scale.add(offset);
        }
        public void render(DatabankModel model, float partialTick, PoseStack pPoseStack, VertexConsumer pConsumer, int pPackedLight, int pPackedOverlay, int pColor, Vec3 normalMult, boolean isShadedByNormal, Quaternionf quaternionf) {
            pPoseStack.pushPose();
            part.data.render(this, model, partialTick, pPoseStack, pConsumer, pPackedLight, pPackedOverlay, pColor, normalMult, isShadedByNormal, quaternionf);
            pPoseStack.popPose();
        }
        public Matrix4f getMatrixWithParents() {
            List<ModelPosePart> parts = new ArrayList<>();
            ModelPosePart current = this;
            while (current != null) {
                parts.addFirst(current);
                current = current.parent;
            }
            if (parts.isEmpty()) {
                return new Matrix4f();
            }
            Quaternionf rotation = new Quaternionf();
            Vector3f offset = new Vector3f();
            for (ModelPosePart i : parts) {
                offset.add(new Vector3f(i.pos).rotate(rotation));
                rotation.rotateXYZ(i.rotation.x, i.rotation.y, i.rotation.z);
            }
            Matrix4f mat4 = new Matrix4f();
            mat4.translate(offset);
            mat4.rotate(rotation);
            return mat4;
        }
    }
}
