package com.cmdpro.databank.dev.block;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.dev.registry.BlockEntityRegistry;
import com.cmdpro.databank.megastructures.Megastructure;
import com.cmdpro.databank.model.animation.DatabankAnimationReference;
import com.cmdpro.databank.model.animation.DatabankAnimationState;
import com.cmdpro.databank.registry.AttachmentTypeRegistry;
import com.cmdpro.databank.registry.BlockRegistry;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.FileUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

@EventBusSubscriber(modid = Databank.MOD_ID)
public class ModelTestBlockEntity extends BlockEntity {
    public ModelTestBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.MODEL_TEST.get(), pos, blockState);
    }
    public DatabankAnimationState animState = new DatabankAnimationState("animation")
            .addAnim(new DatabankAnimationReference("animation", (state, anim) -> {}, (state, anim) -> {}))
            .addAnim(new DatabankAnimationReference("animation2", (state, anim) -> {}, (state, anim) -> {}));

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
        animState.setLevel(level);
    }
}
