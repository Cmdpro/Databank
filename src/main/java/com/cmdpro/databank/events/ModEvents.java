package com.cmdpro.databank.events;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.HiddenBlockSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@EventBusSubscriber(modid = Databank.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(HiddenBlocksManager.getOrCreateInstance());
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
    }
}
