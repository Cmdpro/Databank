package com.cmdpro.databank.hiddenblocks;

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
        BlockState hiddenAs = null;
        Block originalBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(json.get("originalBlock").getAsString()));
        try {
            hiddenAs = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), json.get("hiddenAs").getAsString(), false).blockState();
        } catch (Exception e) {
            Databank.LOGGER.error(e.getMessage());
        }
        return new HiddenBlock(HiddenBlockConditions.conditions.get(condition).deserialize(conditionData), originalBlock, hiddenAs);
    }
    @Nonnull
    public static HiddenBlock fromNetwork(FriendlyByteBuf buf) {
        BlockState hiddenAs = null;
        String originalBlockString = buf.readUtf();
        String hiddenAsString = buf.readUtf();
        Block originalBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.tryParse(originalBlockString));
        try {
            hiddenAs = BlockStateParser.parseForBlock(BuiltInRegistries.BLOCK.asLookup(), hiddenAsString, false).blockState();
        } catch (Exception e) {
            Databank.LOGGER.error(e.getMessage());
        }
        return new HiddenBlock(null, originalBlock, hiddenAs);
    }
    public static void toNetwork(FriendlyByteBuf buf, HiddenBlock block) {
        buf.writeResourceLocation(BuiltInRegistries.BLOCK.getKey(block.originalBlock));
        buf.writeUtf(BlockStateParser.serialize(block.hiddenAs));
    }
}