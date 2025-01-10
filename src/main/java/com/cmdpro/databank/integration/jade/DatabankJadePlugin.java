package com.cmdpro.databank.integration.jade;

import com.cmdpro.databank.ClientDatabankUtils;
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
                BlockState state = ClientDatabankUtils.getHiddenBlock(accessor2.getBlock());
                if (state != null) {
                    return registration.blockAccessor().from(accessor2).blockState(state).build();
                }
            }
            return accessor;
        });
    }
}
