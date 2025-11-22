package com.cmdpro.databank.dialogue.styles;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.dialogue.DialogueStyle;
import com.cmdpro.databank.model.DatabankModel;
import com.cmdpro.databank.model.DatabankPartData;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

public class DialogueStyleManager {
    public static void init() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        if (mc.getResourceManager() instanceof ReloadableResourceManager resourceManager)
            resourceManager.registerReloadListener(DialogueStyleManager::reload);
    }
    public static HashMap<ResourceLocation, DialogueStyle> styles = new HashMap<>();
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager,
                                                 ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor,
                                                 Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> resourceManager.listResources("databank/dialogue/styles", name -> name.toString().endsWith(".json")), backgroundExecutor)
                .thenApply(resources -> {
                    Map<ResourceLocation, CompletableFuture<DialogueStyle>> tasks = new HashMap<>();
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
                            return DialogueStyle.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                        }));
                    }
                    return tasks;
                })
                .thenCompose(stage::wait).thenAccept((tasks) -> {
                    HashMap<ResourceLocation, DialogueStyle> styles = new HashMap<ResourceLocation, DialogueStyle>();
                    for (Map.Entry<ResourceLocation, CompletableFuture<DialogueStyle>> i : tasks.entrySet()) {
                        String path = i.getKey().getPath().replaceFirst("databank/dialogue/styles/", "");
                        path = path.substring(0, path.length()-5);
                        styles.put(i.getKey().withPath(path), i.getValue().join());
                    }
                    DialogueStyleManager.styles = styles;
                    Databank.LOGGER.info("[DATABANK] Loaded Databank Dialogue Styles");
                });
    }
}
