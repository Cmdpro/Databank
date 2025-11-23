package com.cmdpro.databank.dialogue;

import com.cmdpro.databank.DatabankRegistries;
import com.cmdpro.databank.dialogue.styles.DialogueStyleManager;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.ClickChoiceC2SPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public abstract class DialogueStyle {
    public static final Codec<DialogueStyle> CODEC = DatabankRegistries.DIALOGUE_STYLE_REGISTRY.byNameCodec().dispatch(DialogueStyle::getCodec, Function.identity());
    public abstract void render(DialogueInstance instance, GuiGraphics graphics, double mouseX, double mouseY);
    public abstract MapCodec<? extends DialogueStyle> getCodec();
    public static void render(ResourceLocation style, DialogueInstance instance, GuiGraphics graphics, double mouseX, double mouseY) {
        DialogueStyleManager.styles.get(style).render(instance, graphics, mouseX, mouseY);
    }
    public abstract boolean mouseClick(DialogueInstance instance, double mouseX, double mouseY, int button);
    public boolean mouseDrag(DialogueInstance instance, double mouseX, double mouseY, int button, double dragX, double dragY) { return false; }
    public boolean mouseRelease(DialogueInstance instance, double mouseX, double mouseY, int button) { return false; }
    public void changeEntry(DialogueInstance instance, String from, String to) {}
    public void runChoice(int index) {
        ModMessages.sendToServer(new ClickChoiceC2SPacket(index));
    }
    public void tick(DialogueInstance instance, double lastTicksPassed, double ticksPassed) {}
}
