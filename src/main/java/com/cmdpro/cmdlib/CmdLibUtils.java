package com.cmdpro.cmdlib;

import com.cmdpro.cmdlib.hiddenblocks.HiddenBlock;
import com.cmdpro.cmdlib.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.cmdlib.networking.ModMessages;
import com.cmdpro.cmdlib.networking.packet.UnlockHiddenBlockSyncS2CPacket;
import com.cmdpro.cmdlib.networking.packet.UnlockedHiddenBlocksSyncS2CPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CmdLibUtils {
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
}
