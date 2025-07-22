package com.cmdpro.databank.misc;

import com.cmdpro.databank.mixin.client.ChannelHandleMixin;
import com.cmdpro.databank.mixin.client.ChannelMixin;
import com.cmdpro.databank.mixin.client.SoundEngineMixin;
import com.cmdpro.databank.mixin.client.SoundManagerMixin;
import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import java.util.function.Consumer;

public class SoundUtil {
    private static SoundEngine getSoundEngine() {
        return ((SoundManagerMixin) Minecraft.getInstance().getSoundManager()).getSoundEngine();
    }

    public static ChannelAccess.ChannelHandle getChannelHandle(SoundInstance instance) {
        return ((SoundEngineMixin)getSoundEngine()).getInstanceToChannel().get(instance);
    }

    public static int getSource(Channel channel) {
        return ((ChannelMixin)channel).getSource();
    }

    public static void setTime(SoundInstance instance, float time) {
        var handle = getChannelHandle(instance);
        if (handle != null) {
            handle.execute((channel) -> {
                int source = getSource(channel);
                AL10.alSourcef(source, AL11.AL_SEC_OFFSET, time);
                AL10.alGetSourcef(source, AL11.AL_SEC_OFFSET);
            });
        }
    }

    public static float getTime(SoundInstance instance) {
        Channel channel = getChannel(instance);
        if (channel != null) {
            return AL10.alGetSourcef(getSource(channel), AL11.AL_SEC_OFFSET);
        }
        return -1;
    }

    public static void modifySound(SoundInstance instance, Consumer<Channel> consumer) {
        getChannelHandle(instance).execute(consumer);
    }

    public static Channel getChannel(SoundInstance instance) {
        return ((ChannelHandleMixin)getChannelHandle(instance)).getChannel();
    }
}
