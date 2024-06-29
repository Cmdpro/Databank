package com.cmdpro.cmdlib.shaders;

import com.cmdpro.cmdlib.CmdLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CmdLib.MOD_ID)
public class PostShaderManager {
    public static List<PostShaderInstance> instances = new ArrayList<>();
    public static void addShader(PostShaderInstance instance) {
        instances.add(instance);
    }
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        for (PostShaderInstance i : PostShaderManager.instances) {
            i.tick();
        }
    }
}
