package com.cmdpro.databank.advancement;

import com.cmdpro.databank.hidden.Hidden;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public interface ClientAdvancementListener {
    List<ClientAdvancementListener> ADVANCEMENT_LISTENERS = new ArrayList<>();
    default void onLock(ResourceLocation locked) {}
    default void onUnlock(ResourceLocation unlocked) {}
    default void onLock(List<ResourceLocation> locked) {}
    default void onUnlock(List<ResourceLocation> unlocked) {}
}
