package com.cmdpro.databank;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.List;

public class DatabankExtraCodecs {
    public static final Codec<Vector2i> VECTOR2I = Codec.INT
            .listOf()
            .comapFlatMap(
                    p_337581_ -> Util.fixedSize((List<Integer>)p_337581_, 2).map(p_253489_ -> new Vector2i(p_253489_.get(0), p_253489_.get(1))),
                    p_269787_ -> List.of(p_269787_.x, p_269787_.y)
            );
}
