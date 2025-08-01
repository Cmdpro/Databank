package com.cmdpro.databank.events;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.HiddenManager;
import com.cmdpro.databank.megastructures.MegastructureManager;
import com.cmdpro.databank.multiblock.MultiblockManager;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.HiddenSyncS2CPacket;
import com.cmdpro.databank.networking.packet.MultiblockSyncS2CPacket;
import com.cmdpro.databank.registry.CriteriaTriggerRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;

@EventBusSubscriber(modid = Databank.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void addReloadListenerEvent(AddReloadListenerEvent event) {
        event.addListener(HiddenManager.getOrCreateInstance());
        event.addListener(MultiblockManager.getOrCreateInstance());
        event.addListener(MegastructureManager.getOrCreateInstance());
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
        ModMessages.sendToPlayer(new HiddenSyncS2CPacket(HiddenManager.hidden), player);
        ModMessages.sendToPlayer(new MultiblockSyncS2CPacket(MultiblockManager.multiblocks), player);
        DatabankUtils.updateHidden(player, false);
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent.AdvancementProgressEvent event) {
        DatabankUtils.updateHidden(event.getEntity());
        if (event.getProgressType() == AdvancementEvent.AdvancementProgressEvent.ProgressType.GRANT) {
            if (event.getAdvancementProgress().isDone()) {
                DatabankUtils.sendUnlockAdvancement(event.getEntity(), event.getAdvancement().id());
                CriteriaTriggerRegistry.HAS_ADVANCEMENT.get().trigger((ServerPlayer)event.getEntity(), event.getAdvancement().id());
                CriteriaTriggerRegistry.HAS_ADVANCEMENTS.get().trigger((ServerPlayer)event.getEntity());
            }
        } else if (event.getProgressType() == AdvancementEvent.AdvancementProgressEvent.ProgressType.REVOKE) {
            if (!event.getAdvancementProgress().isDone()) {
                DatabankUtils.sendLockAdvancement(event.getEntity(), event.getAdvancement().id());
            }
        }
    }
}
