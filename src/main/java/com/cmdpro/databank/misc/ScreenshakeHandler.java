package com.cmdpro.databank.misc;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.config.DatabankClientConfig;
import com.mojang.blaze3d.Blaze3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

import java.util.ArrayList;
import java.util.List;

public class ScreenshakeHandler {
    public static List<Screenshake> screenshakes = new ArrayList<>();
    public static class Screenshake {
        public FloatGradient intensity;
        public int startTicks;
        public int ticks;
        public int delay;
        public Screenshake(int delay, int startTicks, FloatGradient intensity) {
            this.delay = delay;
            this.startTicks = startTicks;
            this.intensity = intensity;
            this.ticks = startTicks;
        }
        protected void tick() {
            if (delay > 0) {
                delay--;
            } else {
                ticks--;
            }
        }
        public float getIntensity() {
            return this.intensity.getValue(1f-((float)ticks/(float)startTicks));
        }
        public float getAdjustedIntensity() {
            return getIntensity() * (float)DatabankClientConfig.screenshakeMultiplier;
        }
        public void computeAngles(ViewportEvent.ComputeCameraAngles event) {
            float intensity = getAdjustedIntensity();
            float ySpeed = 20;
            float xSpeed = (ySpeed*1.5f);
            float time = (float)(Blaze3D.getTime()*360f);
            float pitchOffset = (float)(Math.cos(time*ySpeed))*intensity;
            float yawOffset = (float)(Math.sin(time*xSpeed))*intensity;
            event.setPitch(event.getPitch()+pitchOffset);
            event.setYaw(event.getYaw()+yawOffset);
        }
    }
    public static Screenshake addScreenshake(int time, int delay, FloatGradient intensity) {
        Screenshake screenshake = new Screenshake(time, delay, intensity);
        screenshakes.add(screenshake);
        return screenshake;
    }
    public static Screenshake addScreenshake(int time, int delay, float startIntensity) {
        Screenshake screenshake = new Screenshake(delay, time, FloatGradient.singleValue(startIntensity).fade(1, 0));
        screenshakes.add(screenshake);
        return screenshake;
    }
    public static Screenshake addScreenshake(int time, float startIntensity) {
        Screenshake screenshake = new Screenshake(0, time, FloatGradient.singleValue(startIntensity).fade(1, 0));
        screenshakes.add(screenshake);
        return screenshake;
    }
    public static Screenshake addScreenshake(int time, FloatGradient startIntensity) {
        Screenshake screenshake = new Screenshake(0, time, startIntensity);
        screenshakes.add(screenshake);
        return screenshake;
    }
    @EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    protected static class GameEvents {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Pre event) {
            for (Screenshake i : screenshakes) {
                i.tick();
            }
            screenshakes.removeIf((i) -> i.ticks <= 0);
        }
        @SubscribeEvent
        public static void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
            Screenshake strongestScreenshake = null;
            for (Screenshake i : screenshakes) {
                if (strongestScreenshake == null || strongestScreenshake.getIntensity() < i.getIntensity()) {
                    if (i.delay <= 0) {
                        strongestScreenshake = i;
                    }
                }
            }
            if (strongestScreenshake != null) {
                strongestScreenshake.computeAngles(event);
            }
        }
    }
}
