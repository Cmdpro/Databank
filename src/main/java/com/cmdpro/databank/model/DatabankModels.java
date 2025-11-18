package com.cmdpro.databank.model;

import com.cmdpro.databank.Databank;
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

public class DatabankModels {
    public static final int CURRENT_MODEL_VERSION = 1;
    public static void init() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }
        if (mc.getResourceManager() instanceof ReloadableResourceManager resourceManager)
            resourceManager.registerReloadListener(DatabankModels::reload);
    }

    // Temporary, Remove at some point, only here to stop things from breaking when transitioning the jsons
    static List<String> oldJsons = new ArrayList<>();
    // Temporary, Remove at some point, only here to stop things from breaking when transitioning the jsons
    protected static JsonObject fixJson(String name, JsonObject json) {
        JsonObject newJson = json;
        int originalVersion = 0;
        if (json.has("version")) {
            originalVersion = json.get("version").getAsInt();
        }
        int version = originalVersion;
        if (version == 0) {
            newJson = new JsonObject();
            JsonArray parts = json.get("parts").getAsJsonArray();
            JsonArray newParts = new JsonArray();
            for (JsonElement i : parts) {
                newParts.add(updateV0Part(i.getAsJsonObject()));
            }
            newJson.add("parts", newParts);
            newJson.add("animations", json.get("animations"));
            newJson.add("textureSize", json.get("textureSize"));
            version = 1;
        }
        if (version != originalVersion) {
            newJson.addProperty("version", version);
            oldJsons.add(name);
        }
        return newJson;
    }
    // Temporary, Remove at some point, only here to stop things from breaking when transitioning the jsons
    private static JsonObject updateV0Part(JsonObject part) {
        JsonObject newJson = new JsonObject();
        String partType = "databank:group";
        if (part.get("isCube").getAsBoolean()) {
            partType = "databank:cube";
        }
        if (part.get("isMesh").getAsBoolean()) {
            partType = "databank:mesh";
        }
        newJson.addProperty("type", partType);
        newJson.addProperty("name", part.get("name").getAsString());
        List<String> copyProperties = new ArrayList<>();
        if (partType.equals("databank:group")) {
            copyProperties.add("rotation");
            copyProperties.add("offset");
            JsonArray children = new JsonArray();
            for (JsonElement i : part.get("children").getAsJsonArray()) {
                children.add(updateV0Part(i.getAsJsonObject()));
            }
            newJson.add("children", children);
        }
        if (partType.equals("databank:cube")) {
            copyProperties.add("rotation");
            copyProperties.add("offset");
            copyProperties.add("texOffset");
            copyProperties.add("mirror");
            copyProperties.add("origin");
            copyProperties.add("dimensions");
            copyProperties.add("inflate");
        }
        if (partType.equals("databank:mesh")) {
            copyProperties.add("rotation");
            copyProperties.add("offset");
            copyProperties.add("mirror");
            JsonArray newFaces = new JsonArray();
            JsonObject newVertices = new JsonObject();
            for (JsonElement face : part.get("faces").getAsJsonArray()) {
                JsonArray newFace = new JsonArray();
                for (JsonElement vertex : face.getAsJsonArray()) {
                    JsonObject vertObj = vertex.getAsJsonObject();
                    String vertexId = String.valueOf(newVertices.size());
                    JsonObject newVertex = new JsonObject();
                    newVertex.add("x", vertObj.get("x"));
                    newVertex.add("y", vertObj.get("y"));
                    newVertex.add("z", vertObj.get("z"));
                    newVertex.add("weights", new JsonObject());
                    newVertices.add(vertexId, newVertex);

                    JsonObject newVertRef = new JsonObject();
                    newVertRef.addProperty("id", vertexId);
                    newVertRef.add("u", vertObj.get("u"));
                    newVertRef.add("v", vertObj.get("v"));
                    newFace.add(newVertRef);
                }
                newFaces.add(newFace);
            }
            newJson.add("faces", newFaces);
            newJson.add("vertices", newVertices);
        }
        HashMap<String, JsonElement> copyPropertyDefaults = new HashMap<>();

        JsonArray texOffsetDefault = new JsonArray();
        texOffsetDefault.add(0);
        texOffsetDefault.add(0);
        copyPropertyDefaults.put("texOffset", texOffsetDefault);

        JsonArray originDefault = new JsonArray();
        originDefault.add(0);
        originDefault.add(0);
        originDefault.add(0);
        copyPropertyDefaults.put("origin", originDefault);

        JsonArray dimensionsDefault = new JsonArray();
        dimensionsDefault.add(1);
        dimensionsDefault.add(1);
        dimensionsDefault.add(1);
        copyPropertyDefaults.put("dimensions", dimensionsDefault);

        copyPropertyDefaults.put("mirror", new JsonPrimitive(false));
        copyPropertyDefaults.put("inflate", new JsonPrimitive(0));

        for (String i : copyProperties) {
            JsonElement property = part.has(i) ? part.get(i) : copyPropertyDefaults.get(i).deepCopy();
            newJson.add(i, property);
        }
        return newJson;
    }
    public static HashMap<ResourceLocation, DatabankModel> models = new HashMap<>();
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    public static CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager,
                                                 ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor,
                                                 Executor gameExecutor) {
        return CompletableFuture.supplyAsync(() -> resourceManager.listResources("databank/models", name -> name.toString().endsWith(".json")), backgroundExecutor)
                .thenApply(resources -> {
                    Map<ResourceLocation, CompletableFuture<DatabankModel>> tasks = new HashMap<>();
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
                            json = fixJson(i.toString(), json); // Temporary, Remove at some point, only here to stop things from breaking when transitioning the jsons

                            return DatabankModel.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                        }));
                    }
                    return tasks;
                })
                .thenCompose(stage::wait).thenAccept((tasks) -> {
                    HashMap<ResourceLocation, DatabankModel> models = new HashMap<ResourceLocation, DatabankModel>();
                    for (Map.Entry<ResourceLocation, CompletableFuture<DatabankModel>> i : tasks.entrySet()) {
                        String path = i.getKey().getPath().replaceFirst("databank/models/", "");
                        path = path.substring(0, path.length()-5);
                        models.put(i.getKey().withPath(path), i.getValue().join());
                    }
                    DatabankModels.models = models;
                    // Temporary, Remove at some point, only here to stop things from breaking when transitioning the jsons
                    for (String i : oldJsons) {
                        Databank.LOGGER.info("Databank Model \"" + i + "\" needs to be updated! If you are a player, you may ignore this as it has been automatically fixed temporarily. If you are a developer, please run the python script in the databank github to update your models.");
                    }
                    oldJsons.clear();
                    //End of temporary
                    Databank.LOGGER.info("[DATABANK] Loaded Databank Models");
                });
    }
}
