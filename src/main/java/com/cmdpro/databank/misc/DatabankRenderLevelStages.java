package com.cmdpro.databank.misc;

import com.cmdpro.databank.Databank;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DatabankRenderLevelStages {
    public static RenderLevelStageEvent.Stage AFTER_HAND;

    @SubscribeEvent
    protected static void registerStages(RenderLevelStageEvent.RegisterStageEvent event) {
        AFTER_HAND = event.register(Databank.locate("after_hand"), null);
    }
}
