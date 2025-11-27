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
    public NineSliceSprite scrollBorder;
    public SpriteData scrollPoint;
    public SpriteData scrollPointHovered;
    public Holder<SoundEvent> clickSound;
    public int charactersPerTick;
    public static final MapCodec<BasicDialogueStyle> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            NineSliceSprite.CODEC.fieldOf("textBorder").forGetter((obj) -> obj.textBorder),
            NineSliceSprite.CODEC.fieldOf("choiceBorder").forGetter((obj) -> obj.choiceBorder),
            NineSliceSprite.CODEC.fieldOf("choiceHoverBorder").forGetter((obj) -> obj.choiceHoverBorder),
            NineSliceSprite.CODEC.fieldOf("nameBorder").forGetter((obj) -> obj.nameBorder),
            SpriteData.CODEC.fieldOf("portraitBorder").forGetter((obj) -> obj.portraitBorder),
            NineSliceSprite.CODEC.fieldOf("scrollBorder").forGetter((obj) -> obj.scrollBorder),
            SpriteData.CODEC.fieldOf("scrollPoint").forGetter((obj) -> obj.scrollPoint),
            SpriteData.CODEC.fieldOf("scrollPointHovered").forGetter((obj) -> obj.scrollPointHovered),
            SoundEvent.CODEC.optionalFieldOf("clickSound", SoundEvents.UI_BUTTON_CLICK).forGetter((obj) -> obj.clickSound),
            Codec.INT.optionalFieldOf("charactersPerTick", 1).forGetter((obj) -> obj.charactersPerTick)
    ).apply(instance, BasicDialogueStyle::new));
    public BasicDialogueStyle(NineSliceSprite textBorder, NineSliceSprite choiceBorder, NineSliceSprite choiceHoverBorder, NineSliceSprite nameBorder, SpriteData portraitBorder, NineSliceSprite scrollBorder, SpriteData scrollPoint, SpriteData scrollPointHovered, Holder<SoundEvent> clickSound, int charactersPerTick) {
        this.textBorder = textBorder;
        this.choiceHoverBorder = choiceHoverBorder;
        this.choiceBorder = choiceBorder;
        this.nameBorder = nameBorder;
        this.portraitBorder = portraitBorder;
        this.scrollBorder = scrollBorder;
        this.scrollPoint = scrollPoint;
        this.scrollPointHovered = scrollPointHovered;
        this.clickSound = clickSound;
        this.charactersPerTick = charactersPerTick;
    }
    @Override
    public MapCodec<? extends DialogueStyle> getCodec() {
        return CODEC;
    }

    @Override
    public boolean mouseClick(DialogueInstance instance, double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        for (int i = 0; i < instance.entry.choices.size(); i++) {
            if (isHovering(instance, i, mouseX, mouseY)) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
                runChoice(i);
                return true;
            }
        }
        if (isHoveringScroll(instance, mouseX, mouseY)) {
            scrolling = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseRelease(DialogueInstance instance, double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        scrolling = false;
        return super.mouseRelease(instance, mouseX, mouseY, button);
    }

    double scroll = 0;
    boolean scrolling = false;

    @Override
    public boolean mouseDrag(DialogueInstance instance, double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button != 0) {
            return false;
        }
        if (scrolling) {
            double y = (mouseY+dragY)-((double)scrollPoint.height()/2d);
            double progress = y-getScrollPointStartY();
            progress = Math.clamp(progress, 0d, getScrollHeight());
            progress /= getScrollHeight();
            scroll = progress;
            return true;
        }
        return false;
    }

    @Override
    public void changeEntry(DialogueInstance instance, String from, String to) {
        super.changeEntry(instance, from, to);
        scroll = 0;
        scrolling = false;
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
    public int getScrollX() {
        int dialogueWidth = getDialogueBoxWidth();
        int x = getX();
        return (x+dialogueWidth)-(2+getScrollBorder().width());
    }
    public int getScrollY() {
        int y = getY();
        return y+2;
    }
    public int getScrollPointX() {
        int x = getScrollX();
        return x+1;
    }
    public int getScrollPointStartY() {
        return getScrollY()+1;
    }
    public int getScrollPointY() {
        int y = getScrollPointStartY();
        y += (int)((double)(getScrollHeight()-2)*scroll);
        return y;
    }
    public int getScrollHeight() {
        return (getDialogueBoxHeight()-4)-scrollPoint.height();
    }
    public boolean scrollEnabled(DialogueInstance instance) {
        Font font = getFont();
        List<FormattedText> lines = font.getSplitter().splitLines(instance.entry.text, (getDialogueBoxWidth()-48)-8, Style.EMPTY);
        int neededHeight = font.lineHeight*lines.size();
        return neededHeight >= getDialogueBoxHeight() - 10;
    }
    public boolean isHoveringScroll(DialogueInstance instance, double mouseX, double mouseY) {
        if (!scrollEnabled(instance)) {
            return false;
        }
        int scrollX = getScrollPointX();
        int scrollY = getScrollPointY();
        int width = scrollPoint.width();
        int height = scrollPoint.height();

        if (mouseX >= scrollX && mouseX <= scrollX+width) {
            if (mouseY >= scrollY && mouseY <= scrollY+height) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void tick(DialogueInstance instance, double lastTicksPassed, double ticksPassed) {
        super.tick(instance, lastTicksPassed, ticksPassed);
        String str = instance.entry.text.getString().replace("\n", "");
        int strLength = str.length()-1;
        int charactersShown = (int)Math.floor((double)charactersPerTick*ticksPassed);
        int lastCharactersShown = (int)Math.floor((double)charactersPerTick*lastTicksPassed);
        if (charactersShown > strLength) charactersShown = strLength;
        if (lastCharactersShown > strLength) lastCharactersShown = strLength;
        boolean play = false;
        for (int i = lastCharactersShown+1; i <= charactersShown; i++) {
            char character = str.charAt(i);
            if (character != ' ') {
                play = true;
            }
        }
        if (charactersShown != lastCharactersShown) {
            Font font = getFont();
            List<FormattedText> lines = font.getSplitter().splitLines(instance.entry.text, (getDialogueBoxWidth()-48)-8, Style.EMPTY);
            int characterStart = 0;
            int pixelsPassed = 0;
            int fullPixels = 0;
            for (FormattedText i : lines) {
                if (characterStart < charactersShown) {
                    pixelsPassed += font.lineHeight;
                }
                fullPixels += font.lineHeight;
                characterStart += i.getString().length();
            }
            int sub = (getDialogueBoxHeight()-font.lineHeight);
            int pixelsNeeded = pixelsPassed-sub;
            if (pixelsNeeded < 0) pixelsNeeded = 0;
            fullPixels -= sub;
            if (fullPixels < 0) fullPixels = 0;
            scroll = Math.clamp((double)pixelsNeeded/(double)fullPixels, 0d, 1d);

            if (play) Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(instance.entry.getSpeaker().talkSound, 1));
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
        NineSliceSprite choiceHoverBorder = getChoiceHoverBorder();
        NineSliceSprite nameBorder = getNameBorder();
        NineSliceSprite scrollBorder = getScrollBorder();
        SpriteData portraitBorder = getPortraitBorder();
        SpriteData scrollPoint = getScrollPoint();
        SpriteData scrollPointHovered = getScrollPointHovered();

        int charactersShown = (int)Math.floor((double)charactersPerTick*instance.ticksOnEntry);
        int dialogueWidth = getDialogueBoxWidth();
        int dialogueHeight = getDialogueBoxHeight();
        int x = getX();
        int y = getY();
        textBorder.blit(graphics, x, y, dialogueWidth, dialogueHeight);

        int centerY = y+(dialogueHeight/2);

        Font font = getFont();

        Component text = instance.entry.text;

        List<FormattedText> lines = font.getSplitter().splitLines(text, (dialogueWidth-48)-8, Style.EMPTY);
        int padding = 0;
        int lineY = centerY-(int)(lines.size()*((font.lineHeight/2f)+(float)padding));
        if (lineY < y+6) {
            lineY = y+6;
        }
        int neededHeight = font.lineHeight*lines.size();
        neededHeight -= dialogueHeight-10;
        double scroll = neededHeight*this.scroll;
        graphics.enableScissor(x, y+6, x+dialogueWidth, y+dialogueHeight-4);
        lineY -= (int)scroll;
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
        graphics.disableScissor();

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
        if (scrollEnabled(instance)) {
            scrollBorder.blit(graphics, getScrollX(), getScrollY() + scrollBorder.top(), scrollBorder.width(), (getScrollHeight() + 1) - scrollBorder.bottom());
            (isHoveringScroll(instance, mouseX, mouseY) || scrolling ? scrollPointHovered : scrollPoint).blit(graphics, getScrollPointX(), getScrollPointY());
        } else {
            this.scroll = 0;
        }
        graphics.flush();
        RenderSystem.disableBlend();
    }
    public int getDialogueBoxWidth() {
        return 64*5;
    }
    public int getDialogueBoxHeight() {
        return 48;
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
    public NineSliceSprite getScrollBorder() {
        return scrollBorder;
    }
    public SpriteData getScrollPoint() {
        return scrollPoint;
    }
    public SpriteData getScrollPointHovered() {
        return scrollPointHovered;
    }
    public Font getFont() {
        return Minecraft.getInstance().font;
    }
}
