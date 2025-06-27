package com.cmdpro.databank.worldgui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;

public abstract class WorldGuiType {
    public abstract WorldGui createGui(WorldGuiEntity entity);
    public void saveData(WorldGui gui, CompoundTag tag) { }
    public WorldGui loadData(WorldGuiEntity entity, CompoundTag tag) { return null; }
    public abstract Vec2 getMenuWorldSize(WorldGuiEntity entity);
    public abstract Vec2 getRenderSize();
    public boolean saves() { return false; }
    public float getViewScale() {
        return 1;
    }
}
