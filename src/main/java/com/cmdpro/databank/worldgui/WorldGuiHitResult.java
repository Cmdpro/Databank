package com.cmdpro.databank.worldgui;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class WorldGuiHitResult extends EntityHitResult {
    public final WorldGuiEntity.WorldGuiIntersectionResult result;
    public WorldGuiHitResult(WorldGuiEntity entity, WorldGuiEntity.WorldGuiIntersectionResult result) {
        super(entity);
        this.result = result;
    }

    public WorldGuiHitResult(WorldGuiEntity entity, Vec3 location, WorldGuiEntity.WorldGuiIntersectionResult result) {
        super(entity, location);
        this.result = result;
    }
}
