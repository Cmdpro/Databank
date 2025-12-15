package com.cmdpro.databank.mixin;

import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;
import java.util.List;

@Mixin(PlayerList.class)
public interface PlayerListAccessor {
    @Accessor
    PlayerDataStorage getPlayerIo();
}
