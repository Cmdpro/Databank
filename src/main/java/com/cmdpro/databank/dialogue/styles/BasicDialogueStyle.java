package com.cmdpro.databank.dialogue.styles;

import com.cmdpro.databank.dialogue.DialogueChoice;
import com.cmdpro.databank.dialogue.DialogueInstance;
import com.cmdpro.databank.dialogue.DialogueStyle;
import com.cmdpro.databank.rendering.NineSliceSprite;
import com.cmdpro.databank.rendering.SpriteData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.*;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class BasicDialogueStyle extends DialogueStyle {
    public NineSliceSprite textBorder;
    public NineSliceSprite choiceBorder;
    public NineSliceSprite choiceHoverBorder;
    public NineSliceSprite nameBorder;
    public SpriteData portraitBorder;
    public Holder<SoundEvent> clickSound;
    public int charactersPerTick;
    public static final MapCodec<BasicDialogueStyle> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            NineSliceSprite.CODEC.fieldOf("textBorder").forGetter((obj) -> obj.textBorder),
            NineSliceSprite.CODEC.fieldOf("choiceBorder").forGetter((obj) -> obj.choiceBorder),
            NineSliceSprite.CODEC.fieldOf("choiceHoverBorder").forGetter((obj) -> obj.choiceHoverBorder),
            NineSliceSprite.CODEC.fieldOf("nameBorder").forGetter((obj) -> obj.nameBorder),
            SpriteData.CODEC.fieldOf("portraitBorder").forGetter((obj) -> obj.portraitBorder),
            SoundEvent.CODEC.optionalFieldOf("clickSound", SoundEvents.UI_BUTTON_CLICK).forGetter((obj) -> obj.clickSound),
            Codec.INT.optionalFieldOf("charactersPerTick", 1).forGetter((obj) -> obj.charactersPerTick)
    ).apply(instance, BasicDialogueStyle::new));
    public BasicDialogueStyle(NineSliceSprite textBorder, NineSliceSprite choiceBorder, NineSliceSprite choiceHoverBorder, NineSliceSprite nameBorder, SpriteData portraitBorder, Holder<SoundEvent> clickSound, int charactersPerTick) {
        this.textBorder = textBorder;
        this.choiceHoverBorder = choiceHoverBorder;
        this.choiceBorder = choiceBorder;
        this.nameBorder = nameBorder;
        this.portraitBorder = portraitBorder;
        this.clickSound = clickSound;
        this.charactersPerTick = charactersPerTick;
    }
    @Override
    public MapCodec<? extends DialogueStyle> getCodec() {
        return CODEC;
    }

    @Override
    public boolean click(DialogueInstance instance, double mouseX, double mouseY, int button) {
        for (int i = 0; i < instance.entry.choices.size(); i++) {
            if (isHovering(instance, i, mouseX, mouseY)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                runChoice(i);
            }
        }
        return false;
    }
    public boolean isHovering(DialogueInstance instance, int index, double mouseX, double mouseY) {
        DialogueChoice choice = instance.entry.choices.get(index);
        int choiceX = getChoiceX(choice, index);
        int choiceY = getChoiceY(choice, index);
        int width = 128;
        int height = 8;

        choiceX -= choiceBorder.left()-choiceBorder.defaultInset();
        choiceY -= choiceBorder.top()-choiceBorder.defaultInset();
        width += choiceBorder.getHorizontalInset();
        height += choiceBorder.getVerticalInset();

        if (mouseX >= choiceX && mouseX <= choiceX+width) {
            if (mouseY >= choiceY && mouseY <= choiceY+height) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick(DialogueInstance instance, double lastTicksPassed, double ticksPassed) {
        super.tick(instance, lastTicksPassed, ticksPassed);
        int strLength = instance.entry.text.getString().length();
        int charactersShown = (int)Math.floor((double)charactersPerTick*ticksPassed);
        int lastCharactersShown = (int)Math.floor((double)charactersPerTick*lastTicksPassed);
        if (charactersShown > strLength) charactersShown = strLength;
        if (lastCharactersShown > strLength) lastCharactersShown = strLength;
        boolean play = false;
        for (int i = lastCharactersShown+1; i <= charactersShown; i++) {
            if (instance.entry.text.getString().charAt(i-1) != ' ') {
                play = true;
            }
        }
        if (charactersShown != lastCharactersShown && play) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(instance.entry.getSpeaker().talkSound, 1));
        }
    }

    private Component cutComponent(FormattedText component, int end) {
        MutableComponent newComponent = Component.empty();
        AtomicInteger characters = new AtomicInteger(0);
        component.visit((style, string) -> {
            if (characters.get() <= end) {
                if (characters.get()+string.length() > end) {
                    int strEnd = end-characters.get();
                    newComponent.append(Component.literal(string.substring(0, strEnd)).setStyle(style));
                } else {
                    newComponent.append(Component.literal(string).setStyle(style));
                }
            }
            characters.addAndGet(string.length());
            return Optional.empty();
        }, Style.EMPTY);
        return newComponent;
    }

    @Override
    public void render(DialogueInstance instance, GuiGraphics graphics, double mouseX, double mouseY) {
        RenderSystem.enableBlend();
        NineSliceSprite textBorder = getTextBorder();
        NineSliceSprite choiceBorder = getChoiceBorder();
        NineSliceSprite nameBorder = getNameBorder();
        SpriteData portraitBorder = getPortraitBorder();

        int charactersShown = (int)Math.floor((double)charactersPerTick*instance.ticksOnEntry);
        int dialogueWidth = getDialogueBoxWidth();
        int x = getX();
        int y = getY();
        textBorder.blit(graphics, x, y, dialogueWidth, 48);

        int centerY = y+(48/2);

        Font font = getFont();

        Component text = instance.entry.text;

        List<FormattedText> lines = Minecraft.getInstance().font.getSplitter().splitLines(text, dialogueWidth-48, Style.EMPTY);
        int padding = 0;
        int lineY = centerY-(int)(lines.size()*((font.lineHeight/2f)+(float)padding));
        int characterStart = 0;
        for (FormattedText i : lines) {
            if (characterStart > charactersShown) {
                break;
            }
            int end = charactersShown-characterStart;
            Component component = cutComponent(i, end);
            FormattedCharSequence formattedCharSequence = FormattedBidiReorder.reorder(component, Language.getInstance().isDefaultRightToLeft());
            graphics.drawString(font, formattedCharSequence, x+48, lineY, 0xFFFFFFFF);
            characterStart += i.getString().length();
            lineY += font.lineHeight+padding;
        }

        int portraitX = x+8;
        int portraitY = y+8;
        graphics.blit(portraitBorder.texture(), portraitX-4, portraitY-4, portraitBorder.u(), portraitBorder.v(), 40, 40);
        graphics.blit(instance.entry.getSpeaker().portrait, portraitX, portraitY, 0, 0, 0, 32, 32, 32, 32);

        int nameWidth = Math.clamp(font.width(instance.entry.getSpeaker().name), 64, Integer.MAX_VALUE);
        nameBorder.blit(graphics, (portraitX+16)-(nameWidth/2), y-17, nameWidth, 8);
        graphics.drawCenteredString(font, instance.entry.getSpeaker().name, portraitX+16, y-17, 0xFFFFFFFF);

        for (int i = 0; i < instance.entry.choices.size(); i++) {
            DialogueChoice choice = instance.entry.choices.get(i);
            int choiceX = getChoiceX(choice, i);
            int choiceY = getChoiceY(choice, i);
            (isHovering(instance, i, mouseX, mouseY) ? choiceHoverBorder : choiceBorder).blit(graphics, choiceX, choiceY, 128, 8);
            graphics.drawCenteredString(font, choice.text, choiceX+64, choiceY, 0xFFFFFFFF);
        }
        graphics.flush();
        RenderSystem.disableBlend();
    }
    public int getDialogueBoxWidth() {
        return 64*5;
    }
    public int getChoiceX(DialogueChoice choice, int index) {
        return (getX()+getDialogueBoxWidth())-(128+8);
    }
    public int getChoiceY(DialogueChoice choice, int index) {
        return getY()-(18*(index+1));
    }
    public int getX() {
        return (getGuiWidth()/2)-(getDialogueBoxWidth()/2);
    }
    public int getY() {
        return getGuiHeight()-58;
    }
    public int getGuiWidth() {
        return Minecraft.getInstance().getWindow().getGuiScaledWidth();
    }
    public int getGuiHeight() {
        return Minecraft.getInstance().getWindow().getGuiScaledHeight();
    }

    public NineSliceSprite getTextBorder() {
        return textBorder;
    }
    public NineSliceSprite getChoiceBorder() {
        return choiceBorder;
    }
    public NineSliceSprite getChoiceHoverBorder() {
        return choiceHoverBorder;
    }
    public NineSliceSprite getNameBorder() {
        return nameBorder;
    }
    public SpriteData getPortraitBorder() {
        return portraitBorder;
    }
    public Font getFont() {
        return Minecraft.getInstance().font;
    }
}
