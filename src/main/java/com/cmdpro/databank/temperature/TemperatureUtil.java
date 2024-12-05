package com.cmdpro.databank.temperature;

import com.cmdpro.databank.Databank;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class TemperatureUtil {

    // Commonly-used (assuming they'll be, anyway) values
    public static int ABSOLUTE_ZERO = -273; // should never be gone below
    public static int WATER_BOILING_POINT = 100;

    /**
     * Gets the ambient temperature of the given position.
     * @param pos the position to query
     * @return ambient temperature in degrees C
     */
    public static int getAmbientTemperatureAt(BlockPos pos) {
        Level world = Minecraft.getInstance().level;
        float T = getBiomeTemperature(world.getBiome(pos).value()); // start with biome temp
        T += (world.dimension() == Level.NETHER) ? WATER_BOILING_POINT : 0; // obviously much hotter in the Nether

        // time of day and weather
        if (world.canSeeSky(pos)) {
            float angle = world.getSunAngle(0);
            float sunIntensity = Math.clamp((float) (1.0f*(Math.cos(angle*2f+0.2f))), 0f, 1f);

            sunIntensity *= (float) (1.0d - world.getRainLevel(0)*5.0f / 16.0d);
            sunIntensity *= (float) (1.0d - world.getThunderLevel(0)*5.0f / 16.0d);

            T += (sunIntensity - 0.75f)*(world.isRaining() ? 2 : 4);
        }

        // altitude
        int seaLevel = world.getSeaLevel();
        int y = pos.getY();
        int dy = y-seaLevel;

        T -= (dy*0.75f);

        return (int) T;
    }

    /**
     * Takes a biome's internal temperature and produces a practical value from it.
     * @param biome the biome to use
     * @return workable temperature in degrees C
     */
    public static int getBiomeTemperature(Biome biome) {
        float factor = biome.getBaseTemperature();
        return (int) (30 * factor) - 10;
    }
}
