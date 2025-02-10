package com.cmdpro.databank;

import com.cmdpro.databank.hiddenblock.HiddenBlock;
import com.cmdpro.databank.hiddenblock.HiddenBlocksManager;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.UnlockHiddenBlockSyncS2CPacket;
import com.cmdpro.databank.networking.packet.UnlockedHiddenBlocksSyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabankUtils {
    public static BlockState getHiddenBlock(Block block, Player player) {
        for (HiddenBlock i : HiddenBlocksManager.blocks.values()) {
            if (i.originalBlock == null || i.hiddenAs == null || i.condition == null) {
                continue;
            }
            if (i.originalBlock.equals(block)) {
                if (!i.condition.isUnlocked(player)) {
                    return i.hiddenAs;
                }
                break;
            }
        }
        return null;
    }
    public static BlockState getHiddenBlock(Block block) {
        for (HiddenBlock i : HiddenBlocksManager.blocks.values()) {
            if (i.originalBlock == null || i.hiddenAs == null || i.condition == null) {
                continue;
            }
            if (i.originalBlock.equals(block)) {
                return i.hiddenAs;
            }
        }
        return null;
    }
    public static void updateHiddenBlocks(Player player) {
        List<ResourceLocation> unlocked = new ArrayList<>();
        for (Map.Entry<ResourceLocation, HiddenBlock> i : HiddenBlocksManager.blocks.entrySet()) {
            if (i.getValue().condition.isUnlocked(player)) {
                unlocked.add(i.getKey());
            }
        }
        ModMessages.sendToPlayer(new UnlockedHiddenBlocksSyncS2CPacket(unlocked), (ServerPlayer)player);
    }
    public static void unlockHiddenBlock(Player player, ResourceLocation hiddenBlock) {
        ModMessages.sendToPlayer(new UnlockHiddenBlockSyncS2CPacket(hiddenBlock), (ServerPlayer)player);
    }
    public static float kelvinToCelcius(float kelvin) {
        return kelvin+273.15f;
    }
    public static float celciusToKelvin(float celcius) {
        return celcius-273.15f;
    }
}
