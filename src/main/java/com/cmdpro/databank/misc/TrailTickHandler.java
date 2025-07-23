package com.cmdpro.databank.misc;


import com.cmdpro.databank.Databank;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class TrailTickHandler {
    private static final List<TrailRender> trails = new ArrayList<>();
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        for (TrailRender i : trails) {
            i.tick();
        }
    }
    public static void addTrail(TrailRender trail) {
        if (!trails.contains(trail)) {
            trails.add(trail);
        }
    }
    public static void removeTrail(TrailRender trail) {
        trails.remove(trail);
    }
}
