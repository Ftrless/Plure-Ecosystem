package com.enthusiasm.plurecore.utils.text;

import net.minecraft.text.TextColor;

public class FormatUtils {
    private static final String DEFAULT = "gold";
    private static final String FOCUS = "aqua";
    private static final String ERROR = "red";
    private static final String SUCCESS = "green";

    public enum Colors {ERROR, DEFAULT, FOCUS, SUCCESS}

    public static TextColor getColor(Colors color) {
        return switch (color) {
            case ERROR -> TextColor.parse(FormatUtils.ERROR);
            case DEFAULT -> TextColor.parse(FormatUtils.DEFAULT);
            case FOCUS -> TextColor.parse(FormatUtils.FOCUS);
            case SUCCESS -> TextColor.parse(FormatUtils.SUCCESS);
        };
    }
}