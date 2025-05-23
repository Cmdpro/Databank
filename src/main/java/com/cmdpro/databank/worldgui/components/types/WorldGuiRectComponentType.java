package com.cmdpro.databank.worldgui.components.types;

import com.cmdpro.databank.worldgui.WorldGui;
import com.cmdpro.databank.worldgui.components.WorldGuiComponent;
import net.minecraft.nbt.CompoundTag;

public abstract class WorldGuiRectComponentType extends WorldGuiPositionedComponentType {
    @Override
    public void saveData(WorldGuiComponent component, CompoundTag tag) {
        if (component instanceof WorldGuiRectComponent rectComponent) {
            tag.putInt("width", rectComponent.width);
            tag.putInt("height", rectComponent.height);
        }
    }

    @Override
    public WorldGuiComponent loadData(WorldGui gui, CompoundTag tag) {
        if (super.loadData(gui, tag) instanceof WorldGuiRectComponent rectComponent) {
            rectComponent.width = tag.getInt("width");
            rectComponent.height = tag.getInt("height");
            return rectComponent;
        }
        return null;
    }
}
