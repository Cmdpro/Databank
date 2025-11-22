package com.cmdpro.databank.dialogue.styles;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.dialogue.DialogueChoice;
import com.cmdpro.databank.dialogue.DialogueInstance;
import com.cmdpro.databank.dialogue.DialogueStyle;
import com.cmdpro.databank.model.DatabankPartData;
import com.cmdpro.databank.networking.ModMessages;
import com.cmdpro.databank.networking.packet.ClickChoiceC2SPacket;
import com.cmdpro.databank.rendering.NineSliceSprite;
import com.cmdpro.databank.rendering.SpriteData;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.resources.language.FormattedBidiReorder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class BasicDialogueStyle extends DialogueStyle {
    public NineSliceSprite textBorder;
    public NineSliceSprite choiceBorder;
    public NineSliceSprite choiceHoverBorder;
    public NineSliceSprite nameBorder;
    public SpriteData portraitBorder;
    public static final MapCodec<BasicDialogueStyle> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            NineSliceSprite.CODEC.fieldOf("textBorder").forGetter((obj) -> obj.textBorder),
            NineSliceSprite.CODEC.fieldOf("choiceBorder").forGetter((obj) -> obj.choiceBorder),
            NineSliceSprite.CODEC.fieldOf("choiceHoverBorder").forGetter((obj) -> obj.choiceHoverBorder),
            NineSliceSprite.CODEC.fieldOf("nameBorder").forGetter((obj) -> obj.nameBorder),
            SpriteData.CODEC.fieldOf("portraitBorder").forGetter((obj) -> obj.portraitBorder)
    ).apply(instance, BasicDialogueStyle::new));
    public BasicDialogueStyle(NineSliceSprite textBorder, NineSliceSprite choiceBorder, NineSliceSprite choiceHoverBorder, NineSliceSprite nameBorder, SpriteData portraitBorder) {
        this.textBorder = textBorder;
        this.choiceHoverBorder = choiceHoverBorder;
        this.choiceBorder = choiceBorder;
        this.nameBorder = nameBorder;
        this.portraitBorder = portraitBorder;
    }
    @Override
    public MapCodec<? extends DialogueStyle> getCodec() {
        return CODEC;
    }

    @Override
    public boolean click(DialogueInstance instance, double mouseX, double mouseY, int button) {
        for (int i = 0; i < instance.entry.choices.size(); i++) {
            if (isHovering(instance, i, mouseX, mouseY)) runChoice(i);
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
    public void render(DialogueInstance instance, GuiGraphics graphics, double mouseX, double mouseY) {
        RenderSystem.enableBlend();
        NineSliceSprite textBorder = getTextBorder();
        NineSliceSprite choiceBorder = getChoiceBorder();
        NineSliceSprite nameBorder = getNameBorder();
        SpriteData portraitBorder = getPortraitBorder();

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
        for (FormattedText i : lines) {
            FormattedCharSequence formattedCharSequence = FormattedBidiReorder.reorder(i, Language.getInstance().isDefaultRightToLeft());
            graphics.drawString(font, formattedCharSequence, x+48, lineY, 0xFFFFFFFF);
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
