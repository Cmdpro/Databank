package com.cmdpro.databank.megastructures;

import com.cmdpro.databank.multiblock.Multiblock;
import com.cmdpro.databank.registry.BlockEntityRegistry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.conditions.ConditionalOps;
import net.neoforged.neoforge.common.conditions.WithConditions;

import java.util.*;

public class Megastructure {
    public static final MapCodec<Megastructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            MegastructureBlock.CODEC.codec().listOf().fieldOf("key").forGetter((megastructure) -> megastructure.key),
            Vec3i.CODEC.fieldOf("center").forGetter((megastructure) -> megastructure.center),
            Codec.INT.listOf().listOf().listOf().fieldOf("shape").forGetter((megastructure) -> megastructure.shape)
        ).apply(instance, Megastructure::new)
    );
    public static final Codec<Optional<WithConditions<Megastructure>>> CONDITION_CODEC = ConditionalOps.createConditionalCodecWithConditions(CODEC.codec());
    public List<MegastructureBlock> key;
    public Vec3i center;
    public List<List<List<Integer>>> shape;
    public Megastructure(List<MegastructureBlock> key, Vec3i center, List<List<List<Integer>>> shape) {
        this.key = key;
        this.center = center;
        this.shape = shape;
    }
    public static Megastructure createFromWorld(Level level, BlockPos corner1, BlockPos corner2, BlockPos center) {
        BlockPos min = new BlockPos(
                Math.min(corner1.getX(), corner2.getX()),
                Math.min(corner1.getY(), corner2.getY()),
                Math.min(corner1.getZ(), corner2.getZ())
        );
        BlockPos max = new BlockPos(
                Math.max(corner1.getX(), corner2.getX()),
                Math.max(corner1.getY(), corner2.getY()),
                Math.max(corner1.getZ(), corner2.getZ())
        );
        Vec3i size = max.subtract(min);
        List<List<List<Integer>>> shape = new ArrayList<>();
        for (int x = 0; x <= size.getX(); x++) {
            List<List<Integer>> yBlocks = new ArrayList<>();
            for (int y = 0; y <= size.getY(); y++) {
                List<Integer> zBlocks = new ArrayList<>();
                for (int z = 0; z <= size.getZ(); z++) {
                    zBlocks.add(0);
                }
                yBlocks.add(zBlocks);
            }
            shape.add(yBlocks);
        }
        List<MegastructureBlock> key = new ArrayList<>();
        key.add(new MegastructureBlock(Blocks.AIR.defaultBlockState(), Optional.empty()));
        for (BlockPos i : BlockPos.betweenClosed(min, max)) {
            BlockPos blockPos = i.subtract(min);
            BlockState blockState = level.getBlockState(i);
            Optional<CompoundTag> nbt = Optional.empty();
            if (level.getBlockEntity(i) instanceof BlockEntity ent) {
                nbt = Optional.of(ent.saveCustomOnly(level.registryAccess()));
            }
            MegastructureBlock block = new MegastructureBlock(blockState, nbt);
            int keyIndex = key.size();
            if (!key.contains(block)) {
                key.add(block);
            } else {
                keyIndex = key.indexOf(block);
            }
            shape.get(blockPos.getX()).get(blockPos.getY()).set(blockPos.getZ(), keyIndex);
        }
        BlockPos relativeCenter = center.subtract(min);
        return new Megastructure(key, relativeCenter, shape);
    }
    public void placeIntoWorld(Level level, BlockPos pos) {
        BlockPos startPos = pos.offset(center.multiply(-1));
        BlockPos blockPos = startPos;
        for (List<List<Integer>> x : shape) {
            for (List<Integer> y : x) {
                for (Integer z : y) {
                    MegastructureBlock block = key.get(z);
                    if (!block.state.is(Blocks.STRUCTURE_VOID)) {
                        level.setBlockAndUpdate(blockPos, block.state);
                        if (block.nbt.isPresent()) {
                            if (level.getBlockEntity(blockPos) instanceof BlockEntity ent) {
                                ent.loadCustomOnly(block.nbt.get(), level.registryAccess());
                            }
                        }
                    }
                    blockPos = blockPos.offset(0, 0, 1);
                }
                blockPos = new BlockPos(blockPos.getX(), blockPos.getY(), startPos.getZ());
                blockPos = blockPos.offset(0, 1, 0);
            }
            blockPos = new BlockPos(blockPos.getX(), startPos.getY(), blockPos.getZ());
            blockPos = blockPos.offset(1, 0, 0);
        }
    }
    public static class MegastructureBlock {
        public static final MapCodec<MegastructureBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BlockState.CODEC.fieldOf("state").forGetter((block) -> block.state),
                CompoundTag.CODEC.optionalFieldOf("nbt").forGetter((block) -> block.nbt)
            ).apply(instance, MegastructureBlock::new)
        );
        public BlockState state;
        public Optional<CompoundTag> nbt;
        public MegastructureBlock(BlockState state, Optional<CompoundTag> nbt) {
            this.state = state;
            this.nbt = nbt;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof MegastructureBlock that)) return false;
            return Objects.equals(state, that.state) && Objects.equals(nbt, that.nbt);
        }

        @Override
        public int hashCode() {
            return Objects.hash(state, nbt);
        }
    }
}
