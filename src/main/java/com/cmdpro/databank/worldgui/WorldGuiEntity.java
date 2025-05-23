package com.cmdpro.databank.worldgui;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.registry.EntityRegistry;
import com.cmdpro.databank.worldgui.components.WorldGuiComponent;
import com.cmdpro.databank.worldgui.components.WorldGuiComponentType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.util.List;
import java.util.UUID;

public class WorldGuiEntity extends Entity {
    public WorldGuiEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    public WorldGuiEntity(Level level, Vec3 position, WorldGuiType type) {
        this(EntityRegistry.WORLD_GUI.get(), level);
        setPos(position);
        this.guiType = type;
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if (!level().isClientSide) {
            this.gui = guiType.createGui(this);
            this.gui.addInitialComponents();
            syncData();
        }
    }

    public static final EntityDataAccessor<CompoundTag> GUI_DATA = SynchedEntityData.defineId(WorldGuiEntity.class, EntityDataSerializers.COMPOUND_TAG);
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(GUI_DATA, new CompoundTag());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        loadGuiData(compound);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        saveGuiData(compound);
    }

    public void sendComponentData(WorldGui gui, CompoundTag tag) {
        ListTag components = new ListTag();
        for (WorldGuiComponent i : gui.components) {
            CompoundTag component = new CompoundTag();
            i.sendData(component);
            component.putUUID("uuid", i.uuid);
            ResourceLocation id = DatabankRegistries.WORLD_GUI_COMPONENT_REGISTRY.getKey(i.getType());
            if (id != null) {
                component.putString("id", id.toString());
            }
            components.add(component);
        }
        tag.put("components", components);
    }
    public void recieveComponentData(WorldGui gui, CompoundTag tag) {
        ListTag components = (ListTag)tag.get("components");
        if (components != null) {
            for (Tag i : components) {
                if (i instanceof CompoundTag compoundTag) {
                    UUID uuid = compoundTag.getUUID("uuid");
                    ResourceLocation id = ResourceLocation.tryParse(compoundTag.getString("id"));
                    WorldGuiComponentType type = DatabankRegistries.WORLD_GUI_COMPONENT_REGISTRY.get(id);
                    if (type != null) {
                        WorldGuiComponent component = gui.components.stream().filter((j) -> j.uuid.equals(uuid)).findFirst().orElse(null);
                        if (component == null) {
                            component = type.createComponent(gui);
                        }
                        component.recieveData(compoundTag);
                        component.uuid = uuid;
                        gui.addComponent(component);
                    }
                }
            }
        }
    }
    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        if (guiType.saves()) {
            guiType.saveData(gui, tag);
        }
        gui.sendData(tag);
        sendComponentData(gui, tag);
        ResourceLocation id = DatabankRegistries.WORLD_GUI_TYPE_REGISTRY.getKey(guiType);
        if (id != null) {
            tag.putString("id", id.toString());
        }
        return tag;
    }
    public void syncData() {
        getEntityData().set(GUI_DATA, getSyncData());
    }
    public void recieveData(CompoundTag tag) {
        if (gui == null || guiType == null) {
            gui = null;
            guiType = null;
            if (tag.contains("id")) {
                ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
                WorldGuiType type = DatabankRegistries.WORLD_GUI_TYPE_REGISTRY.get(id);
                if (type != null) {
                    guiType = type;
                    gui = type.saves() ? type.loadData(this, tag) : type.createGui(this);
                }
            }
        }
        if (gui != null) {
            gui.recieveData(tag);
            recieveComponentData(gui, tag);
        }
    }
    public void saveComponentData(WorldGui gui, CompoundTag tag) {
        ListTag components = new ListTag();
        for (WorldGuiComponent i : gui.components) {
            CompoundTag component = new CompoundTag();
            i.getType().saveData(i, component);
            ResourceLocation id = DatabankRegistries.WORLD_GUI_COMPONENT_REGISTRY.getKey(i.getType());
            if (id != null) {
                component.putString("id", id.toString());
            }
            components.add(component);
        }
        tag.put("components", components);
    }
    public void loadComponentData(WorldGui gui, CompoundTag tag) {
        ListTag components = (ListTag)tag.get("components");
        if (components != null) {
            for (Tag i : components) {
                if (i instanceof CompoundTag compoundTag) {
                    ResourceLocation id = ResourceLocation.tryParse(compoundTag.getString("id"));
                    WorldGuiComponentType type = DatabankRegistries.WORLD_GUI_COMPONENT_REGISTRY.get(id);
                    if (type != null) {
                        WorldGuiComponent component = type.createComponent(gui);
                        component.getType().loadData(gui, compoundTag);
                    }
                }
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (key.equals(GUI_DATA)) {
            CompoundTag tag = getEntityData().get(GUI_DATA);
            if (tag != null) {
                recieveData(tag);
            }
        }
    }

    public CompoundTag saveGuiData(CompoundTag tag) {
        ResourceLocation id = DatabankRegistries.WORLD_GUI_TYPE_REGISTRY.getKey(guiType);
        guiType.saveData(gui, tag);
        saveComponentData(gui, tag);
        if (id != null) {
            tag.putString("id", id.toString());
        }
        return tag;
    }
    public void loadGuiData(CompoundTag tag) {
        ResourceLocation id = null;
        if (tag.contains("id")) {
            id = ResourceLocation.tryParse(tag.getString("id"));
        }
        WorldGuiType type = DatabankRegistries.WORLD_GUI_TYPE_REGISTRY.get(id);
        if (type != null) {
            if (type.saves()) {
                guiType = type;
                gui = type.loadData(this, tag);
                loadComponentData(gui, tag);
            }
        }
    }
    public Vec3 getBoundsCorner(float multX, float multY) {
        Vec2 size = guiType.getMenuWorldSize(this);
        Vector3f vec3 = new Vector3f((size.x/2f)*multX, 0f, (size.y/2f)*multY);
        Matrix3f matrix = new Matrix3f();
        List<Matrix3f> matrixs = gui.getMatrixs();
        for (Matrix3f i : matrixs) {
            matrix.mul(i);
        }
        matrix.transform(vec3);
        Vec3 corner = position().add(vec3.x, vec3.y, vec3.z);
        return corner;
    }

    @Override
    public boolean shouldBeSaved() {
        return (guiType == null || guiType.saves()) && super.shouldBeSaved();
    }

    @Override
    public void tick() {
        super.tick();
        if (gui != null) {
            gui.tick();
        }
    }

    public WorldGuiIntersectionResult getLineIntersectResult(Vec3 lineStart, Vec3 lineEnd) {
        Vector3f topLeft = getBoundsCorner(-1, 1).toVector3f();
        Vector3f topRight = getBoundsCorner(1, 1).toVector3f();
        Vector3f bottomLeft = getBoundsCorner(-1, -1).toVector3f();
        Vector3f bottomRight = getBoundsCorner(1, -1).toVector3f();
        Vector3f start = lineStart.toVector3f();
        WorldGuiIntersectionResult result1 = intersectTriangle(start, lineStart.vectorTo(lineEnd).toVector3f(), (float)lineStart.distanceTo(lineEnd), topLeft, topRight, bottomLeft);
        if (result1 != null) {
            result1 = new WorldGuiIntersectionResult(new Vec2(1f-result1.normal.x, result1.normal.y), result1.pos);
            if (result1.normal.length() >= 1) {
                result1.normal = result1.normal.normalized();
            }
            return result1;
        }
        WorldGuiIntersectionResult result2 = intersectTriangle(start, lineStart.vectorTo(lineEnd).toVector3f(), (float)lineStart.distanceTo(lineEnd), bottomRight, bottomLeft, topRight);
        if (result2 != null) {
            result2 = new WorldGuiIntersectionResult(new Vec2(result2.normal.x, 1f-result2.normal.y), result2.pos);
            if (result2.normal.length() >= 1) {
                result2.normal = result2.normal.normalized();
            }
            return result2;
        }
        return null;
    }
    private WorldGuiIntersectionResult intersectTriangle(Vector3f rayStart, Vector3f direction, float maxDistance, Vector3f triangle1, Vector3f triangle2, Vector3f triangle3) {
        Vector3f e1 = new Vector3f(triangle2).sub(triangle1);
        Vector3f e2 = new Vector3f(triangle3).sub(triangle1);

        Vector3f h = new Vector3f(direction).cross(e2);
        float a = e1.dot(h);

        if (a > -0.00001f && a < 0.00001f)
            return null;

        float f = 1f/a;
        Vector3f s = new Vector3f(rayStart).sub(triangle1);
        float u = f * (s.dot(h));

        if (u < 0.0f || u > 1.0f)
            return null;

        Vector3f q = new Vector3f(s).cross(e1);
        float v = f * direction.dot(q);

        if (v < 0.0f || u + v > 1.0f)
            return null;

        float t = f * e2.dot(q);

        Vector3f hitPos = new Vector3f(rayStart).add(new Vector3f(direction).mul(t));

        if (t > 0.00001f && t <= maxDistance) {
            return new WorldGuiIntersectionResult(new Vec2(u, v), new Vec3(hitPos.x, hitPos.y, hitPos.z));
        }
        return null;
    }
    public static class WorldGuiIntersectionResult {
        public Vec2 normal;
        public Vec3 pos;
        protected WorldGuiIntersectionResult(Vec2 normal, Vec3 pos) {
            this.normal = normal;
            this.pos = pos;
        }
    }
    public WorldGuiType guiType;
    public WorldGui gui;
}
