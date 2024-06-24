package com.cmdpro.cmdlib.music;

import com.cmdpro.cmdlib.CmdLib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CmdLib.MOD_ID)
public class MusicSystem {
    public static List<MusicController> musicControllers = new ArrayList<>();
    public static SimpleSoundInstance music;
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END && mc.level != null)
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
