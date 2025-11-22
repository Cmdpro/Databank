package com.cmdpro.databank.dialogue;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.megastructures.Megastructure;
import com.cmdpro.databank.music.MusicSerializer;
import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DialogueTreeManager extends SimpleJsonResourceReloadListener {
    public static HashMap<ResourceLocation, DialogueTree> trees = new HashMap<>();
    protected static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public static DialogueTreeManager instance;
    protected DialogueTreeManager() {
        super(GSON, "databank/dialogue/trees");
    }
    public static DialogueTreeManager getOrCreateInstance() {
        if (instance == null) {
            instance = new DialogueTreeManager();
        }
        return instance;
    }
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        trees = new HashMap<>();
        Databank.LOGGER.info("[DATABANK] Adding Databank Dialogue Trees");
        for (Map.Entry<ResourceLocation, JsonElement> i : pObject.entrySet()) {
            ResourceLocation location = i.getKey();
            if (location.getPath().startsWith("_")) {
                continue;
            }

            try {
                JsonObject obj = i.getValue().getAsJsonObject();
                DialogueTree tree = DialogueTree.CODEC.parse(JsonOps.INSTANCE, obj).getOrThrow();
                trees.put(i.getKey(), tree);
            } catch (IllegalArgumentException | JsonParseException e) {
                Databank.LOGGER.error("[DATABANK ERROR] Parsing error loading dialogue tree type {}", location, e);
            }
        }
        Databank.LOGGER.info("[DATABANK] Loaded {} Dialogue Trees", trees.size());
    }
    public static MusicSerializer serializer = new MusicSerializer();
}