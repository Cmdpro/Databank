package com.cmdpro.databank.multiblock;

import com.cmdpro.databank.multiblock.predicates.AnyMultiblockPredicate;
import com.cmdpro.databank.multiblock.predicates.BlockstateMultiblockPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Multiblock implements BlockAndTintGetter {
    public String[][] multiblockLayers;
    public Map<Character, MultiblockPredicate> key;
    public BlockPos center;
    private List<List<List<PredicateAndPos>>> states;
    public Multiblock(String[][] multiblockLayers, Map<Character, MultiblockPredicate> key, BlockPos center) {
        this.multiblockLayers = multiblockLayers;
        key.put(' ', new BlockstateMultiblockPredicate(Blocks.AIR.defaultBlockState()));
        key.put('*', new AnyMultiblockPredicate());
        this.key = key;
        this.center = center;
    }
    public List<List<List<PredicateAndPos>>> getStates() {
        return getStates(false);
    }
    public List<List<List<PredicateAndPos>>> getStates(boolean forceCacheReset) {
        if (forceCacheReset || this.states == null) {
            int x = 0;
            int y = 0;
            int z = 0;
            List<List<List<PredicateAndPos>>> states = new ArrayList<>();
            for (String[] i : multiblockLayers) {
                z = 0;
                List<List<PredicateAndPos>> states2 = new ArrayList<>();
                for (String o : i) {
                    List<PredicateAndPos> layer = new ArrayList<>();
                    x = 0;
                    for (char p : o.toCharArray()) {
                        layer.add(new PredicateAndPos(key.get(p), new BlockPos(x, y, z).offset(center.getX(), center.getY(), center.getZ())));
                        x++;
                    }
                    states2.add(layer);
                    z++;
                }
                states.add(states2);
                y++;
            }
            this.states = states;
            return states;
        } else {
            return this.states;
        }
    }
    public Rotation getMultiblockRotation(Level level, BlockPos pos) {
        if (checkMultiblock(level, pos, Rotation.NONE)) {
            return Rotation.NONE;
        }
        if (checkMultiblock(level, pos, Rotation.COUNTERCLOCKWISE_90)) {
            return Rotation.COUNTERCLOCKWISE_90;
        }
        if (checkMultiblock(level, pos, Rotation.CLOCKWISE_180)) {
            return Rotation.CLOCKWISE_180;
        }
        if (checkMultiblock(level, pos, Rotation.CLOCKWISE_90)) {
            return Rotation.CLOCKWISE_90;
        }
        return null;
    }
    public boolean checkMultiblock(Level level, BlockPos pos) {
        return checkMultiblock(level, pos, Rotation.NONE);
    }
    public boolean checkMultiblockAll(Level level, BlockPos pos) {
        return checkMultiblock(level, pos, Rotation.NONE) || checkMultiblock(level, pos, Rotation.CLOCKWISE_90) || checkMultiblock(level, pos, Rotation.CLOCKWISE_180) || checkMultiblock(level, pos, Rotation.COUNTERCLOCKWISE_90);
    }
    public boolean checkMultiblock(Level level, BlockPos pos, Rotation rotation) {
        for (List<List<PredicateAndPos>> i : getStates()) {
            for (List<PredicateAndPos> j : i) {
                for (PredicateAndPos k : j) {
                    if (k.predicate == null) {
                        continue;
                    }
                    BlockPos blockPos = k.offset.rotate(rotation).offset(pos.getX(), pos.getY(), pos.getZ());
                    BlockState state = level.getBlockState(blockPos);
                    if (!k.predicate.isSame(state, rotation)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public List<List<String>> getMultiblockLayersList() {
        return Arrays.stream(multiblockLayers).map((a) -> Arrays.stream(a).toList()).toList();
    }
    public static class PredicateAndPos {
        public PredicateAndPos(MultiblockPredicate predicate, BlockPos offset) {
            this.predicate = predicate;
            this.offset = offset;
        }
        public MultiblockPredicate predicate;
        public BlockPos offset;
    }


    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        BlockState state = this.getBlockState(pos);
        if (state.getBlock() instanceof EntityBlock eb) {
            return MultiblockRenderer.blockEntityCache.computeIfAbsent(pos.immutable(), p -> eb.newBlockEntity(p, state));
        }
        return null;
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (pos.getY() >= 0 && pos.getY() < states.size() && pos.getZ() >= 0 && pos.getZ() < states.get(pos.getY()).size() && pos.getX() >= 0 && pos.getX() < states.get(pos.getY()).get(pos.getZ()).size()) {
            return states.get(pos.getY()).get(pos.getZ()).get(pos.getX()).predicate.getVisual();
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public float getShade(Direction direction, boolean shaded) {
        return 1.0F;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver color) {
        var plains = Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BIOME)
                .getOrThrow(Biomes.PLAINS);
        return color.getColor(plains, pos.getX(), pos.getZ());
    }

    @Override
    public int getBrightness(LightLayer type, BlockPos pos) {
        return 15;
    }

    @Override
    public int getRawBrightness(BlockPos pos, int ambientDarkening) {
        return 15 - ambientDarkening;
    }

    @Override
    public int getHeight() {
        return Minecraft.getInstance().level.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return Minecraft.getInstance().level.getMinBuildHeight();
    }
}
