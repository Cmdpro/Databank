package com.cmdpro.databank.worldgui.components.types;

import com.cmdpro.databank.worldgui.WorldGui;
import net.minecraft.nbt.CompoundTag;

public abstract class WorldGuiRectComponent extends WorldGuiPositionedComponent {
    public int width;
    public int height;

    public WorldGuiRectComponent(WorldGui gui, int x, int y, int width, int height) {
        super(gui, x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public void sendData(CompoundTag tag) {
        super.sendData(tag);
        tag.putInt("width", width);
        tag.putInt("height", height);
    }

    @Override
    public void recieveData(CompoundTag tag) {
        super.recieveData(tag);
        width = tag.getInt("width");
        height = tag.getInt("height");
    }
}
