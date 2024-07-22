package com.enthusiasm.plurecore.utils;

import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.ApiStatus;

public class ColorUtils {
    /**
     * Генерирует градиент Minecraft цветов между двумя HEX цветами.
     * @param hexColor1 Начальный цвет
     * @param hexColor2 Конечный цвет
     * @return Массив цветов Minecraft в формате int
     */
    @ApiStatus.Experimental
    public static int[] gradientFromHex(String text, String hexColor1, String hexColor2) {
        return new int[0];
    }

    /**
     * Преобразует строку HEX цвета в формат RGB.
     * @param hexColor Строка цвета
     * @return Цвета RGB в формате int
     */
    public static int[] hexToRgbColor(String hexColor) {
        int rgb = Integer.parseInt(hexColor.substring(1), 16);
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        return new int[]{r, g, b};
    }

    /**
     * Преобразует RGB значения (0-255) в формат цвета Minecraft (0-1).
     * @param red Красная компонента (0-255)
     * @param green Зелёная компонента (0-255)
     * @param blue Синяя компонента (0-255)
     * @return Цвет Minecraft в формате int
     */
    public static int rgbToMinecraftColor(int red, int green, int blue) {
        int r = MathHelper.clamp(red, 0, 255);
        int g = MathHelper.clamp(green, 0, 255);
        int b = MathHelper.clamp(blue, 0, 255);
        return (r << 16) + (g << 8) + b;
    }

    /**
     * Смешивает два цвета Minecraft.
     * @param color1 Первый цвет Minecraft
     * @param color2 Второй цвет Minecraft
     * @param ratio Коэффициент смешивания (0.0 - 1.0)
     * @return Смешанный цвет Minecraft в формате int
     */
    public static int blendMinecraftColors(int color1, int color2, float ratio) {
        ratio = MathHelper.clamp(ratio, 0.0f, 1.0f);
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int blendedR = (int) (r1 * (1 - ratio) + r2 * ratio);
        int blendedG = (int) (g1 * (1 - ratio) + g2 * ratio);
        int blendedB = (int) (b1 * (1 - ratio) + b2 * ratio);

        return rgbToMinecraftColor(blendedR, blendedG, blendedB);
    }
}
