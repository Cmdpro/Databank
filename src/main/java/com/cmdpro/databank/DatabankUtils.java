package com.cmdpro.databank;

import com.cmdpro.databank.hidden.Hidden;
import com.cmdpro.databank.hidden.HiddenManager;
import com.cmdpro.databank.mixin.PlayerAdvancementsAccessor;
import com.cmdpro.databank.mixin.client.ClientAdvancementsMixin;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.*;
import com.cmdpro.databank.registry.CriteriaTriggerRegistry;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.*;

public class DatabankUtils {

    private static final Set<UUID> scheduledUpdateHidden = new HashSet<>();
    private static final Map<UUID, List<ResourceLocation>> playerUnlockedCache = new HashMap<>();
    private static final Map<UUID, Map<ResourceLocation, Boolean>> scheduledUpdateAdvancement = new HashMap<>();

    public static void scheduleUpdateHidden(Player player) {
        scheduledUpdateHidden.add(player.getUUID());
    }

    public static void sendScheduledUpdates(MinecraftServer server) {
        PlayerList players = server.getPlayerList();
        if (!scheduledUpdateHidden.isEmpty()) {
            for(UUID id : scheduledUpdateHidden) {
                ServerPlayer player = players.getPlayer(id);
                if (player != null && ! player.hasDisconnected())
                    updateHidden(player, true, false);
            }
            scheduledUpdateHidden.clear();
        }

        if (!scheduledUpdateAdvancement.isEmpty()) {
            for(Map.Entry<UUID, Map<ResourceLocation, Boolean>> entry : scheduledUpdateAdvancement.entrySet()) {
                ServerPlayer player = players.getPlayer(entry.getKey());
                if (player != null && ! player.hasDisconnected()) {
                    for(Map.Entry<ResourceLocation, Boolean> e : entry.getValue().entrySet()) {
                        ModMessages.sendToPlayer(e.getValue()
                                ? new UnlockAdvancementS2CPacket(e.getKey())
                                : new LockAdvancementS2CPacket(e.getKey()),
                                player
                        );
                    }
                }
            }
            scheduledUpdateAdvancement.clear();
        }
    }

    public static void uncachePlayerHidden(Player player) {
        playerUnlockedCache.remove(player.getUUID());
    }

    public static BlockState changeBlockType(BlockState originalState, Block newType) {
        BlockState state = newType.defaultBlockState();
        for (Property<?> i : originalState.getProperties()) {
            state = copyProperty(i, originalState, state);
        }
        return state;
    }
    private static <T extends Comparable<T>> BlockState copyProperty(Property<T> property, BlockState original, BlockState newState) {
        if (original.hasProperty(property) && newState.hasProperty(property)) {
            return newState.setValue(property, original.getValue(property));
        }
        return newState;
    }
    public static ItemStack changeItemType(ItemStack originalStack, Item newType) {
        return new ItemStack(Holder.direct(newType), originalStack.getCount(), originalStack.getComponentsPatch());
    }
    public static void updateHidden(Player player) {
        updateHidden(player, true, true);
    }
    public static void updateHidden(Player player, boolean updateListeners) {
        updateHidden(player, updateListeners, true);
    }
    public static void updateHidden(Player player, boolean updateListeners, boolean removeScheduled) {
        UUID id = player.getUUID();
        // If not otherwise told not to, remove this player from the scheduled updates
        // list because they'll be up-to-date after this gets sent.
        if (removeScheduled)
            scheduledUpdateHidden.remove(id);

        // Compute the player's list of unlocks.
        List<ResourceLocation> unlocked = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Hidden> i : HiddenManager.hidden.entrySet()) {
            if (i.getValue().condition.isUnlocked(player)) {
                unlocked.add(i.getKey());
            }
        }

        // If we have a cached state for this player, and it matches the new state, then we don't actually
        // need to send an update to the client.
        List<ResourceLocation> oldUnlocked = playerUnlockedCache.get(id);
        if (oldUnlocked != null && oldUnlocked.size() == unlocked.size() && unlocked.containsAll(oldUnlocked))
            return;

