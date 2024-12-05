package com.cmdpro.databank.temperature;

import net.minecraft.core.BlockPos;

public interface TemperatureSensitive {

    /**
     * Gets the current temperature of the block.
     * @return temperature in degrees C
     */
    int getTemperature();

    /**
     * Gets the maximum temperature this block can withstand. When this value is reached,
     * the block may either cap the temperature there or fail due to overheating.
     * @return maximum temperature in degrees C
     */
    int getMaxTemperature();

    /**
     * Performs overheating behaviour on the block.
     * @param pos the position of the block
     */
    void overheat(BlockPos pos);
}
