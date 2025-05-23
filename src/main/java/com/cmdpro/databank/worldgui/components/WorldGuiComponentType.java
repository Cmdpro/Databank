package com.cmdpro.databank.worldgui.components;

import com.cmdpro.databank.worldgui.WorldGui;
import com.cmdpro.databank.worldgui.WorldGuiEntity;
import net.minecraft.nbt.CompoundTag;

public abstract class WorldGuiComponentType {
    public abstract WorldGuiComponent createComponent(WorldGui gui);
    public abstract void saveData(WorldGuiComponent component, CompoundTag tag);
    public abstract WorldGuiComponent loadData(WorldGui gui, CompoundTag tag);
}
