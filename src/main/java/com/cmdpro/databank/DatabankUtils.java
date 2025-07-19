package com.cmdpro.databank;

import com.cmdpro.databank.hidden.Hidden;
import com.cmdpro.databank.hidden.HiddenManager;
import com.cmdpro.databank.mixin.client.ClientAdvancementsMixin;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.*;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementNode;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientAdvancements;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabankUtils {
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
    public static void updateHidden(Player player) {
        updateHidden(player, true);
    }
    public static void updateHidden(Player player, boolean updateListeners) {
        List<ResourceLocation> unlocked = new ArrayList<>();
        for (Map.Entry<ResourceLocation, Hidden> i : HiddenManager.hidden.entrySet()) {
            if (i.getValue().condition.isUnlocked(player)) {
                unlocked.add(i.getKey());
            }
        }
        ModMessages.sendToPlayer(new UnlockedHiddenSyncS2CPacket(unlocked, updateListeners), (ServerPlayer)player);
    }
    public static void unlockHiddenBlock(Player player, ResourceLocation hiddenBlock) {
        ModMessages.sendToPlayer(new UnlockHiddenSyncS2CPacket(hiddenBlock), (ServerPlayer)player);
    }
    public static void sendUnlockAdvancement(Player player, ResourceLocation advancement) {
        ModMessages.sendToPlayer(new UnlockAdvancementS2CPacket(advancement), (ServerPlayer)player);
    }
    public static void sendLockAdvancement(Player player, ResourceLocation advancement) {
        ModMessages.sendToPlayer(new LockAdvancementS2CPacket(advancement), (ServerPlayer)player);
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
