package com.cmdpro.databank.worldgui;

import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.worldgui.components.WorldGuiComponent;
import com.cmdpro.databank.worldgui.components.WorldGuiComponentType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public abstract class WorldGui {
    public WorldGuiEntity entity;
    public List<WorldGuiComponent> components;
    public WorldGui(WorldGuiEntity entity) {
        this.entity = entity;
        this.components = new ArrayList<>();
    }
    public WorldGui addComponent(WorldGuiComponent component) {
        if (!components.contains(component)) {
            components.add(component);
        }
        return this;
    }
    public WorldGui removeComponent(WorldGuiComponent component) {
        components.remove(component);
        return this;
    }
    public WorldGui removeComponents(Predicate<WorldGuiComponent> predicate) {
        components.removeAll(components.stream().filter(predicate).toList());
        return this;
    }
    public void renderComponents(GuiGraphics guiGraphics) {
        for (WorldGuiComponent i : entity.gui.components) {
            i.render(guiGraphics);
        }
    }
    public boolean tryLeftClickComponent(boolean isClient, Player player, WorldGuiComponent component, int x, int y) {
        return true;
    }
    public boolean tryRightClickComponent(boolean isClient, Player player, WorldGuiComponent component, int x, int y) {
        return true;
    }
    public abstract void addInitialComponents();
    public abstract WorldGuiType getType();
    public abstract void sendData(CompoundTag tag);
    public abstract void recieveData(CompoundTag tag);
    public void renderGui(GuiGraphics guiGraphics) {
        renderComponents(guiGraphics);
    }
    public void leftClick(boolean isClient, Player player, int x, int y) {}
    public void rightClick(boolean isClient, Player player, int x, int y) {}
    public void tick() {}
    public List<Matrix3f> getMatrixs() {
        return new ArrayList<>();
    }
    public void addMatrixsForFacingPlayer(List<Matrix3f> matrixs, boolean horizontal, boolean vertical) {
        if (entity.level().isClientSide) {
            Vec2 angle = ClientHandler.angleToClient(this);
            matrixs.add(new Matrix3f()
                    .rotateX((float)Math.toRadians(-90))
            );
            if (horizontal) {
                matrixs.add(new Matrix3f()
                        .rotateZ((float) Math.toRadians(-angle.y + 180))
                );
            }
            if (vertical) {
                matrixs.add(new Matrix3f()
                        .rotateX((float)Math.toRadians(-angle.x))
                );
            }
        }
    }
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
        public static Vec2 angleToClient(WorldGui gui) {
            Vec3 pointA = gui.entity.position();
            Vec3 pointB = Minecraft.getInstance().player.getEyePosition();
            return calculateRotationVector(pointA, pointB);
        }
        private static Vec2 calculateRotationVector(Vec3 pVec, Vec3 pTarget) {
            double d0 = pTarget.x - pVec.x;
            double d1 = pTarget.y - pVec.y;
            double d2 = pTarget.z - pVec.z;
            double d3 = Math.sqrt(d0 * d0 + d2 * d2);
            return new Vec2(
                    Mth.wrapDegrees((float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)))),
                    Mth.wrapDegrees((float)(Mth.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F)
            );
        }
    }
}
