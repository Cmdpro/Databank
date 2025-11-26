package com.cmdpro.databank.instanceddimension;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.megastructures.Megastructure;
import com.cmdpro.databank.music.MusicSerializer;
import com.cmdpro.databank.registry.AttachmentTypeRegistry;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.*;

@EventBusSubscriber(modid = Databank.MOD_ID)
public class InstancedDimensionManager extends SimpleJsonResourceReloadListener {
    public static HashMap<ResourceLocation, InstancedDimension> instanceddimensions = new HashMap<>();
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static InstancedDimensionManager instance;
    protected InstancedDimensionManager() {
        super(GSON, "databank/instanced_dimensions");
    }
    public static InstancedDimensionManager getOrCreateInstance() {
        if (instance == null) {
            instance = new InstancedDimensionManager();
        }
        return instance;
    }
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        instanceddimensions = new HashMap<>();
        Databank.LOGGER.info("[DATABANK] Adding Databank Instanced Dimensions");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                InstancedDimension dimension = InstancedDimension.CODEC.parse(RegistryOps.create(JsonOps.INSTANCE, getRegistryLookup()), obj).getOrThrow();
                dimension.id = i.getKey();
                instanceddimensions.put(i.getKey(), dimension);
            } catch (IllegalArgumentException | JsonParseException e) {
                Databank.LOGGER.error("[DATABANK ERROR] Parsing error loading instanced dimension type {}", location, e);
            }
        }
        Databank.LOGGER.info("[DATABANK] Loaded {} Instanced Dimensions", instanceddimensions.size());
    }
    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        ServerLevel overworld = event.getServer().overworld();
        List<InstancedDimension.Instance> instances = getInstances(overworld);
        List<InstancedDimension.Instance> toRemove = new ArrayList<>();
        for (InstancedDimension.Instance i : instances) {
            if (i.level != null) {
                if (i.getInstancedDimension().deletesWhenNoPlayers && i.level.players().isEmpty()) {
                    toRemove.add(i);
                }
            }
        }
        for (InstancedDimension.Instance i : toRemove) {
            i.removeDimension(event.getServer());
        }
    }
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        ServerLevel overworld = event.getEntity().getServer().overworld();
        List<InstancedDimension.Instance> instances = getInstances(overworld);
        boolean inDimension = false;
        for (InstancedDimension.Instance i : instances) {
            if (i.level != null && event.getEntity().level() == i.level) {
                inDimension = true;
            }
        }
        if (!inDimension) {
            event.getEntity().setData(AttachmentTypeRegistry.LAST_LOCATION_DATA, Optional.of(new PlayerLastLocationData(event.getEntity().level().dimension(), event.getEntity().position())));
        }
    }
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        Level overworld = event.getServer().overworld();
        List<InstancedDimension.Instance> instances = overworld.getData(AttachmentTypeRegistry.INSTANCED_DIMENSIONS);
        for (InstancedDimension.Instance i : instances.stream().toList()) {
            i.getOrCreateDimension(event.getServer());
        }
    }
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        Level overworld = event.getServer().overworld();
        List<InstancedDimension.Instance> instances = overworld.getData(AttachmentTypeRegistry.TEMP_INSTANCED_DIMENSIONS);
        for (InstancedDimension.Instance i : instances.stream().toList()) {
            i.removeDimension(event.getServer());
        }
    }
    public static List<InstancedDimension.Instance> getInstances(Level level) {
        List<InstancedDimension.Instance> instances = level.getData(AttachmentTypeRegistry.TEMP_INSTANCED_DIMENSIONS);
        instances.addAll(level.getData(AttachmentTypeRegistry.INSTANCED_DIMENSIONS));
        return instances;
    }
}
