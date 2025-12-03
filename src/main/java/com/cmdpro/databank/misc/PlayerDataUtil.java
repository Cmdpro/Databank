package com.cmdpro.databank.misc;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.mixin.PlayerDataStorageAccessor;
import com.cmdpro.databank.mixin.PlayerListAccessor;
import com.mojang.authlib.GameProfile;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PlayerDataUtil {
    public static Optional<CompoundTag> load(MinecraftServer server, String path, String suffix) {
        File playerDir = getPlayerDir(server);
        File file1 = new File(playerDir, path + suffix);
        if (file1.exists() && file1.isFile()) {
            try {
                return Optional.of(NbtIo.readCompressed(file1.toPath(), NbtAccounter.unlimitedHeap()));
            } catch (Exception exception) {
                Databank.LOGGER.warn("Failed to load player data for {}", path);
            }
        }

        return Optional.empty();
    }
    public static Optional<CompoundTag> load(MinecraftServer server, String path) {
        return load(server, path, ".dat");
    }
    public static Optional<CompoundTag> load(MinecraftServer server, Player player) {
        PlayerDataStorage storage = ((PlayerListAccessor)server.getPlayerList()).getPlayerIo();
        return storage.load(player);
    }
    public static File getPlayerDir(MinecraftServer server) {
        return ((PlayerDataStorageAccessor)(((PlayerListAccessor)server.getPlayerList()).getPlayerIo())).getPlayerDir();
    }
    public static void save(MinecraftServer server, CompoundTag data, String path) {
        try {
            Path playerDir = getPlayerDir(server).toPath();
            Path path1 = Files.createTempFile(playerDir, path + "-", ".dat");
            NbtIo.writeCompressed(data, path1);
            Path path2 = playerDir.resolve(path + ".dat");
            Path path3 = playerDir.resolve(path + ".dat_old");
            Util.safeReplaceFile(path2, path1, path3);
        } catch (Exception exception) {
            Databank.LOGGER.warn("Failed to save player data for {}", path);
        }
    }
    public static void save(MinecraftServer server, Player player) {
        PlayerDataStorage storage = ((PlayerListAccessor)server.getPlayerList()).getPlayerIo();
        storage.save(player);
    }
    public static List<String> getAllUUIDS(MinecraftServer server) {
        File playerDir = getPlayerDir(server);
        var files = playerDir.listFiles();
        if (files == null) {
            return List.of();
        }
        List<String> uuids = new ArrayList<>();
        for (File i : files) {
            if (FilenameUtils.getExtension(i.getName()).equals("dat")) {
                uuids.add(FilenameUtils.getBaseName(i.getName()));
            }
        }
        return uuids;
    }
    public static List<String> getAllOfflineUUIDS(MinecraftServer server) {
        List<String> uuids = getAllUUIDS(server);
        uuids = uuids.stream().filter((i) -> server.getPlayerList().getPlayers().stream().noneMatch((j) -> j.getStringUUID().equals(i))).toList();
        return uuids;
    }
    public static void modifyOfflinePlayerData(MinecraftServer server, String uuid, Function<CompoundTag, Optional<CompoundTag>> func) {
        Optional<CompoundTag> data = load(server, uuid);
        if (data.isPresent()) {
            Optional<CompoundTag> funcResult = func.apply(data.get());
            funcResult.ifPresent(tag -> save(server, tag, uuid));
        }
    }
}
