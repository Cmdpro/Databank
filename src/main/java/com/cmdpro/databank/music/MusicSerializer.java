package com.cmdpro.databank.music;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.hiddenblock.HiddenBlock;
import com.cmdpro.databank.hiddenblock.HiddenBlockConditions;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class MusicSerializer {
    public MusicController read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("condition")) {
            throw new JsonSyntaxException("Element condition missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("conditionData")) {
            throw new JsonSyntaxException("Element conditionData missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("music")) {
            throw new JsonSyntaxException("Element hiddenAs missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("priority")) {
            throw new JsonSyntaxException("Element originalBlock missing in entry JSON for " + entryId.toString());
        }
        ResourceLocation condition = ResourceLocation.tryParse(json.get("condition").getAsString());
        JsonObject conditionData = json.get("conditionData").getAsJsonObject();
        SoundEvent music = SoundEvent.createVariableRangeEvent(ResourceLocation.parse(json.get("music").getAsString()));
        int priority = json.get("priority").getAsInt();
        return new MusicController(MusicConditions.conditions.get(condition).deserialize(conditionData), music, priority);
    }
}