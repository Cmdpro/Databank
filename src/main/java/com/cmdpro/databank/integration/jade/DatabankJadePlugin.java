package com.cmdpro.databank.integration.jade;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.DatabankUtils;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class DatabankJadePlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.addRayTraceCallback((hitResult, accessor, accessor1) -> {
            if (accessor instanceof BlockAccessor accessor2) {
                Block block = ClientDatabankUtils.getHiddenBlock(accessor2.getBlock());
                if (block != null) {
                    return registration.blockAccessor().from(accessor2).blockState(DatabankUtils.changeBlockType(accessor2.getBlockState(), block)).build();
                }
            }
            return accessor;
        });
    }
}
