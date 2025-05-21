package com.cmdpro.databank.worldgui;

import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.registry.EntityRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class WorldGuiEntity extends Entity {
    public WorldGuiEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
    public WorldGuiEntity(Level level, Vec3 position, WorldGuiType type) {
        this(EntityRegistry.WORLD_GUI.get(), level);
        setPos(position);
        this.guiType = type;
        this.gui = type.createGui(this);
        syncData();
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

    public CompoundTag getSyncData() {
        CompoundTag tag = new CompoundTag();
        if (guiType.saves()) {
            guiType.saveData(gui, tag);
        }
        gui.sendData(tag);
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
            }
        }
    }
    public Vec3 getBoundsCorner(float multX, float multY) {
        Vec2 size = guiType.getMenuWorldSize(this).scale(0.5f);
        size = new Vec2(size.x*multX, size.y*multY);
        Vec3 corner = position().add(size.x, size.y, 0);
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
        Vec3 topLeft = getBoundsCorner(-1, 1);
        Vec3 topRight = getBoundsCorner(1, 1);
        Vec3 bottomLeft = getBoundsCorner(-1, -1);

        // 1.
        Vec3 dS21 = topRight.subtract(topLeft);
        Vec3 dS31 = bottomLeft.subtract(topLeft);
        Vec3 n = dS21.cross(dS31);

        // 2.
        Vec3 dR = lineStart.subtract(lineEnd);

        double ndotdR = n.dot(dR);

        if (Math.abs(ndotdR) < 1e-6f) { // Choose your tolerance
            return null;
        }

        double t = -n.dot(lineStart.subtract(topLeft)) / ndotdR;
        Vec3 M = lineStart.add(dR.scale(t));

        // 3.
        Vec3 dMS1 = M.subtract(topLeft);
        double u = dMS1.dot(dS21);
        double v = dMS1.dot(dS31);

        // 4.
        if ((u >= 0.0f && u <= dS21.dot(dS21)
                && v >= 0.0f && v <= dS31.dot(dS31))) {
            return new WorldGuiIntersectionResult(new Vec2((float)u, (float)v), M);
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
