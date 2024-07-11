package com.cmdpro.cmdlib.events;

import com.cmdpro.cmdlib.CmdLib;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.cmdlib.networking.ModMessages;
import com.cmdpro.cmdlib.networking.packet.HiddenBlockSyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;

@Mod.EventBusSubscriber(modid = CmdLib.MOD_ID)
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
