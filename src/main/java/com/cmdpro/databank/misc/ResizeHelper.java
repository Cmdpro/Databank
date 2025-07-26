package com.cmdpro.databank.misc;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.impact.ImpactFrameHandler;
import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.databank.shaders.PostShaderManager;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class ResizeHelper {
    private static final List<ResizeListener> onResize = new ArrayList<>();
    private static Vector2i queuedResize;
    private static boolean hasFinishedLoad;
    public static void resize(int width, int height) {
        if (!hasFinishedLoad) {
            queuedResize = new Vector2i(width, height);
            return;
        }
        for (ResizeListener i : onResize) {
            i.resize(width, height);
        }
    }
    public static void addListener(ResizeListener listener) {
        if (!onResize.contains(listener)) {
            onResize.add(listener);
        }
    }
    public static void removeListener(ResizeListener listener) {
        onResize.remove(listener);
    }
    public interface ResizeListener {
        void resize(int width, int height);
    }
    @SubscribeEvent
    protected static void doSetup(RenderFrameEvent.Pre event) {
        if (!hasFinishedLoad) {
            hasFinishedLoad = true;
            if (queuedResize != null) {
                int width = queuedResize.x;
                int height = queuedResize.y;
                for (ResizeListener i : onResize) {
                    i.resize(width, height);
                }
                queuedResize = null;
            }
        }
    }
}
