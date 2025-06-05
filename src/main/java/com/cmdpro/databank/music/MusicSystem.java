package com.cmdpro.databank.music;

import com.cmdpro.databank.Databank;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class MusicSystem {
    public static void init() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        if (mc.getResourceManager() instanceof ReloadableResourceManager resourceManager)
            resourceManager.registerReloadListener(MusicManager.getOrCreateInstance());
    }
    public static SimpleSoundInstance music;
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null)
        {
            boolean playMusic = false;
            SoundEvent mus = null;
            List<MusicController> sortedControllers = MusicManager.musicControllers.values().stream().sorted((a, b) -> Integer.compare(b.priority, a.priority)).toList();
            for (MusicController i : sortedControllers) {
                SoundEvent getMusic = i.music;
                if (i.condition.isPlaying()) {
                    SoundEvent override = i.condition.getMusicOverride(i);
                    mus = override == null ? getMusic : override;
                    playMusic = true;
                    break;
                }
            }
            SoundManager manager = mc.getSoundManager();
            if (manager.isActive(music))
            {
                mc.getMusicManager().stopPlaying();
                if (!playMusic)
                {
                    manager.stop(music);
                } else {
                    if (!music.getLocation().equals(mus.getLocation())) {
                        manager.stop(music);
                    }
                }
            }
            if (!manager.isActive(music))
            {
                if (!manager.isActive(music) && playMusic)
                {
                    music = SimpleSoundInstance.forMusic(mus);
                    manager.play(music);
                }
            }
        }
    }
}
