package com.cmdpro.databank.rendering;

import com.cmdpro.databank.Databank;
import com.mojang.blaze3d.pipeline.RenderTarget;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class RenderTargetPool {
    private static List<RenderTargetPool> pools = new ArrayList<>();
    private HashMap<RenderTarget, PooledRenderTarget> pool = new HashMap<>();
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        for (RenderTargetPool i : pools) {
            i.tick();
        }
    }
    public final int unusedFreeTime;
    public RenderTargetPool(int unusedFreeTime) {
        this.unusedFreeTime = unusedFreeTime;
    }
    public RenderTargetPool() {
        this(20*60);
    }
    public void tick() {
        for (PooledRenderTarget i : pool.values()) {
            if (i.uses.isEmpty()) {
                i.time++;
            } else {
                i.time = 0;
            }
            if (canFree(i.target, i.time, i.maxTime, !i.uses.isEmpty())) {
                freeTarget(i);
            }
        }
    }
    public boolean canFree(RenderTarget target, int time, int maxTime, boolean inUse) {
        return time >= maxTime && !inUse;
    }
    public RenderTarget getTarget(Predicate<RenderTarget> predicate, Supplier<RenderTarget> ifCreate) {
        RenderTarget target = pool.values().stream().map((pooledTarget) -> pooledTarget.target).filter(predicate).findAny().orElse(null);
        if (target != null) {
            return target;
        }
        target = ifCreate.get();
        pool.put(target, new PooledRenderTarget(0, unusedFreeTime, target));
        return target;
    }
    public void freeTarget(RenderTarget target) {
        PooledRenderTarget pooledRenderTarget = pool.get(target);
        if (pooledRenderTarget != null) {
            freeTarget(pooledRenderTarget);
        }
    }
    private void freeTarget(PooledRenderTarget target) {
        target.target.destroyBuffers();
        pool.remove(target.target);
    }
    public void free() {
        for (PooledRenderTarget i : pool.values()) {
            i.target.destroyBuffers();
        }
        pools.remove(this);
    }
    public void markUse(RenderTarget target, ResourceLocation id) {
        PooledRenderTarget pooledRenderTarget = pool.get(target);
        if (pooledRenderTarget != null) {
            pooledRenderTarget.time = 0;
            if (!pooledRenderTarget.uses.contains(id)) {
                pooledRenderTarget.uses.add(id);
            }
        }
    }
    public void unmarkUse(RenderTarget target, ResourceLocation id) {
        PooledRenderTarget pooledRenderTarget = pool.get(target);
        if (pooledRenderTarget != null) {
            pooledRenderTarget.uses.remove(id);
        }
    }
    public List<ResourceLocation> getUses(RenderTarget target) {
        PooledRenderTarget pooledRenderTarget = pool.get(target);
        if (pooledRenderTarget != null) {
            return pooledRenderTarget.uses;
        }
        return null;
    }
    public boolean isInUse(RenderTarget target) {
        PooledRenderTarget pooledRenderTarget = pool.get(target);
        if (pooledRenderTarget != null) {
            return !pooledRenderTarget.uses.isEmpty();
        }
        return false;
    }
    public ResourceLocation generateRandomUseId(String modId) {
        return generateRandomUseId(modId, "");
    }
    public ResourceLocation generateRandomUseId(String modId, String prefix) {
        return ResourceLocation.fromNamespaceAndPath(modId, prefix + "-" + UUID.randomUUID());
    }
    private static class PooledRenderTarget {
        public int time;
        public int maxTime;
        public RenderTarget target;
        public List<ResourceLocation> uses;
        public PooledRenderTarget(int time, int maxTime, RenderTarget target) {
            this.time = time;
            this.maxTime = maxTime;
            this.target = target;
            this.uses = new ArrayList<>();
        }
    }
}
