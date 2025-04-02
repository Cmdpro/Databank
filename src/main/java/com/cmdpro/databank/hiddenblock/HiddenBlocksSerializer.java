package com.cmdpro.databank.hiddenblock;

import com.cmdpro.databank.Databank;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class HiddenBlocksSerializer {
    public HiddenBlock read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("condition")) {
            throw new JsonSyntaxException("Element condition missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("conditionData")) {
            throw new JsonSyntaxException("Element conditionData missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("hiddenAs")) {
            throw new JsonSyntaxException("Element hiddenAs missing in entry JSON for " + entryId.toString());
        }
        if (!json.has("originalBlock")) {
            throw new JsonSyntaxException("Element originalBlock missing in entry JSON for " + entryId.toString());
        }
        ResourceLocation condition = ResourceLocation.tryParse(json.get("condition").getAsString());
        JsonObject conditionData = json.get("conditionData").getAsJsonObject();
        Block originalBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(json.get("originalBlock").getAsString()));
        Block hiddenAs = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(json.get("hiddenAs").getAsString()));
        return new HiddenBlock(HiddenBlockConditions.conditions.get(condition).deserialize(conditionData), originalBlock, hiddenAs);
    }
    @Nonnull
    public static HiddenBlock fromNetwork(FriendlyByteBuf buf) {
        String originalBlockString = buf.readUtf();
        String hiddenAsString = buf.readUtf();
        Block originalBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(originalBlockString));
        Block hiddenAs = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(hiddenAsString));
        return new HiddenBlock(null, originalBlock, hiddenAs);
    }
    public static void toNetwork(FriendlyByteBuf buf, HiddenBlock block) {
        buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(block.originalBlock));
        buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(block.hiddenAs));
    }
}