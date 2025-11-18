package com.cmdpro.databank.dev.registry;

import com.cmdpro.databank.dev.DatabankDev;
import com.cmdpro.databank.dev.block.ModelTestBlockEntity;
import com.cmdpro.databank.dev.client.renderer.block.ModelTestRenderer;
import com.cmdpro.databank.megastructures.block.MegastructureSaveBlockEntity;
import com.cmdpro.databank.megastructures.block.renderers.MegastructureSaveRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = DatabankDev.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DatabankDev.MOD_ID);

    public static final Supplier<BlockEntityType<ModelTestBlockEntity>> MODEL_TEST =
            register("model_test", () ->
                    BlockEntityType.Builder.of(ModelTestBlockEntity::new,
                            BlockRegistry.MODEL_TEST.get()).build(null));

    private static <T extends BlockEntityType<?>> Supplier<T> register(final String name, final Supplier<T> blockentity) {
        return BLOCK_ENTITIES.register(name, blockentity);
    }
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MODEL_TEST.get(), ModelTestRenderer::new);
    }
}
