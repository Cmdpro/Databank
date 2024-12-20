package com.cmdpro.databank.events;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hiddenblock.HiddenBlocksManager;
import com.cmdpro.databank.model.DatabankModels;
import com.cmdpro.databank.multiblock.MultiblockManager;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.HiddenBlockSyncS2CPacket;
import com.cmdpro.databank.networking.packet.MultiblockSyncS2CPacket;
import com.cmdpro.databank.networking.packet.UnlockedHiddenBlocksSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

@EventBusSubscriber(modid = Databank.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(HiddenBlocksManager.getOrCreateInstance());
        event.addListener(MultiblockManager.getOrCreateInstance());
    }
    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            for (ServerPlayer player : event.getPlayerList().getPlayers()) {
                syncToPlayer(player);
            }
        } else {
            syncToPlayer(event.getPlayer());
        }
    }
    protected static void syncToPlayer(ServerPlayer player) {
        ModMessages.sendToPlayer(new HiddenBlockSyncS2CPacket(HiddenBlocksManager.blocks), player);
        ModMessages.sendToPlayer(new MultiblockSyncS2CPacket(MultiblockManager.multiblocks), player);
        DatabankUtils.updateHiddenBlocks(player);
    }
    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {
        DatabankUtils.updateHiddenBlocks(event.getEntity());
    }
}
