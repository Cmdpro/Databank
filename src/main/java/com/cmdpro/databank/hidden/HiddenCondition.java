package com.cmdpro.databank.hidden;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.entity.player.Player;

public abstract class HiddenCondition {
    public abstract boolean isUnlocked(Player player);

    public abstract Serializer<?> getSerializer();

    public static abstract class Serializer<T extends HiddenCondition> {
        public abstract MapCodec<T> codec();
    }
}
