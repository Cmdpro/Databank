package com.cmdpro.databank.music;

import com.cmdpro.databank.hiddenblock.HiddenBlockConditions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MusicController {
    public MusicController(MusicConditions.MusicCondition condition, SoundEvent music, int priority) {
        this.condition = condition;
        this.music = music;
        this.priority = priority;
    }
    public MusicConditions.MusicCondition condition;
    public SoundEvent music;
    public int priority;
}
