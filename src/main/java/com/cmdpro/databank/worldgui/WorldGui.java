package com.cmdpro.databank.worldgui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;

public abstract class WorldGui {
    public WorldGuiEntity entity;
    public WorldGui(WorldGuiEntity entity) {
        this.entity = entity;
    }
    public abstract WorldGuiType getType();
    public abstract void sendData(CompoundTag tag);
    public abstract void recieveData(CompoundTag tag);
    public abstract void drawGui(GuiGraphics guiGraphics);
    public void leftClick(Player player, int x, int y) {}
    public void rightClick(Player player, int x, int y) {}
    public void tick() {}
    public void sync() {
        entity.syncData();
    }
    public boolean isPosInBounds(int x, int y, int minX, int minY, int maxX, int maxY) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }
    public int normalXIntoGuiX(double normalX) {
        return (int)(normalX*getType().getRenderSize().x);
    }
    public int normalYIntoGuiY(double normalY) {
        return (int)(normalY*getType().getRenderSize().y);
    }
    public Vec2 getClientTargetNormal() {
        WorldGuiEntity entity = ClientHandler.getClientTargetGui();
        if (entity != null && entity.gui == this) {
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
