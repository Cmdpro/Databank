package com.cmdpro.databank.instanceddimension;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;

public class LevelLinkedDelegateBorderChangeListener extends BorderChangeListener.DelegateBorderChangeListener {
    public ServerLevel level;
    public LevelLinkedDelegateBorderChangeListener(ServerLevel level) {
        super(level.getWorldBorder());
        this.level = level;
    }
}
