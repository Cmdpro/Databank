package com.cmdpro.databank.music;

import com.cmdpro.databank.Databank;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class MusicSystem {
    public static List<MusicController> musicControllers = new ArrayList<>();
    public static SimpleSoundInstance music;
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null)
        {
            boolean playMusic = false;
            SoundEvent mus = null;
            List<MusicController> sortedControllers = musicControllers.stream().sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority())).toList();
            for (MusicController i : sortedControllers) {
                SoundEvent getMusic = i.getMusic();
                if (getMusic != null) {
                    mus = getMusic;
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
