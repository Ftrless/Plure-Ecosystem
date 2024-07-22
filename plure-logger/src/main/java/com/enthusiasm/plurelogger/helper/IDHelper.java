package com.enthusiasm.plurelogger.helper;

public class IDHelper {
    public static String getIdentifier(String translationKey) {
        return translationKey
                .replace("block.", "")
                .replace("item.", "")
                .replace(".", ":");
    }
}
