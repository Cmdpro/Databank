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
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

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
