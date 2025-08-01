package com.cmdpro.databank.networking.packet;

import com.cmdpro.databank.Databank;
import com.cmdpro.databank.networking.Message;
import com.cmdpro.databank.worldgui.WorldGuiEntity;
import com.cmdpro.databank.worldgui.components.WorldGuiComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WorldGuiInteractC2SPacket(int entity, int interactionType, int x, int y) implements Message {

    public static WorldGuiInteractC2SPacket read(FriendlyByteBuf buf) {
        int entity = buf.readInt();
        int interactionType = buf.readInt();
        int x = buf.readInt();
        int y = buf.readInt();
        return new WorldGuiInteractC2SPacket(entity, interactionType, x, y);
    }
    public static void write(FriendlyByteBuf buf, WorldGuiInteractC2SPacket obj) {
        buf.writeInt(obj.entity);
        buf.writeInt(obj.interactionType);
        buf.writeInt(obj.x);
        buf.writeInt(obj.y);
    }
    public static final Type<WorldGuiInteractC2SPacket> TYPE = new Type<>(Databank.locate("world_gui_interact"));
    @Override
    public Type<WorldGuiInteractC2SPacket> type() {
        return TYPE;
    }

    @Override
    public void handleServer(MinecraftServer server, ServerPlayer player, IPayloadContext context) {
        if (player.level().getEntity(entity) instanceof WorldGuiEntity entity) {
            if (entity.gui != null) {
                switch (interactionType) {
                    case 0: {
                        entity.gui.leftClick(false, player, x, y);
                        for (WorldGuiComponent i : entity.gui.components.stream().toList()) {
                            if (entity.gui.tryLeftClickComponent(false, player, i, x, y)) {
                                i.leftClick(false, player, x, y);
                            }
                        }
                        break;
                    }
                    case 1: {
                        entity.gui.rightClick(false, player, x, y);
                        for (WorldGuiComponent i : entity.gui.components.stream().toList()) {
                            if (entity.gui.tryLeftClickComponent(false, player, i, x, y)) {
                                i.rightClick(false, player, x, y);
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
}