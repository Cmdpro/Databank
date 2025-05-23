package com.cmdpro.databank.worldgui.components;

import com.cmdpro.databank.worldgui.WorldGui;
import com.cmdpro.databank.worldgui.WorldGuiEntity;
import com.cmdpro.databank.worldgui.WorldGuiHitResult;
import com.cmdpro.databank.worldgui.WorldGuiType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;

import java.util.UUID;

public abstract class WorldGuiComponent {
    public UUID uuid;
    public WorldGui gui;
    public abstract void render(GuiGraphics guiGraphics);
    public void leftClick(boolean isClient, Player player, int x, int y) {}
    public void rightClick(boolean isClient, Player player, int x, int y) {}
    public abstract void sendData(CompoundTag tag);
    public abstract void recieveData(CompoundTag tag);
    public abstract WorldGuiComponentType getType();
    public int getDrawPriority() { return 0; }
    public WorldGuiComponent(WorldGui gui) {
        this.gui = gui;
        this.uuid = UUID.randomUUID();
    }
    public void sync() {
        gui.entity.syncData();
    }
    public boolean isPosInBounds(int x, int y, int minX, int minY, int maxX, int maxY) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
    public int normalXIntoGuiX(double normalX) {
        return (int)(normalX*gui.getType().getRenderSize().x);
    }
    public int normalYIntoGuiY(double normalY) {
        return (int)(normalY*gui.getType().getRenderSize().y);
    }
    public Vec2 getClientTargetNormal() {
        WorldGuiEntity entity = ClientHandler.getClientTargetGui();
        if (entity != null && entity.gui == gui) {
            return ClientHandler.getClientTargetNormal();
        }
        return null;
    }
    public static Vec2 getClientTargetNormalGlobal() {
        return ClientHandler.getClientTargetNormal();
    }
    private static class ClientHandler {
        public static Vec2 getClientTargetNormal() {
            HitResult hitResult = Minecraft.getInstance().hitResult;
            if (hitResult instanceof WorldGuiHitResult result) {
                return result.result.normal;
            }
            return null;
        }

        public static WorldGuiEntity getClientTargetGui() {
            HitResult hitResult = Minecraft.getInstance().hitResult;
            if (hitResult instanceof WorldGuiHitResult result) {
                if (result.getEntity() instanceof WorldGuiEntity entity) {
                    return entity;
                }
            }
            return null;
        }
    }
}
