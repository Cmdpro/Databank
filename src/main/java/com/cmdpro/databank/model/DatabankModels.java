package com.cmdpro.databank.model;

import com.cmdpro.databank.Databank;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import org.apache.commons.io.IOUtils;

import javax.xml.crypto.Data;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DatabankModels {
    public static void init() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.getResourceManager() instanceof ReloadableResourceManager resourceManager)
            resourceManager.registerReloadListener(DatabankModels::reload);
    }
    public static HashMap<ResourceLocation, DatabankEntityModel> models = new HashMap<>();
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager,
                                                 ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor,
                                                 Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> resourceManager.listResources("databank/models", name -> name.toString().endsWith(".json")), backgroundExecutor)
                .thenApply(resources -> {
                    Map<ResourceLocation, CompletableFuture<DatabankEntityModel>> tasks = new HashMap<>();
                    for (ResourceLocation i : resources.keySet()) {
                        tasks.put(i, CompletableFuture.supplyAsync(() -> {
                            JsonObject json;
                            try {
                                Resource resource = resourceManager.getResourceOrThrow(i);
                                InputStream stream = resource.open();
                                json = GsonHelper.fromJson(GSON, IOUtils.toString(stream, Charset.defaultCharset()), JsonObject.class);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            return DatabankEntityModel.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                        }));
                    }
                    return tasks;
                })
                .thenCompose(stage::wait).thenAccept((tasks) -> {
                    HashMap<ResourceLocation, DatabankEntityModel> models = new HashMap<ResourceLocation, DatabankEntityModel>();
                    for (Map.Entry<ResourceLocation, CompletableFuture<DatabankEntityModel>> i : tasks.entrySet()) {
                        String path = i.getKey().getPath().replaceFirst("databank/models/", "");
                        path = path.substring(0, path.length()-5);
                        models.put(i.getKey().withPath(path), i.getValue().join());
                    }
                    DatabankModels.models = models;
                    Databank.LOGGER.info("Loaded Databank Models");
                });
    }
}
