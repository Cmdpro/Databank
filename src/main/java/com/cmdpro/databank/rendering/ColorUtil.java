package com.cmdpro.databank.rendering;

import java.awt.*;
import org.joml.Math;

public class ColorUtil {
    /**
     * Converts a color into hexadecimal.
     */
    public static int RGBtoHex(int R, int G, int B, int A) {
        return (B | G << 8 | R << 16 | A << 24);
    }

    /**
     * Converts a color into hexadecimal.
     */
    public static int RGBtoHex(int R, int G, int B) {
        return RGBtoHex(R, G, B, 255);
    }

    /**
     * Converts a color into hexadecimal.
     */
    public static int RGBtoHex(Color color) {
        return RGBtoHex(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    /**
     * Converts a hexadecimal color into RGB.
     */
    public static int[] hexToRGB(int hex) {
        int[] color = new int[4];
        color[0] = (hex >>> 16) & 0xFF;
        color[1] = (hex >>> 8) & 0xFF;
        color[2] = (hex) & 0xFF;
        color[3] = (hex >>> 24) & 0xFF;
        return color;
    }

    /**
     * Converts a hexadecimal color into RGB, as a Color.
     */
    public static Color hexToRGBColor(int hex) {
        int[] rgb = hexToRGB(hex);
        return new Color(rgb[0], rgb[1], rgb[2]);
    }

    /**
     * Takes two colors, and blends them together.
     * @param color1 the first color
     * @param color2 the second color
     * @param blend the blend factor
     * @return the blended color
     */
    public static Color blendColors(Color color1, Color color2, float blend) {
        return new Color(
                Math.lerp(color1.getRed()/255f, color2.getRed()/255f, blend),
                Math.lerp(color1.getGreen()/255f, color2.getGreen()/255f, blend),
                Math.lerp(color1.getBlue()/255f, color2.getBlue()/255f, blend),
                Math.lerp(color1.getAlpha()/255f, color2.getAlpha()/255f, blend)
        );
    }
}
