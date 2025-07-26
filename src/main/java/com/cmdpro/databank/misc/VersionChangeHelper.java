package com.cmdpro.databank.misc;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.registry.AttachmentTypeRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforgespi.language.IModFileInfo;

import java.util.*;

@EventBusSubscriber(modid = Databank.MOD_ID)
public class VersionChangeHelper {
    private static final HashMap<String, PlayerVersionChangeListener> playerListeners = new HashMap<>();
    private static final HashMap<String, LevelVersionChangeListener> levelListeners = new HashMap<>();
    public interface PlayerVersionChangeListener {
        public void onChange(String modId, String from, String to, ServerPlayer player);
    }
    public interface LevelVersionChangeListener {
        public void onChange(String modId, String from, String to, ServerLevel level);
    }

    public static void registerPlayerListener(String modId, PlayerVersionChangeListener listener) {
        playerListeners.put(modId, listener);
    }
    public static void registerLevelListener(String modId, LevelVersionChangeListener listener) {
        levelListeners.put(modId, listener);
    }

    @SubscribeEvent
    protected static void onJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) {
            return;
        }
        if (event.getEntity() instanceof ServerPlayer player) {
            HashMap<String, String> oldVersions = player.getData(AttachmentTypeRegistry.MOD_VERSIONS);
            HashMap<String, String> newMap = createVersionMap(playerListeners.keySet());
            HashMap<String, VersionChange> changes = getChanges(oldVersions, newMap);
            for (Map.Entry<String, VersionChange> i : changes.entrySet()) {
                playerListeners.get(i.getKey()).onChange(i.getKey(), i.getValue().from, i.getValue().to, player);
            }
            player.setData(AttachmentTypeRegistry.MOD_VERSIONS, newMap);
        }
    }
    @SubscribeEvent
    protected static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (event.getLevel() instanceof ServerLevel level) {
            HashMap<String, String> oldVersions = level.getData(AttachmentTypeRegistry.MOD_VERSIONS);
            HashMap<String, String> newMap = createVersionMap(levelListeners.keySet());
            HashMap<String, VersionChange> changes = getChanges(oldVersions, newMap);
            for (Map.Entry<String, VersionChange> i : changes.entrySet()) {
                levelListeners.get(i.getKey()).onChange(i.getKey(), i.getValue().from, i.getValue().to, level);
            }
            level.setData(AttachmentTypeRegistry.MOD_VERSIONS, newMap);
        }
    }
    private static HashMap<String, VersionChange> getChanges(HashMap<String, String> oldMap, HashMap<String, String> newMap) {
        HashMap<String, VersionChange> changes = new HashMap<>();
        for (Map.Entry<String, String> i : newMap.entrySet()) {
            boolean different = false;
            String oldVer = null;
            if (oldMap.containsKey(i.getKey())) {
                oldVer = oldMap.get(i.getKey());
                if (!oldVer.equals(i.getValue())) {
                    different = true;
                }
            } else {
                different = true;
            }
            if (different) {
                changes.put(i.getKey(), new VersionChange(oldVer, i.getValue()));
            }
        }
        return changes;
    }
    protected static HashMap<String, String> createVersionMap(Set<String> modIds) {
        HashMap<String, String> map = new HashMap<>();
        for (String i : modIds) {
            IModFileInfo file = ModList.get().getModFileById(i);
            if (file != null) {
                map.put(i, file.versionString());
            }
        }
        return map;
    }
    private record VersionChange(String from, String to) {}
}
