package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.megastructures.block.MegastructureSaveBlockEntity;
import com.cmdpro.databank.megastructures.block.renderers.MegastructureSaveRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, modid = Databank.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Databank.MOD_ID);

    public static final Supplier<BlockEntityType<MegastructureSaveBlockEntity>> MEGASTRUCTURE_SAVE =
            register("megastructure_save", () ->
                    BlockEntityType.Builder.of(MegastructureSaveBlockEntity::new,
                            BlockRegistry.MEGASTRUCTURE_SAVE.get()).build(null));

    private static <T extends BlockEntityType<?>> Supplier<T> register(final String name, final Supplier<T> blockentity) {
        return BLOCK_ENTITIES.register(name, blockentity);
    }
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MEGASTRUCTURE_SAVE.get(), MegastructureSaveRenderer::new);
    }
}
