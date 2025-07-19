package com.cmdpro.databank.hidden;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;

public abstract class HiddenTypeInstance<T> {
    public Hidden hidden;
    public boolean isHidden(T obj, Player player) {
        if (matches(obj)) {
            return !hidden.condition.isUnlocked(player);
        }
        return false;
    }
    public boolean isHiddenClient(T obj) {
        if (matches(obj)) {
            return !ClientHidden.unlocked.contains(hidden.id);
        }
        return false;
    }
    public abstract boolean matches(T obj);
    public abstract HiddenType getType();
    public abstract static class HiddenType<T extends HiddenTypeInstance<?>> {
        public abstract MapCodec<T> codec();
        public abstract StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();
        public abstract void updateClient();
        public HashMap<ResourceLocation, Hidden> getHiddenOfType() {
            HashMap<ResourceLocation, Hidden> hiddens = new HashMap<>();
            HiddenManager.hidden.forEach((k, v) -> { if (this.equals(v.type.getType())) { hiddens.put(k, v); } });
            return hiddens;
        }
    }
}
