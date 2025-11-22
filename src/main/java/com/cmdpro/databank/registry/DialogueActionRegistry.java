package com.cmdpro.databank.registry;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.dialogue.DialogueChoiceAction;
import com.cmdpro.databank.dialogue.DialogueStyle;
import com.cmdpro.databank.dialogue.actions.CloseDialogueAction;
import com.cmdpro.databank.dialogue.actions.CommandDialogueAction;
import com.cmdpro.databank.dialogue.actions.SwitchEntryDialogueAction;
import com.cmdpro.databank.dialogue.styles.BasicDialogueStyle;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class DialogueActionRegistry {
    public static final DeferredRegister<DialogueChoiceAction.Codecs> DIALOGUE_ACTIONS = DeferredRegister.create(DatabankRegistries.DIALOGUE_CHOICE_ACTION_REGISTRY_KEY, Databank.MOD_ID);

    public static final Supplier<DialogueChoiceAction.Codecs> SWITCH_ENTRY = register("switch_entry", () -> SwitchEntryDialogueAction.CODECS);
    public static final Supplier<DialogueChoiceAction.Codecs> COMMAND = register("command", () -> CommandDialogueAction.CODECS);
    public static final Supplier<DialogueChoiceAction.Codecs> CLOSE = register("close", () -> CloseDialogueAction.CODECS);
    private static <T extends DialogueChoiceAction.Codecs> Supplier<T> register(final String name, final Supplier<T> item) {
        return DIALOGUE_ACTIONS.register(name, item);
    }
}
