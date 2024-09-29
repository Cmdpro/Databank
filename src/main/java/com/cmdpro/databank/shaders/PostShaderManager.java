package com.cmdpro.databank.shaders;

import com.cmdpro.databank.Databank;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID)
public class PostShaderManager {
    public static Matrix4f viewStackMatrix;
    public static List<PostShaderInstance> instances = new ArrayList<>();
    public static void addShader(PostShaderInstance instance) {
        instances.add(instance);
    }
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        for (PostShaderInstance i : PostShaderManager.instances) {
            i.tick();
        }
    }
}
