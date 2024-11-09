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
    public static Color mixColorsSubtractive(Color color1, Color color2) {
        return mixColorsSubtractive(color1, color2, 1f);
    }
    public static Color mixColorsSubtractive(Color color1, Color color2, float alpha) {
        return cmyToRgb(blendCmy(rgbToCmy(color1), rgbToCmy(color2), alpha));
    }
    public static float[] rgbToCmy(Color color) {
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;
        float c = 1f - r;
        float m = 1f - g;
        float y = 1f - b;
        return new float[] {c, m, y};
    }
    public static float[] blendCmy(float[] cmy1, float[] cmy2, float alpha) {
        float c, m, y;
        if (alpha == 1f) {
            c = Math.max(0, Math.min(1, cmy1[0] + cmy2[0]));
            m = Math.max(0, Math.min(1, cmy1[1] + cmy2[1]));
            y = Math.max(0, Math.min(1, cmy1[2] + cmy2[2]));
        } else {
            c = (1 - alpha) * cmy1[0] + alpha * cmy2[0];
            m = (1 - alpha) * cmy1[1] + alpha * cmy2[1];
            y = (1 - alpha) * cmy1[2] + alpha * cmy2[2];
        }
        return new float[] {c, m, y};
    }
    private static Color cmyToRgb(float[] cmy) {
        int r = (int)((1f - cmy[0]) * 255);
        int g = (int)((1f - cmy[1]) * 255);
        int b = (int)((1f - cmy[2]) * 255);
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));
        return new Color(r, g, b);
    }
}
