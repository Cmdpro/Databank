package com.cmdpro.databank.dialogue;

import com.cmdpro.databank.dialogue.styles.DialogueStyleManager;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.ClickChoiceC2SPacket;
import com.cmdpro.databank.networking.packet.CloseDialogueC2SPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DialogueScreen extends Screen {
    public DialogueInstance instance;
    public DialogueScreen(DialogueInstance instance) {
        super(Component.empty());
        this.instance = instance;
        if (instance != null && instance.entry != null && instance.entry.style != null) {
            DialogueStyleManager.styles.get(instance.entry.style).changeEntry(instance, null, instance.entry.id);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (instance != null && instance.entry != null && instance.entry.style != null) {
            if (DialogueStyleManager.styles.get(instance.entry.style).mouseClick(instance, mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (instance != null && instance.entry != null && instance.entry.style != null) {
            if (DialogueStyleManager.styles.get(instance.entry.style).mouseDrag(instance, mouseX, mouseY, button, dragX, dragY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (instance != null && instance.entry != null && instance.entry.style != null) {
            if (DialogueStyleManager.styles.get(instance.entry.style).mouseRelease(instance, mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void changeEntry(String from, String to) {
        if (instance != null && instance.entry != null && instance.entry.style != null) {
            DialogueStyleManager.styles.get(instance.entry.style).changeEntry(instance, from, to);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (instance != null && instance.entry != null && instance.entry.style != null) {
            DialogueStyle.render(instance.entry.style, instance, guiGraphics, mouseX, mouseY);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (instance != null) {
            double last = instance.ticksOnEntry;
            instance.ticksOnEntry += instance.entry != null ? instance.entry.speed : 1;
            if (instance.entry != null && instance.entry.style != null) {
                DialogueStyleManager.styles.get(instance.entry.style).tick(instance, last, instance.ticksOnEntry);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        ModMessages.sendToServer(new CloseDialogueC2SPacket());
    }
}
