package com.cmdpro.databank.model.animation;

import com.cmdpro.databank.model.DatabankModel;
import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public class DatabankEntityAnimationState extends DatabankAnimationState {
    public Entity entity;
    public DatabankEntityAnimationState(String defaultAnim, Entity entity) {
        super(defaultAnim);
        this.entity = entity;
    }

    @Override
    protected double getTime() {
        float partialTicks = 0;
        if (level.isClientSide) {
            partialTicks = ClientHandler.getPartialTicks();
        }
        return (entity.tickCount+partialTicks)/20d;
    }
    private static class ClientHandler {
        public static float getPartialTicks() {
            return Minecraft.getInstance().getTimer().getGameTimeDeltaPartialTick(false);
        }
    }
}