        // Cache the newly sent state and send it to the player.
        playerUnlockedCache.put(id, unlocked);
        ModMessages.sendToPlayer(new UnlockedHiddenSyncS2CPacket(unlocked, updateListeners), (ServerPlayer)player);
    }
    public static void unlockHiddenBlock(Player player, ResourceLocation hiddenBlock) {
        // If there's a cached set of hidden unlocks already sent to the player, we might be
        // able to avoid this.
        List<ResourceLocation> oldUnlocked = playerUnlockedCache.get(player.getUUID());
        if (oldUnlocked != null) {
            // If we have a match, we don't need to do anything.
            if (oldUnlocked.contains(hiddenBlock))
                return;

            // We don't have a match, so go ahead and add it to the cached list.
            oldUnlocked.add(hiddenBlock);
        }

        ModMessages.sendToPlayer(new UnlockHiddenSyncS2CPacket(hiddenBlock), (ServerPlayer)player);
    }
    public static void sendUnlockAdvancement(Player player, ResourceLocation advancement) {
        scheduledUpdateAdvancement.computeIfAbsent(player.getUUID(), k -> new HashMap<>()).put(advancement, true);
        //ModMessages.sendToPlayer(new UnlockAdvancementS2CPacket(advancement), (ServerPlayer)player);
    }
    public static void sendLockAdvancement(Player player, ResourceLocation advancement) {
        scheduledUpdateAdvancement.computeIfAbsent(player.getUUID(), k -> new HashMap<>()).put(advancement, false);
        //ModMessages.sendToPlayer(new LockAdvancementS2CPacket(advancement), (ServerPlayer)player);
    }
    public static float kelvinToCelcius(float kelvin) {
        return kelvin+273.15f;
    }
    public static float celciusToKelvin(float celcius) {
        return celcius-273.15f;
    }
    public static boolean hasAdvancement(Player player, ResourceLocation advancement) {
        AdvancementProgress progress = getAdvancementProgress(player, advancement);
        if (progress == null) {
            return false;
        }
        return progress.isDone();
    }
    public static AdvancementProgress getAdvancementProgress(Player player, ResourceLocation advancement) {
        if (player.level().isClientSide) {
            if (ClientHandler.isClientPlayer(player)) {
                return getAdvancementProgressClient(advancement);
            }
        } else {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            MinecraftServer server = player.level().getServer();
            if (server != null) {
                AdvancementHolder holder = server.getAdvancements().get(advancement);
                if (holder != null) {
                    return serverPlayer.getAdvancements().getOrStartProgress(holder);
                }
            }
        }
        return null;
    }
    public static boolean hasAdvancementClient(ResourceLocation advancement) {
        return ClientHandler.hasAdvancementClient(advancement);
    }
    public static AdvancementProgress getAdvancementProgressClient(ResourceLocation advancement) {
        return ClientHandler.getProgress(advancement);
    }
    public static void recheckAdvancements(ServerPlayer player) {
        Map<AdvancementHolder, AdvancementProgress> progress = ((PlayerAdvancementsAccessor)player.getAdvancements()).getProgress();
        for (Map.Entry<AdvancementHolder, AdvancementProgress> i : progress.entrySet()) {
            if (i.getValue().isDone()) {
                CriteriaTriggerRegistry.HAS_ADVANCEMENT.get().trigger(player, i.getKey().id());
                CriteriaTriggerRegistry.HAS_ADVANCEMENTS.get().trigger(player);
            }
        }
    }
    private static class ClientHandler {
        public static boolean isClientPlayer(Player player) {
            return Minecraft.getInstance().player == player;
        }
        public static boolean hasAdvancementClient(ResourceLocation advancement) {
            AdvancementProgress progress = getProgress(advancement);
            if (progress == null) {
                return false;
            }
            return progress.isDone();
        }
        public static AdvancementProgress getProgress(ResourceLocation advancement) {
            ClientPacketListener connection = Minecraft.getInstance().getConnection();
            if (connection != null) {
                ClientAdvancements advancements = connection.getAdvancements();
                AdvancementNode node = advancements.getTree().get(advancement);
                if (node != null) {
                    Map<AdvancementHolder, AdvancementProgress> progress = ((ClientAdvancementsMixin)advancements).getProgress();
                    if (progress.containsKey(node.holder())) {
                        return progress.get(node.holder());
                    }
                }
            }
            return null;
        }
    }
}
