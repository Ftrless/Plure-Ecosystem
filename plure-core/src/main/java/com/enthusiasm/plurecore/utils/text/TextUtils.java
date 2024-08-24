package com.enthusiasm.plurecore.utils.text;

import java.util.Arrays;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class TextUtils {
    private static MutableText parseVariable(String var) {
        return Text.literal(var)
                .setStyle(Style.EMPTY.withColor(FormatUtils.getColor(FormatUtils.Colors.FOCUS)));
    }

    private static MutableText parseVariable(Object var) {
        if (var instanceof PlayerEntity) {
            return parsePlayer((PlayerEntity) var);
        }

        if (var instanceof Text) {
            return ((Text) var).copy();
        }

        return parseVariable(var.toString());
    }

    private static MutableText parsePlayer(PlayerEntity playerEntity) {
        return playerEntity.getDisplayName().copy()
                .setStyle(Style.EMPTY.withColor(FormatUtils.getColor(FormatUtils.Colors.FOCUS)));
    }

    public static MutableText translation(String msgKey, FormatUtils.Colors style, Object... args) {
        return Text.translatable(
                msgKey,
                Arrays.stream(args).map(TextUtils::parseVariable).toArray()
        ).setStyle(Style.EMPTY.withColor(FormatUtils.getColor(style)));
    }

    public static String declensionWord(long num, String[] declensions) {
        long result = num % 100;

        if (result >= 10 && result <= 20) {
            return declensions[2];
        }

        result = num % 10;

        if (result == 0 || result > 4) {
            return declensions[2];
        } else if (result > 1) {
            return declensions[1];
        } else if (result == 1) {
            return declensions[0];
        } else {
            return null;
        }
    }
}
