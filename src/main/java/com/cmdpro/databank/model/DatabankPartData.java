package com.cmdpro.databank.model;

import com.cmdpro.databank.DatabankExtraCodecs;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.rendering.RenderHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.joml.Random;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class DatabankPartData {
    public static final Codec<DatabankPartData> CODEC = DatabankRegistries.MODEL_PART_TYPE_REGISTRY.byNameCodec().dispatch(DatabankPartData::getCodec, Function.identity());
    public DatabankPartDefinition part;
    public String name;
    public abstract MapCodec<? extends DatabankPartData> getCodec();
    public Vector3f getOffset() { return new Vector3f(); }
    public Vector3f getRotation() { return new Vector3f(); }
    public Vector3f getDimensions() { return new Vector3f(1, 1, 1); }
    public List<DatabankPartDefinition> getChildren() { return null; }
    public float getInflate() { return 0; }
    public PartPose createPartPose() {
        Vector3f offset = getOffset();
        Vector3f rotation = getRotation();
        return PartPose.offsetAndRotation(offset.x, offset.y, offset.z, rotation.x, rotation.y, rotation.z);
    }
    public Matrix4f getMatrixWithParents() {
        List<DatabankPartDefinition> parts = new ArrayList<>();
        DatabankPartDefinition current = part;
        while (current != null) {
            parts.addFirst(current);
            current = current.parent;
        }
        if (parts.isEmpty()) {
            return new Matrix4f();
        }
        Quaternionf rotation = new Quaternionf();
        Vector3f offset = new Vector3f();
        for (DatabankPartDefinition i : parts) {
            offset.add(new Vector3f(i.data.getOffset()).rotate(rotation));
            rotation.rotateXYZ(i.data.getRotation().x, i.data.getRotation().y, i.data.getRotation().z);
        }
        Matrix4f mat4 = new Matrix4f();
        mat4.translate(offset);
        mat4.rotate(rotation);
        return mat4;
    }
    public void render(ModelPose.ModelPosePart posePart, DatabankModel model, float partialTick, PoseStack pPoseStack, VertexConsumer pConsumer, int pPackedLight, int pPackedOverlay, int pColor, Vec3 normalMult, boolean isShadedByNormal, Quaternionf quaternionf) {}
    public void renderFaces(List<DatabankPartData.Face> faces, ModelPose.ModelPosePart posePart, DatabankModel model, float partialTick, PoseStack pPoseStack, VertexConsumer pConsumer, int pPackedLight, int pPackedOverlay, int pColor, Vec3 normalMult, boolean isShadedByNormal, Quaternionf quaternionf) {
        if (faces != null) {
            for (DatabankPartData.Face i : faces) {
                if (i.vertices.size() == 3 || i.vertices.size() == 4) {
                    Vector3f normal = i.normal.toVector3f().rotate(quaternionf);
                    if (!isShadedByNormal) {
                        normal = new Vec3(0, 1, 0).multiply(normalMult).toVector3f();
                    }
                    for (DrawVertex j : i.vertices) {
                        float x = (float)j.pos.x() / 16f;
                        float y = (float)(j.pos.y()) / 16f;
                        float z = (float)j.pos.z() / 16f;
                        pConsumer.addVertex(pPoseStack.last(), x, y, z);
                        pConsumer.setColor(pColor);
                        pConsumer.setUv((float)j.u/(float)model.textureSize.x, (float)j.v/(float)model.textureSize.y);
                        pConsumer.setOverlay(pPackedOverlay);
                        pConsumer.setLight(pPackedLight);
                        pConsumer.setNormal(normal.x, normal.y, normal.z);
                    }
                    if (i.vertices.size() == 3) {
                        DrawVertex j = i.vertices.getLast();
                        float x = (float)j.pos.x() / 16f;
                        float y = (float)(j.pos.y()) / 16f;
                        float z = (float)j.pos.z() / 16f;
                        pConsumer.addVertex(pPoseStack.last(), x, y, z);
                        pConsumer.setColor(pColor);
                        pConsumer.setUv((float)j.u/(float)model.textureSize.x, (float)j.v/(float)model.textureSize.y);
                        pConsumer.setOverlay(pPackedOverlay);
                        pConsumer.setLight(pPackedLight);
                        pConsumer.setNormal(normal.x, normal.y, normal.z);
                    }
                }
            }
        }
    }
    public static class DatabankGroupPart extends DatabankPartData {
        public static final MapCodec<DatabankGroupPart> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.fieldOf("name").forGetter((data) -> data.name),
                ExtraCodecs.VECTOR3F.fieldOf("rotation").forGetter((data) -> data.rotation),
                ExtraCodecs.VECTOR3F.fieldOf("offset").forGetter((data) -> data.offset),
                DatabankPartDefinition.CODEC.listOf().fieldOf("children").forGetter((data) -> data.children)
        ).apply(instance, DatabankGroupPart::new));
        public Vector3f rotation;
        public Vector3f offset;
        public List<DatabankPartDefinition> children;
        public DatabankGroupPart(String name, Vector3f rotation, Vector3f offset, List<DatabankPartDefinition> children) {
            this.name = name;
            this.rotation = rotation;
            this.offset = offset;
            this.children = children;
        }

        @Override
        public MapCodec<? extends DatabankPartData> getCodec() {
            return CODEC;
        }

        @Override
        public Vector3f getOffset() {
            return offset;
        }

        @Override
        public Vector3f getRotation() {
            return rotation;
        }

        @Override
        public List<DatabankPartDefinition> getChildren() {
            return children;
        }
    }
    public static class DatabankArmaturePart extends DatabankPartData {
        public static final MapCodec<DatabankArmaturePart> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.fieldOf("name").forGetter((data) -> data.name),
                ExtraCodecs.VECTOR3F.fieldOf("offset").forGetter((data) -> data.offset),
                DatabankPartDefinition.CODEC.listOf().fieldOf("children").forGetter((data) -> data.children)
        ).apply(instance, DatabankArmaturePart::new));
        public Vector3f offset;
        public List<DatabankPartDefinition> children;
        public DatabankArmaturePart(String name, Vector3f offset, List<DatabankPartDefinition> children) {
            this.name = name;
            this.offset = offset;
            this.children = children;
        }
        @Override
        public MapCodec<? extends DatabankPartData> getCodec() {
            return CODEC;
        }
        @Override
        public Vector3f getOffset() {
            return offset;
        }
        @Override
        public List<DatabankPartDefinition> getChildren() {
            return children;
        }
    }
    public static class DatabankBonePart extends DatabankPartData {
        public static final MapCodec<DatabankBonePart> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.fieldOf("name").forGetter((data) -> data.name),
                ExtraCodecs.VECTOR3F.fieldOf("offset").forGetter((data) -> data.offset),
                ExtraCodecs.VECTOR3F.fieldOf("rotation").forGetter((data) -> data.rotation),
                ExtraCodecs.VECTOR3F.fieldOf("dimensions").forGetter((data) -> data.dimensions),
                DatabankPartDefinition.CODEC.listOf().fieldOf("children").forGetter((data) -> data.children)
        ).apply(instance, DatabankBonePart::new));
        public Vector3f offset;
        public List<DatabankPartDefinition> children;
        public Vector3f rotation;
        public Vector3f dimensions;
        public DatabankBonePart(String name, Vector3f offset, Vector3f rotation, Vector3f dimensions, List<DatabankPartDefinition> children) {
            this.name = name;
            this.offset = offset;
            this.rotation = rotation;
            this.dimensions = dimensions;
            this.children = children;
        }
        @Override
        public MapCodec<? extends DatabankPartData> getCodec() {
            return CODEC;
        }

        @Override
        public Vector3f getOffset() {
            return offset;
        }

        @Override
        public Vector3f getRotation() {
            return rotation;
        }

        @Override
        public Vector3f getDimensions() {
            return dimensions;
        }

        @Override
        public List<DatabankPartDefinition> getChildren() {
            return children;
        }
    }
    public static class DatabankCubePart extends DatabankPartData {
        public static final MapCodec<DatabankCubePart> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.fieldOf("name").forGetter((data) -> data.name),
                ExtraCodecs.VECTOR3F.fieldOf("rotation").forGetter((data) -> data.rotation),
                ExtraCodecs.VECTOR3F.fieldOf("offset").forGetter((data) -> data.offset),
                DatabankExtraCodecs.VECTOR2I.fieldOf("texOffset").forGetter((data) -> data.texOffset),
                Codec.BOOL.fieldOf("mirror").forGetter((data) -> data.mirror),
                ExtraCodecs.VECTOR3F.fieldOf("origin").forGetter((data) -> data.origin),
                ExtraCodecs.VECTOR3F.fieldOf("dimensions").forGetter((data) -> data.dimensions),
                Codec.FLOAT.fieldOf("inflate").forGetter((data) -> data.inflate)
        ).apply(instance, DatabankCubePart::new));
        public Vector3f rotation;
        public Vector3f offset;
        public Vector2i texOffset;
        public boolean mirror;
        public Vector3f origin;
        public Vector3f dimensions;
        public float inflate;
        public DatabankCubePart(String name, Vector3f rotation, Vector3f offset, Vector2i texOffset, boolean mirror, Vector3f origin, Vector3f dimensions, float inflate) {
            this.name = name;
            this.rotation = rotation;
            this.offset = offset;
            this.texOffset = texOffset;
            this.mirror = mirror;
            this.origin = origin;
            this.dimensions = dimensions;
            this.inflate = inflate;
        }
        @Override
        public MapCodec<? extends DatabankPartData> getCodec() {
            return CODEC;
        }
        public CubeListBuilder createCubeListBuilder() {
            return CubeListBuilder.create().texOffs(texOffset.x, texOffset.y).mirror(mirror).addBox(origin.x, origin.y, origin.z, dimensions.x, dimensions.y, dimensions.z, new CubeDeformation(inflate));
        }
        @Override
        public Vector3f getOffset() {
            return offset;
        }

        @Override
        public float getInflate() {
            return inflate;
        }
        @Override
        public Vector3f getRotation() {
            return rotation;
        }
        @Override
        public Vector3f getDimensions() {
            return dimensions;
        }

        @Override
        public void render(ModelPose.ModelPosePart posePart, DatabankModel model, float partialTick, PoseStack pPoseStack, VertexConsumer pConsumer, int pPackedLight, int pPackedOverlay, int pColor, Vec3 normalMult, boolean isShadedByNormal, Quaternionf quaternionf) {
            pPoseStack.pushPose();
            Vec3 origin = new Vec3(this.origin.x, this.origin.y, this.origin.z);

            Vec3 x0y0z0 = new Vec3(0, 0, 0).add(-inflate, -inflate, -inflate).add(origin);
            Vec3 x1y0z0 = new Vec3(dimensions.x, 0, 0).add(inflate, -inflate, -inflate).add(origin);
            Vec3 x1y1z0 = new Vec3(dimensions.x, dimensions.y, 0).add(inflate, inflate, -inflate).add(origin);
            Vec3 x0y1z0 = new Vec3(0, dimensions.y, 0).add(-inflate, inflate, -inflate).add(origin);
            Vec3 x0y0z1 = new Vec3(0, 0, dimensions.z).add(-inflate, -inflate, inflate).add(origin);
            Vec3 x1y0z1 = new Vec3(dimensions.x, 0, dimensions.z).add(inflate, -inflate, inflate).add(origin);
            Vec3 x1y1z1 = new Vec3(dimensions.x, dimensions.y, dimensions.z).add(inflate, inflate, inflate).add(origin);
            Vec3 x0y1z1 = new Vec3(0, dimensions.y, dimensions.z).add(-inflate, inflate, inflate).add(origin);

            float uMin = texOffset.x + dimensions.z;
            float vMin = texOffset.y;
            float uMax = texOffset.x + dimensions.z + dimensions.x;
            float vMax = texOffset.y + dimensions.z;

            if (mirror) {
                float uMinBackup = uMin;
                uMin = uMax;
                uMax = uMinBackup;
            }

            List<DrawVertex> up = new ArrayList<>();
            up.add(new DrawVertex(x1y1z1, uMin, vMin));
            up.add(new DrawVertex(x0y1z1, uMax, vMin));
            up.add(new DrawVertex(x0y1z0, uMax, vMax));
            up.add(new DrawVertex(x1y1z0, uMin, vMax));

            uMin = texOffset.x + dimensions.z + dimensions.x;
            vMin = texOffset.y;
            uMax = texOffset.x + dimensions.z + dimensions.x + dimensions.x;
            vMax = texOffset.y + dimensions.z;

            if (mirror) {
                float uMinBackup = uMin;
                uMin = uMax;
                uMax = uMinBackup;
            }

            List<DrawVertex> down = new ArrayList<>();
            down.add(new DrawVertex(x1y0z1, uMin, vMin));
            down.add(new DrawVertex(x0y0z1, uMax, vMin));
            down.add(new DrawVertex(x0y0z0, uMax, vMax));
            down.add(new DrawVertex(x1y0z0, uMin, vMax));
            uMin = texOffset.x + dimensions.z;
            vMin = texOffset.y + dimensions.z;
            uMax = texOffset.x;
            vMax = texOffset.y + dimensions.z + dimensions.y;

            if (mirror) {
                float uMinBackup = uMin;
                uMin = uMax;
                uMax = uMinBackup;
            }

            List<DrawVertex> east = new ArrayList<>();
            east.add(new DrawVertex(x1y1z1, uMax, vMin));
            east.add(new DrawVertex(x1y1z0, uMin, vMin));
            east.add(new DrawVertex(x1y0z0, uMin, vMax));
            east.add(new DrawVertex(x1y0z1, uMax, vMax));

            uMin = texOffset.x + dimensions.z;
            vMin = texOffset.y + dimensions.z;
            uMax = texOffset.x + dimensions.z + dimensions.x;
            vMax = texOffset.y + dimensions.z + dimensions.y;

            if (mirror) {
                float uMinBackup = uMin;
                uMin = uMax;
                uMax = uMinBackup;
            }

            List<DrawVertex> north = new ArrayList<>();
            north.add(new DrawVertex(x1y1z0, uMin, vMin));
            north.add(new DrawVertex(x0y1z0, uMax, vMin));
            north.add(new DrawVertex(x0y0z0, uMax, vMax));
            north.add(new DrawVertex(x1y0z0, uMin, vMax));

            uMin = texOffset.x + dimensions.z + dimensions.x + dimensions.z;
            vMin = texOffset.y + dimensions.z;
            uMax = texOffset.x + dimensions.z + dimensions.x;
            vMax = texOffset.y + dimensions.z + dimensions.y;

            if (mirror) {
                float uMinBackup = uMin;
                uMin = uMax;
                uMax = uMinBackup;
            }

            List<DrawVertex> west = new ArrayList<>();
            west.add(new DrawVertex(x0y1z1, uMin, vMin));
            west.add(new DrawVertex(x0y1z0, uMax, vMin));
            west.add(new DrawVertex(x0y0z0, uMax, vMax));
            west.add(new DrawVertex(x0y0z1, uMin, vMax));

            uMin = texOffset.x + dimensions.z + dimensions.x + dimensions.z;
            vMin = texOffset.y + dimensions.z;
            uMax = texOffset.x + dimensions.z + dimensions.x + dimensions.z + dimensions.x;
            vMax = texOffset.y + dimensions.z + dimensions.y;

            if (mirror) {
                float uMinBackup = uMin;
                uMin = uMax;
                uMax = uMinBackup;
            }

            List<DrawVertex> south = new ArrayList<>();
            south.add(new DrawVertex(x1y1z1, uMax, vMin));
            south.add(new DrawVertex(x0y1z1, uMin, vMin));
            south.add(new DrawVertex(x0y0z1, uMin, vMax));
            south.add(new DrawVertex(x1y0z1, uMax, vMax));

            boolean[] facesVisible = new boolean[] { true, true, true, true, true, true };
            Vec3[] normals = new Vec3[] {
                    new Vec3(0, 1*normalMult.y, 0),
                    new Vec3(0, -1*normalMult.y, 0),
                    new Vec3(1*normalMult.x, 0, 0),
                    new Vec3(0, 0, -1*normalMult.z),
                    new Vec3(-1*normalMult.x, 0, 0),
                    new Vec3(0, 0, 1*normalMult.z)
            };

            if (dimensions.x+inflate <= 0.001 && dimensions.x+inflate >= -0.001) {
                facesVisible[2] = false;
            }
            if (dimensions.y+inflate <= 0.001 && dimensions.y+inflate >= -0.001) {
                facesVisible[1] = false;
            }
            if (dimensions.z+inflate <= 0.001 && dimensions.z+inflate >= -0.001) {
                facesVisible[5] = false;
            }

            List<DatabankPartData.Face> faces = new ArrayList<>();
            if (facesVisible[0]) { faces.add(new DatabankPartData.Face(up, normals[0])); }
            if (facesVisible[1]) { faces.add(new DatabankPartData.Face(down, normals[1])); }
            if (facesVisible[2]) { faces.add(new DatabankPartData.Face(east, normals[2])); }
            if (facesVisible[3]) { faces.add(new DatabankPartData.Face(north, normals[3])); }
            if (facesVisible[4]) { faces.add(new DatabankPartData.Face(west, normals[4])); }
            if (facesVisible[5]) { faces.add(new DatabankPartData.Face(south, normals[5])); }
            Matrix4f matrix4f = new Matrix4f();
            matrix4f.rotateZYX(rotation.z, rotation.y, rotation.x);
            quaternionf.rotateZYX(rotation.z, -rotation.y, rotation.x);
            for (DatabankPartData.Face i : faces) {
                for (DrawVertex j : i.vertices) {
                    Vector3f pos = j.pos.toVector3f();
                    matrix4f.transformPosition(pos);
                    pos.add(new Vector3f(posePart.pos.x, posePart.pos.y, posePart.pos.z));
                    j.pos = new Vec3(pos.x, pos.y, pos.z);
                }
            }
            pPoseStack.popPose();
            renderFaces(faces, posePart, model, partialTick, pPoseStack, pConsumer, pPackedLight, pPackedOverlay, pColor, normalMult, isShadedByNormal, quaternionf);
        }
    }
    public static class DatabankMeshPart extends DatabankPartData {
        public static final MapCodec<DatabankMeshPart> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Codec.STRING.fieldOf("name").forGetter((data) -> data.name),
                ExtraCodecs.VECTOR3F.fieldOf("rotation").forGetter((data) -> data.rotation),
                ExtraCodecs.VECTOR3F.fieldOf("offset").forGetter((data) -> data.offset),
                Codec.unboundedMap(Codec.STRING, Vertex.CODEC).fieldOf("vertices").forGetter((part) -> part.vertices),
                DatabankPartData.VertexRef.CODEC.listOf().listOf().fieldOf("faces").forGetter((part) -> part.faces)
        ).apply(instance, DatabankMeshPart::new));
        public Vector3f rotation;
        public Vector3f offset;
        public Map<String, Vertex> vertices;
        public List<List<VertexRef>> faces;
        public DatabankMeshPart(String name, Vector3f rotation, Vector3f offset, Map<String, Vertex> vertices, List<List<VertexRef>> faces) {
            this.name = name;
            this.rotation = rotation;
            this.offset = offset;
            this.vertices = vertices;
            this.faces = faces;
        }
        @Override
        public MapCodec<? extends DatabankPartData> getCodec() {
            return CODEC;
        }
        @Override
        public Vector3f getOffset() {
            return offset;
        }

        @Override
        public Vector3f getRotation() {
            return rotation;
        }

        @Override
        public void render(ModelPose.ModelPosePart posePart, DatabankModel model, float partialTick, PoseStack pPoseStack, VertexConsumer pConsumer, int pPackedLight, int pPackedOverlay, int pColor, Vec3 normalMult, boolean isShadedByNormal, Quaternionf quaternionf) {
            pPoseStack.translate(posePart.pos.x/16f, posePart.pos.y/16f, posePart.pos.z/16f);
            Matrix4f matrix4f = new Matrix4f();
            matrix4f.rotateXYZ(rotation.x, -rotation.y, rotation.z);
            quaternionf.rotateXYZ(rotation.x, rotation.y, rotation.z);
            List<List<VertexRef>> faces2 = faces;
            List<Face> faces = new ArrayList<>();
            Map<String, Vertex> vertices = new HashMap<>();
            for (Map.Entry<String, Vertex> i : this.vertices.entrySet()) {
                Vector3f pos = i.getValue().pos.toVector3f();

                Matrix4f mat4 = new Matrix4f();
                Vector3f target = new Vector3f();

                Matrix4f armatureMatrixInverse = new Matrix4f(posePart.parent.parent.getMatrixWithParents()).invert();
                Matrix4f bindMatrix = new Matrix4f(armatureMatrixInverse);
                bindMatrix.mul(getMatrixWithParents());
                Matrix4f bindMatrixInverse = new Matrix4f(bindMatrix).invert();

                pos.mulPosition(bindMatrix);

                Map<ModelPose.ModelPosePart, Float> affectingBones = i.getValue().weights.entrySet().stream().map((j) -> Map.entry(posePart.pose.allBones.get(j.getKey()), j.getValue())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                if (affectingBones.size() > 4) {
                    List<Float> highest = affectingBones.values().stream().sorted().toList().subList(0, 4);
                    for (Map.Entry<ModelPose.ModelPosePart, Float> j : affectingBones.entrySet()) {
                        if (j.getValue() < highest.getLast()) {
                            affectingBones.remove(j.getKey());
                        }
                    }
                }
                List<ModelPose.ModelPosePart> bones = new ArrayList<>(affectingBones.keySet());
                List<Float> weights = new ArrayList<>(affectingBones.values());
                while (weights.size() < 4) {
                    weights.add(0f);
                }
                Vector4f weightsVector = new Vector4f(weights.get(0), weights.get(1), weights.get(2), weights.get(3));
                float scale = 1.0f / weightsVector.length();
                if (scale != Float.POSITIVE_INFINITY) {
                    weightsVector.mul(scale);
                    weights.set(0, weightsVector.x);
                    weights.set(1, weightsVector.y);
                    weights.set(2, weightsVector.z);
                    weights.set(3, weightsVector.w);

                    for (int j = 0; j < 4; j++) {
                        float weight = weights.get(j);
                        if (weight != 0 && affectingBones.size() > j) {
                            ModelPose.ModelPosePart bone = bones.get(j);
                            mat4 = new Matrix4f(armatureMatrixInverse).mul(bone.getMatrixWithParents());
                            mat4.mul(new Matrix4f(bone.part.data.getMatrixWithParents()).invert());
                            target.add(new Vector3f(pos).mulPosition(mat4).mul(weight));
                        }
                    }
                } else {
                    target = new Vector3f(pos);
                }

                target.mulPosition(bindMatrixInverse);

                Vertex vert = new Vertex(new Vec3(target.x, target.y, target.z), i.getValue().weights);
                vertices.put(i.getKey(), vert);
            }


            for (List<VertexRef> i : faces2) {
                if (i.size() == 3 || i.size() == 4) {
                    List<Vertex> refVerts = new ArrayList<>();
                    List<DrawVertex> verts = new ArrayList<>();
                    for (VertexRef j : i) {
                        Vertex refVert = j.getVertex(vertices);
                        refVerts.add(refVert);
                        Vector3f pos = matrix4f.transformPosition(refVert.pos.toVector3f());
                        verts.add(new DrawVertex(new Vec3(pos.x, pos.y, pos.z), j.u, j.v));
                    }
                    Vec3 vecA = refVerts.get(1).pos.subtract(refVerts.get(0).pos);
                    Vec3 vecB = refVerts.get(2).pos.subtract(refVerts.get(0).pos);
                    Vector3f normal = vecA.cross(vecB).toVector3f().normalize();
                    faces.add(new Face(verts, new Vec3(normal.x, normal.y, normal.z)));
                }
            }
            renderFaces(faces, posePart, model, partialTick, pPoseStack, pConsumer, pPackedLight, pPackedOverlay, pColor, normalMult, isShadedByNormal, quaternionf);
        }
    }

    public static class Face {
        public List<DrawVertex> vertices;
        public Vec3 normal;
        public Face(List<DrawVertex> vertices, Vec3 normal) {
            this.vertices = vertices;
            this.normal = normal;
        }
    }
    public static class VertexRef {
        public static final Codec<VertexRef> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.fieldOf("id").forGetter((vertex) -> vertex.vertexId),
                Codec.FLOAT.fieldOf("u").forGetter(vertex -> vertex.u),
                Codec.FLOAT.fieldOf("v").forGetter(vertex -> vertex.v)
        ).apply(instance, VertexRef::new));
        public String vertexId;
        public float u;
        public float v;
        public VertexRef(String vertexId, float u, float v) {
            this.vertexId = vertexId;
            this.u = u;
            this.v = v;
        }
        public Vertex getVertex(Map<String, Vertex> vertices) {
            return vertices.get(vertexId);
        }
    }

    public static class Vertex {
        public static final Codec<Vertex> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.DOUBLE.fieldOf("x").forGetter((vertice) -> vertice.pos.x),
                Codec.DOUBLE.fieldOf("y").forGetter((vertice) -> vertice.pos.y),
                Codec.DOUBLE.fieldOf("z").forGetter((vertice) -> vertice.pos.z),
                Codec.unboundedMap(Codec.STRING, Codec.FLOAT).fieldOf("weights").forGetter(vertice -> vertice.weights)
        ).apply(instance, (x, y, z, weights) -> new Vertex(new Vec3(x, y, z), weights)));
        public Vec3 pos;
        public Map<String, Float> weights;
        public Vertex(Vec3 pos, Map<String, Float> weights) {
            this.pos = pos;
            this.weights = weights;
        }
        public Vertex(Vec3 pos) {
            this(pos, Map.of());
        }
    }
    public static class DrawVertex {
        public Vec3 pos;
        public float u;
        public float v;
        public DrawVertex(Vec3 pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }
}