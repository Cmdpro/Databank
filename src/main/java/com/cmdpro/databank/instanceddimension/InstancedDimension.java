package com.cmdpro.databank.instanceddimension;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.misc.PlayerDataUtil;
import com.cmdpro.databank.mixin.ChunkMapAccessor;
import com.cmdpro.databank.mixin.MinecraftServerAccessor;
import com.cmdpro.databank.mixin.PlayerListAccessor;
import com.cmdpro.databank.mixin.WorldBorderAccessor;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.AddDimensionS2CPacket;
import com.cmdpro.databank.networking.packet.RemoveDimensionS2CPacket;
import com.cmdpro.databank.registry.AttachmentTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.RandomSequences;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.apache.commons.io.FileUtils;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class InstancedDimension {
    public static final Codec<InstancedDimension> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            LevelStem.CODEC.fieldOf("stem").forGetter((obj) -> obj.stem),
            Codec.BOOL.fieldOf("saves").forGetter((obj) -> obj.saves),
            Codec.BOOL.optionalFieldOf("deletesWhenNoPlayers", false).forGetter((obj) -> obj.deletesWhenNoPlayers)
    ).apply(instance, InstancedDimension::new));
    public ResourceLocation id;
    public LevelStem stem;
    public boolean saves;
    public boolean deletesWhenNoPlayers;
    public InstancedDimension(LevelStem stem, boolean saves, boolean deletesWhenNoPlayers) {
        this.stem = stem;
        this.saves = saves;
        this.deletesWhenNoPlayers = deletesWhenNoPlayers;
    }
    public Instance create(String identifier) {
        ResourceKey<Level> key = ResourceKey.create(Registries.DIMENSION, Databank.locate(id.getNamespace() + "-" + id.getPath() + "-" + identifier));
        return new Instance(id, key);
    }
    public Instance create() {
        return create(UUID.randomUUID().toString());
    }
    protected Supplier<AttachmentType<ArrayList<Instance>>> getAttachmentType() {
        return saves ? AttachmentTypeRegistry.INSTANCED_DIMENSIONS : AttachmentTypeRegistry.TEMP_INSTANCED_DIMENSIONS;
    }
    public static class Instance {
        public static final Codec<Instance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                ResourceLocation.CODEC.fieldOf("dimension").forGetter((obj) -> obj.dimension),
                ResourceKey.codec(Registries.DIMENSION).fieldOf("key").forGetter((obj) -> obj.key)
        ).apply(instance, Instance::new));
        public ResourceLocation dimension;
        public ResourceKey<Level> key;
        public ServerLevel level;
        public Instance(ResourceLocation dimension, ResourceKey<Level> key) {
            this.dimension = dimension;
            this.key = key;
        }
        public InstancedDimension getInstancedDimension() {
            return InstancedDimensionManager.instanceddimensions.get(dimension);
        }
        public ServerLevel getOrCreateDimension(MinecraftServer server) {
            if (level == null) {
                level = createDimension(server);
            }
            return level;
        }
        protected ServerLevel createDimension(MinecraftServer server) {
            LevelStem stem = getInstancedDimension().stem;
            MinecraftServerAccessor accessor = ((MinecraftServerAccessor)server);
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            WorldBorder worldborder = overworld.getWorldBorder();
            WorldOptions worldoptions = server.getWorldData().worldGenOptions();
            long i = worldoptions.seed();
            long j = BiomeManager.obfuscateSeed(i);
            RandomSequences randomsequences = overworld.getRandomSequences();
            ResourceKey<Level> resourcekey1 = ResourceKey.create(Registries.DIMENSION, key.location());
            DerivedLevelData derivedleveldata = new DerivedLevelData(server.getWorldData(), server.getWorldData().overworldData());
            ServerLevel serverlevel1 = new ServerLevel(server, accessor.getExecutor(), accessor.getStorageSource(), derivedleveldata, resourcekey1, stem, ((ChunkMapAccessor)overworld.getChunkSource().chunkMap).getProgressListener(), server.getWorldData().isDebugWorld(), j, ImmutableList.of(), false, randomsequences);
            BorderChangeListener.DelegateBorderChangeListener listener = new LevelLinkedDelegateBorderChangeListener(serverlevel1);
            worldborder.addListener(listener);
            accessor.getLevels().put(resourcekey1, serverlevel1);
            NeoForge.EVENT_BUS.post(new LevelEvent.Load(accessor.getLevels().get(key)));
            server.markWorldsDirty();
            ModMessages.sendToAllPlayers(new AddDimensionS2CPacket(key));
            var list = server.overworld().getData(getInstancedDimension().getAttachmentType());
            if (list.stream().noneMatch((k) -> k.key.equals(key))) list.add(this);
            server.overworld().setData(getInstancedDimension().getAttachmentType(), list);
            return serverlevel1;
        }
        public void removeDimension(MinecraftServer server) {
            MinecraftServerAccessor accessor = ((MinecraftServerAccessor)server);
            for (Player o : level.players()) {
                InstancedDimensionManager.teleportPlayerOut(server, o);
            }
            for (String o : PlayerDataUtil.getAllOfflineUUIDS(server)) {
                PlayerDataUtil.modifyOfflinePlayerData(server, o, (data) -> {
                    if (data.contains("Dimension")) {
                        ResourceKey<Level> resourcekey = DimensionType.parseLegacy(new Dynamic<>(NbtOps.INSTANCE, data.get("Dimension"))).getOrThrow();
                        if (resourcekey.equals(key)) {
                            String attachmentsKey = ResourceLocation.fromNamespaceAndPath("neoforge", "attachments").toString();
                            if (data.contains(attachmentsKey)) {
                                CompoundTag attachments = data.getCompound(attachmentsKey);
                                ResourceLocation lastLocationDataKey = ResourceLocation.fromNamespaceAndPath(Databank.MOD_ID, "last_location_data");
                                if (attachments.contains(lastLocationDataKey.toString())) {
                                    CompoundTag lastLocationData = attachments.getCompound(lastLocationDataKey.toString());
                                    String level = lastLocationData.getString("level");
                                    ListTag pos = lastLocationData.getList("pos", CompoundTag.TAG_DOUBLE);
                                    data.putString("Dimension", level);
                                    data.put("Pos", pos);
                                    return Optional.of(data);
                                }
                            }
                        }
                    }
                    return Optional.empty();
                });
            }
            if (accessor.getLevels().containsKey(key)) {
                try {
                    ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                    ServerLevel level = accessor.getLevels().get(key);
                    NeoForge.EVENT_BUS.post(new LevelEvent.Unload(level));
                    accessor.getLevels().remove(key);
                    List<BorderChangeListener> listeners = ((WorldBorderAccessor)overworld.getWorldBorder()).getListeners();
                    BorderChangeListener toRemove = null;
                    for (BorderChangeListener i : listeners) {
                        if (i instanceof LevelLinkedDelegateBorderChangeListener listener) {
                            if (listener.level == level) {
                                toRemove = i;
                            }
                        }
                    }
                    listeners.remove(toRemove);
                    level.close();
                    server.markWorldsDirty();
                    Path path = ((MinecraftServerAccessor) server).getStorageSource().getDimensionPath(key).toRealPath();
                    FileUtils.deleteDirectory(new File(path.toString()));
                    ModMessages.sendToAllPlayers(new RemoveDimensionS2CPacket(key));
                    var list = server.overworld().getData(getInstancedDimension().getAttachmentType());
                    list.removeIf((i) -> i.key.equals(key));
                    server.overworld().setData(getInstancedDimension().getAttachmentType(), list);
                } catch (Exception e) {}
            }
        }
    }
}
