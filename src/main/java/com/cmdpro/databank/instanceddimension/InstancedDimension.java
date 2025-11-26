package com.cmdpro.databank.instanceddimension;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.mixin.ChunkMapAccessor;
import com.cmdpro.databank.mixin.MinecraftServerAccessor;
import com.cmdpro.databank.mixin.WorldBorderAccessor;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.AddDimensionS2CPacket;
import com.cmdpro.databank.networking.packet.RemoveDimensionS2CPacket;
import com.cmdpro.databank.registry.AttachmentTypeRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.LevelEvent;
import org.apache.commons.io.FileUtils;
import org.spongepowered.include.com.google.common.collect.ImmutableList;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
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
            server.overworld().getData(getInstancedDimension().getAttachmentType()).add(this);
            return serverlevel1;
        }
        public void removeDimension(MinecraftServer server) {
            MinecraftServerAccessor accessor = ((MinecraftServerAccessor)server);
            for (Player o : level.players()) {
                Optional<PlayerLastLocationData> data = o.getData(AttachmentTypeRegistry.LAST_LOCATION_DATA);
                ResourceKey<Level> outside = Level.OVERWORLD;
                Vec3 pos = Vec3.ZERO;
                if (data.isPresent()) {
                    outside = data.get().level;
                    pos = data.get().pos;
                }
                o.changeDimension(new DimensionTransition(server.getLevel(outside), pos, Vec3.ZERO, 0, 0, false, (entity) -> {}));
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
                    server.overworld().getData(getInstancedDimension().getAttachmentType()).remove(this);
                } catch (Exception e) {}
            }
        }
    }
}
