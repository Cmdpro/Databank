package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.dialogue.DialogueStyle;
import com.cmdpro.databank.dialogue.styles.BasicDialogueStyle;
import com.cmdpro.databank.model.DatabankPartData;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DialogueStyleRegistry {
    public static final DeferredRegister<MapCodec<? extends DialogueStyle>> DIALOGUE_STYLES = DeferredRegister.create(DatabankRegistries.DIALOGUE_STYLE_REGISTRY_KEY, Databank.MOD_ID);

    public static final Supplier<MapCodec<? extends DialogueStyle>> BASIC = register("basic", () -> BasicDialogueStyle.CODEC);
    private static <T extends MapCodec<? extends DialogueStyle>> Supplier<T> register(final String name, final Supplier<T> item) {
        return DIALOGUE_STYLES.register(name, item);
    }
}
