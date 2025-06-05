package com.cmdpro.databank.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
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
    }
    public static class ModelPosePart {
        public DatabankPartDefinition part;
        public List<ModelPosePart> children;
        public Vector3f pos;
        public Vector3f rotation;
        public Vector3f scale;
        public ModelPosePart(DatabankPartDefinition part, List<ModelPosePart> children, Vector3f pos, Vector3f rotation, Vector3f scale) {
            this.part = part;
            this.children = children;
            this.pos = pos;
            this.rotation = rotation;
            this.scale = scale;
        }
        public void offsetPosition(Vector3f offset) {
            pos.add(offset.x, offset.y, offset.z);
        }
        public void offsetRotation(Vector3f offset) {
            rotation.add(offset);
        }
        public void offsetScale(Vector3f offset) {
            scale.add(offset);
        }
        public void render(DatabankModel model, float partialTick, PoseStack pPoseStack, VertexConsumer pConsumer, int pPackedLight, int pPackedOverlay, int pColor) {
            pPoseStack.pushPose();
            List<List<DatabankPartDefinition.Vertex>> faces = null;
            if (part.isCube) {
                pPoseStack.pushPose();
                Vec3 origin = new Vec3(part.origin.x, part.origin.y, part.origin.z);

                Vec3 x0y0z0 = new Vec3(0, 0, 0).add(-part.inflate, part.inflate, -part.inflate).add(origin);
                Vec3 x1y0z0 = new Vec3(part.dimensions.x, 0, 0).add(part.inflate, part.inflate, -part.inflate).add(origin);
                Vec3 x1y1z0 = new Vec3(part.dimensions.x, part.dimensions.y, 0).add(part.inflate, -part.inflate, -part.inflate).add(origin);
                Vec3 x0y1z0 = new Vec3(0, part.dimensions.y, 0).add(-part.inflate, -part.inflate, -part.inflate).add(origin);
                Vec3 x0y0z1 = new Vec3(0, 0, part.dimensions.z).add(-part.inflate, part.inflate, part.inflate).add(origin);
                Vec3 x1y0z1 = new Vec3(part.dimensions.x, 0, part.dimensions.z).add(part.inflate, part.inflate, part.inflate).add(origin);
                Vec3 x1y1z1 = new Vec3(part.dimensions.x, part.dimensions.y, part.dimensions.z).add(part.inflate, -part.inflate, part.inflate).add(origin);
                Vec3 x0y1z1 = new Vec3(0, part.dimensions.y, part.dimensions.z).add(-part.inflate, -part.inflate, part.inflate).add(origin);

                float uMin = part.texOffset.x + part.dimensions.z;
                float vMin = part.texOffset.y + part.dimensions.z;
                float uMax = part.texOffset.x + part.dimensions.z + part.dimensions.x;
                float vMax = part.texOffset.y;
                List<DatabankPartDefinition.Vertex> down = new ArrayList<>();
                down.add(new DatabankPartDefinition.Vertex(x1y0z1, uMax, vMin));
                down.add(new DatabankPartDefinition.Vertex(x0y0z1, uMin, vMin));
                down.add(new DatabankPartDefinition.Vertex(x0y0z0, uMin, vMax));
                down.add(new DatabankPartDefinition.Vertex(x1y0z0, uMax, vMax));

                uMin = part.texOffset.x + part.dimensions.z + part.dimensions.x;
                vMin = part.texOffset.y;
                uMax = part.texOffset.x + part.dimensions.z + part.dimensions.x + part.dimensions.x;
                vMax = part.texOffset.y + part.dimensions.z;
                List<DatabankPartDefinition.Vertex> up = new ArrayList<>();
                up.add(new DatabankPartDefinition.Vertex(x1y1z0, uMax, vMin));
                up.add(new DatabankPartDefinition.Vertex(x0y1z0, uMin, vMin));
                up.add(new DatabankPartDefinition.Vertex(x0y1z1, uMin, vMax));
                up.add(new DatabankPartDefinition.Vertex(x1y1z1, uMax, vMax));

                uMin = part.texOffset.x;
                vMin = part.texOffset.y + part.dimensions.z + part.dimensions.y;
                uMax = part.texOffset.x + part.dimensions.z;
                vMax = part.texOffset.y + part.dimensions.z;
                List<DatabankPartDefinition.Vertex> west = new ArrayList<>();
                west.add(new DatabankPartDefinition.Vertex(x0y0z0, uMax, vMax));
                west.add(new DatabankPartDefinition.Vertex(x0y0z1, uMin, vMax));
                west.add(new DatabankPartDefinition.Vertex(x0y1z1, uMin, vMin));
                west.add(new DatabankPartDefinition.Vertex(x0y1z0, uMax, vMin));

                uMin = part.texOffset.x + part.dimensions.z;
                vMin = part.texOffset.y + part.dimensions.z + part.dimensions.y;
                uMax = part.texOffset.x + part.dimensions.z + part.dimensions.x;
                vMax = part.texOffset.y + part.dimensions.z;
                List<DatabankPartDefinition.Vertex> north = new ArrayList<>();
                north.add(new DatabankPartDefinition.Vertex(x1y0z0, uMax, vMax));
                north.add(new DatabankPartDefinition.Vertex(x0y0z0, uMin, vMax));
                north.add(new DatabankPartDefinition.Vertex(x0y1z0, uMin, vMin));
                north.add(new DatabankPartDefinition.Vertex(x1y1z0, uMax, vMin));

                uMin = part.texOffset.x + part.dimensions.z + part.dimensions.x;
                vMin = part.texOffset.y + part.dimensions.z + part.dimensions.y;
                uMax = part.texOffset.x + part.dimensions.z + part.dimensions.x + part.dimensions.z;
                vMax = part.texOffset.y + part.dimensions.z;
                List<DatabankPartDefinition.Vertex> east = new ArrayList<>();
                east.add(new DatabankPartDefinition.Vertex(x1y0z1, uMax, vMax));
                east.add(new DatabankPartDefinition.Vertex(x1y0z0, uMin, vMax));
                east.add(new DatabankPartDefinition.Vertex(x1y1z0, uMin, vMin));
                east.add(new DatabankPartDefinition.Vertex(x1y1z1, uMax, vMin));

                uMin = part.texOffset.x + part.dimensions.z + part.dimensions.x + part.dimensions.z;
                vMin = part.texOffset.y + part.dimensions.z + part.dimensions.y;
                uMax = part.texOffset.x + part.dimensions.z + part.dimensions.x + part.dimensions.z + part.dimensions.x;
                vMax = part.texOffset.y + part.dimensions.z;
                List<DatabankPartDefinition.Vertex> south = new ArrayList<>();
                south.add(new DatabankPartDefinition.Vertex(x0y0z1, uMax, vMax));
                south.add(new DatabankPartDefinition.Vertex(x1y0z1, uMin, vMax));
                south.add(new DatabankPartDefinition.Vertex(x1y1z1, uMin, vMin));
                south.add(new DatabankPartDefinition.Vertex(x0y1z1, uMax, vMin));



                faces = new ArrayList<>();
                faces.add(down);
                faces.add(up);
                faces.add(west);
                faces.add(north);
                faces.add(east);
                faces.add(south);
                Matrix4f matrix4f = new Matrix4f();
                matrix4f.rotateZYX(part.rotation.z, part.rotation.y, part.rotation.x);
                for (List<DatabankPartDefinition.Vertex> i : faces) {
                    for (DatabankPartDefinition.Vertex j : i) {
                        Vector3f pos = j.pos.toVector3f();
                        matrix4f.transformPosition(pos);
                        pos.add(new Vector3f(this.pos.x, this.pos.y, this.pos.z));
                        j.pos = new Vec3(pos.x, -pos.y, pos.z);
                    }
                }
                pPoseStack.popPose();
            }
            if (part.isMesh) {
                pPoseStack.translate(pos.x/16f, pos.y/16f, pos.z/16f);
                pPoseStack.mulPose(new Quaternionf().rotationZYX(rotation.z, rotation.y, rotation.x));
                faces = part.faces.orElse(null);
            }
            if (faces != null) {
                Vector3f middle = faces.getFirst().getFirst().pos.toVector3f();
                for (List<DatabankPartDefinition.Vertex> i : faces) {
                    if (i.size() == 3 || i.size() == 4) {
                        for (DatabankPartDefinition.Vertex j : i) {
                            middle.lerp(j.pos.toVector3f(), 0.5f);
                        }
                    }
                }
                for (List<DatabankPartDefinition.Vertex> i : faces) {
                    if (i.size() == 3 || i.size() == 4) {
                        Vector3f faceMiddle = i.getFirst().pos.toVector3f();
                        for (DatabankPartDefinition.Vertex j : i) {
                            faceMiddle.lerp(j.pos.toVector3f(), 0.5f);
                        }
                        faceMiddle.mul(1, -1, 1);
                        middle.mul(1, -1, 1);
                        pPoseStack.last().pose().transformPosition(middle);
                        pPoseStack.last().pose().transformPosition(faceMiddle);
                        Vector3f normal = new Vector3f(faceMiddle).sub(middle).normalize();
                        for (DatabankPartDefinition.Vertex j : i) {
                            float x = (float)j.pos.x() / 16f;
                            float y = (float)-j.pos.y() / 16f;
                            float z = (float)j.pos.z() / 16f;
                            pConsumer.addVertex(pPoseStack.last(), x, y, z);
                            pConsumer.setColor(pColor);
                            pConsumer.setUv((float)j.u/(float)model.textureSize.x, (float)j.v/(float)model.textureSize.y);
                            pConsumer.setOverlay(pPackedOverlay);
                            pConsumer.setLight(pPackedLight);
                            pConsumer.setNormal(normal.x, normal.y, normal.z);
                        }
                        if (i.size() == 3) {
                            DatabankPartDefinition.Vertex j = i.getLast();
                            float x = (float)j.pos.x() / 16f;
                            float y = (float)-j.pos.y() / 16f;
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
            pPoseStack.popPose();
        }
    }
}
