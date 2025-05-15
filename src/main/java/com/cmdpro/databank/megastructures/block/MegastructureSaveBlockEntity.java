package com.cmdpro.databank.megastructures.block;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.megastructures.Megastructure;
import com.cmdpro.databank.registry.AttachmentTypeRegistry;
import com.cmdpro.databank.registry.BlockEntityRegistry;
import com.cmdpro.databank.registry.BlockRegistry;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.RecordBuilder;
import net.minecraft.FileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(modid = Databank.MOD_ID)
public class MegastructureSaveBlockEntity extends BlockEntity {
    public MegastructureSaveBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.MEGASTRUCTURE_SAVE.get(), pos, blockState);
    }
    public void setBindProcess(int process) {
        changeProgressTo = process;
    }
    public boolean isChangingBind() {
        return bindProcess != changeProgressTo;
    }
    public int getBindProcess() {
        return bindProcess;
    }
    int changeProgressTo;
    private int bindProcess;
    public BlockPos corner1;
    public BlockPos corner2;
    public BlockPos center;
    private UUID uuid;
    public UUID getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
        return uuid;
    }
    public void tick(Level level, BlockPos pos, BlockState state) {
        if (changeProgressTo != bindProcess) {
            bindProcess = changeProgressTo;
        }
    }
    protected void updateBlock() {
        BlockState blockState = level.getBlockState(this.getBlockPos());
        this.level.sendBlockUpdated(this.getBlockPos(), blockState, blockState, 3);
        this.setChanged();
    }
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket(){
        return ClientboundBlockEntityDataPacket.create(this);
    }
    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider pRegistries){
        CompoundTag tag = pkt.getTag();
        if (tag.contains("corner1X") && tag.contains("corner1Y") && tag.contains("corner1Z")) {
            corner1 = new BlockPos(tag.getInt("corner1X"), tag.getInt("corner1Y"), tag.getInt("corner1Z"));
        }
        if (tag.contains("corner2X") && tag.contains("corner2Y") && tag.contains("corner2Z")) {
            corner2 = new BlockPos(tag.getInt("corner2X"), tag.getInt("corner2Y"), tag.getInt("corner2Z"));
        }
        if (tag.contains("centerX") && tag.contains("centerY") && tag.contains("centerZ")) {
            center = new BlockPos(tag.getInt("centerX"), tag.getInt("centerY"), tag.getInt("centerZ"));
        }
    }
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries) {
        CompoundTag tag = new CompoundTag();
        if (corner1 != null) {
            tag.putInt("corner1X", corner1.getX());
            tag.putInt("corner1Y", corner1.getY());
            tag.putInt("corner1Z", corner1.getZ());
        }
        if (corner2 != null) {
            tag.putInt("corner2X", corner2.getX());
            tag.putInt("corner2Y", corner2.getY());
            tag.putInt("corner2Z", corner2.getZ());
        }
        if (center != null) {
            tag.putInt("centerX", center.getX());
            tag.putInt("centerY", center.getY());
            tag.putInt("centerZ", center.getZ());
        }
        return tag;
    }
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                if (getBindProcess() == 0) {
                    player.sendSystemMessage(Component.translatable("block.databank.megastructure_save.corner1"));
                    player.setData(AttachmentTypeRegistry.BINDING_BLOCK, Optional.of(this));
                    setBindProcess(1);
                } else {
                    corner1 = null;
                    corner2 = null;
                    center = null;
                    player.setData(AttachmentTypeRegistry.BINDING_BLOCK, Optional.empty());
                    player.sendSystemMessage(Component.translatable("block.databank.megastructure_save.reset"));
                    updateBlock();
                    setBindProcess(0);
                }
            } else {
                if (corner1 != null && corner2 != null && center != null) {
                    save();
                    player.sendSystemMessage(Component.translatable("block.databank.megastructure_save.saved", uuid.toString()));
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
    public void save() {
        Megastructure megastructure = Megastructure.createFromWorld(level, corner1, corner2, center);
        try {
            Path path = ((ServerLevel) level).getServer().getWorldPath(LevelResource.GENERATED_DIR).normalize();
            path = path.resolve(Databank.MOD_ID).resolve("megastructures");
            path = FileUtil.createPathToResource(path, getUuid().toString(), ".json");
            JsonElement json = Megastructure.CODEC.encode(megastructure, JsonOps.INSTANCE, JsonOps.INSTANCE.mapBuilder()).build(JsonOps.INSTANCE.empty()).result().orElse(null);
            if (json != null) {
                Path parentPath = path.getParent();
                Files.createDirectories(Files.exists(parentPath) ? parentPath.toRealPath() : parentPath);
                Files.deleteIfExists(path);
                Files.writeString(path, json.toString());
            }
        } catch (Exception e) {
            Databank.LOGGER.trace(e.getMessage(), e.fillInStackTrace());
        }
    }
    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide()) {
            if (!event.getLevel().getBlockState(event.getPos()).is(BlockRegistry.MEGASTRUCTURE_SAVE.get())) {
                event.getEntity().getData(AttachmentTypeRegistry.BINDING_BLOCK).ifPresent((binding) -> {
                    if (binding instanceof MegastructureSaveBlockEntity ent) {
                        if (!ent.isChangingBind()) {
                            if (ent.getBindProcess() == 1) {
                                ent.corner1 = event.getPos();
                                event.getEntity().sendSystemMessage(Component.translatable("block.databank.megastructure_save.corner2"));
                                ent.updateBlock();
                                ent.setBindProcess(2);
                            } else if (ent.getBindProcess() == 2) {
                                ent.corner2 = event.getPos();
                                event.getEntity().sendSystemMessage(Component.translatable("block.databank.megastructure_save.center", ent.corner1.toShortString(), ent.corner2.toShortString()));
                                ent.updateBlock();
                                ent.setBindProcess(3);
                            } else if (ent.getBindProcess() == 3) {
                                ent.center = event.getPos();
                                event.getEntity().setData(AttachmentTypeRegistry.BINDING_BLOCK, Optional.empty());
                                event.getEntity().sendSystemMessage(Component.translatable("block.databank.megastructure_save.finished", ent.corner1.toShortString(), ent.corner2.toShortString()));
                                ent.updateBlock();
                                ent.setBindProcess(0);
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putUUID("uuid", getUuid());
        if (corner1 != null) {
            tag.putInt("corner1X", corner1.getX());
            tag.putInt("corner1Y", corner1.getY());
            tag.putInt("corner1Z", corner1.getZ());
        }
        if (corner2 != null) {
            tag.putInt("corner2X", corner2.getX());
            tag.putInt("corner2Y", corner2.getY());
            tag.putInt("corner2Z", corner2.getZ());
        }
        if (center != null) {
            tag.putInt("centerX", center.getX());
            tag.putInt("centerY", center.getY());
            tag.putInt("centerZ", center.getZ());
        }
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("uuid")) {
            uuid = tag.getUUID("uuid");
        }
        if (tag.contains("corner1X") && tag.contains("corner1Y") && tag.contains("corner1Z")) {
            corner1 = new BlockPos(tag.getInt("corner1X"), tag.getInt("corner1Y"), tag.getInt("corner1Z"));
        }
        if (tag.contains("corner2X") && tag.contains("corner2Y") && tag.contains("corner2Z")) {
            corner2 = new BlockPos(tag.getInt("corner2X"), tag.getInt("corner2Y"), tag.getInt("corner2Z"));
        }
        if (tag.contains("centerX") && tag.contains("centerY") && tag.contains("centerZ")) {
            center = new BlockPos(tag.getInt("centerX"), tag.getInt("centerY"), tag.getInt("centerZ"));
        }
    }
}
