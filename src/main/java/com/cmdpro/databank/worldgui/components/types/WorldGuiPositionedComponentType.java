package com.cmdpro.databank.worldgui.components.types;

import com.cmdpro.databank.worldgui.WorldGui;
import com.cmdpro.databank.worldgui.components.WorldGuiComponent;
import com.cmdpro.databank.worldgui.components.WorldGuiComponentType;
import net.minecraft.nbt.CompoundTag;

public abstract class WorldGuiPositionedComponentType extends WorldGuiComponentType {
    @Override
    public void saveData(WorldGuiComponent component, CompoundTag tag) {
        if (component instanceof WorldGuiPositionedComponent positionedComponent) {
            tag.putInt("x", positionedComponent.x);
            tag.putInt("y", positionedComponent.y);
        }
    }

    @Override
    public WorldGuiComponent loadData(WorldGui gui, CompoundTag tag) {
        if (createComponent(gui) instanceof WorldGuiPositionedComponent positionedComponent) {
            positionedComponent.x = tag.getInt("x");
            positionedComponent.y = tag.getInt("y");
            return positionedComponent;
        }
        return null;
    }
}
