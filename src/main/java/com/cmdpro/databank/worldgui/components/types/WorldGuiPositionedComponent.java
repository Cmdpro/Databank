package com.cmdpro.databank.worldgui.components.types;

import com.cmdpro.databank.worldgui.WorldGui;
import com.cmdpro.databank.worldgui.components.WorldGuiComponent;
import net.minecraft.nbt.CompoundTag;

public abstract class WorldGuiPositionedComponent extends WorldGuiComponent {
    public int x;
    public int y;
    public WorldGuiPositionedComponent(WorldGui gui, int x, int y) {
        super(gui);
        this.x = x;
        this.y = y;
    }
    @Override
    public void sendData(CompoundTag tag) {
        tag.putInt("x", x);
        tag.putInt("y", y);
    }

    @Override
    public void recieveData(CompoundTag tag) {
        x = tag.getInt("x");
        y = tag.getInt("y");
    }
}
