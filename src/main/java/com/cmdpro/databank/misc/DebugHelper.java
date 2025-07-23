package com.cmdpro.databank.misc;


import com.cmdpro.databank.Databank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

import java.util.ArrayList;
import java.util.List;

public class DebugHelper {
    @EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
    protected static class ModEvents {
        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "debug"), (guiGraphics, deltaTracker) -> {
                int y = 4;
                Font font = Minecraft.getInstance().font;
                for (String i : renderedText) {
                    guiGraphics.drawString(font, i, 4, y, 0xFFFFFFFF);
                    y += font.lineHeight + 2;
                }
            });
        }
    }
    @EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
    protected static class GameEvents {
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void onClientTick(ClientTickEvent.Post event) {
            renderedText.clear();
            renderedText.addAll(debugText);
            debugText.clear();
        }
    }
    public static final List<String> debugText = new ArrayList<>();
    private static final List<String> renderedText = new ArrayList<>();
}
