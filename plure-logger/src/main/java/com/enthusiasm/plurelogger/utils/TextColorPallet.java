package com.enthusiasm.plurelogger.utils;

import net.minecraft.text.Style;
import net.minecraft.text.TextColor;

import com.enthusiasm.plurelogger.config.ConfigWrapper;

public final class TextColorPallet {
    public static Style getPrimary() {
        return Style.EMPTY.withColor(parseColor(ConfigWrapper.getConfig().primary));
    }

    public static Style getPrimaryVariant() {
        return Style.EMPTY.withColor(parseColor(ConfigWrapper.getConfig().primaryVariant));
    }

    public static Style getSecondary() {
        return Style.EMPTY.withColor(parseColor(ConfigWrapper.getConfig().secondary));
    }

    public static Style getSecondaryVariant() {
        return Style.EMPTY.withColor(parseColor(ConfigWrapper.getConfig().secondaryVariant));
    }

    public static Style getLight() {
        return Style.EMPTY.withColor(parseColor(ConfigWrapper.getConfig().light));
    }

    private static TextColor parseColor(String colorString) {
        return TextColor.parse(colorString);
    }
}