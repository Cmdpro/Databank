package com.cmdpro.databank.worldgui.components.types;

import com.cmdpro.databank.worldgui.WorldGui;
import com.cmdpro.databank.worldgui.components.WorldGuiComponentType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;

public abstract class WorldGuiButtonComponent extends WorldGuiRectComponent {
    public WorldGuiButtonComponent(WorldGui gui, int x, int y, int width, int height) {
        super(gui, x, y, width, height);
    }

    @Override
    public void render(GuiGraphics guiGraphics) {
        Vec2 normal = getClientTargetNormal();
        boolean hovered = false;
        if (normal != null) {
            int x = normalXIntoGuiX(normal.x);
            int y = normalYIntoGuiY(normal.y);
            if (isPosInBounds(x, y, this.x, this.y, this.x+width, this.y+height)) {
                hovered = true;
            }
        }
        if (hovered) {
            renderHovered(guiGraphics);
        } else {
            renderNormal(guiGraphics);
        }
    }

    @Override
    public void leftClick(boolean isClient, Player player, int x, int y) {
        if (isPosInBounds(x, y, this.x, this.y, this.x+width, this.y+height)) {
            leftClickButton(isClient, player, x, y);
        }
    }
    @Override
    public void rightClick(boolean isClient, Player player, int x, int y) {
        if (isPosInBounds(x, y, this.x, this.y, this.x+width, this.y+height)) {
            rightClickButton(isClient, player, x, y);
        }
    }

    public abstract void renderNormal(GuiGraphics guiGraphics);
    public abstract void renderHovered(GuiGraphics guiGraphics);
    public abstract void leftClickButton(boolean isClient, Player player, int x, int y);
    public abstract void rightClickButton(boolean isClient, Player player, int x, int y);

}
