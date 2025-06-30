package com.cmdpro.databank.integration.jade;

import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.hidden.types.BlockHiddenType;
import com.cmdpro.databank.registry.EntityRegistry;
import com.cmdpro.databank.worldgui.WorldGui;
import com.cmdpro.databank.worldgui.WorldGuiEntity;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.*;

@WailaPlugin
public class DatabankJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addRayTraceCallback((hitResult, accessor, accessor1) -> {
            if (accessor instanceof BlockAccessor accessor2) {
                Block block = BlockHiddenType.getHiddenBlockClient(accessor2.getBlock());
                if (block != null) {
                    return registration.blockAccessor().from(accessor2).blockState(DatabankUtils.changeBlockType(accessor2.getBlockState(), block)).build();
                }
            }
            return accessor;
        });
        registration.addRayTraceCallback((hitResult, accessor, accessor1) -> {
            if (accessor instanceof EntityAccessor accessor2) {
                if (accessor2.getEntity() instanceof WorldGuiEntity) {
                    return null;
                }
            }
            return accessor;
        });
    }
}
