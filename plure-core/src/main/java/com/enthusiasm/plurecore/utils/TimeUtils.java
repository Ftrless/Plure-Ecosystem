package com.enthusiasm.plurecore.utils;

import com.enthusiasm.plurecore.utils.text.TextUtils;

public class TimeUtils {
    private static final String[] SECONDS = { "секунду", "секунды", "секунд" };
    private static final String[] MINUTES = { "минуту", "минуты", "минут" };
    private static final String[] HOURS = { "час", "часа", "часов" };
    private static final String[] DAYS = { "день", "дня", "дней" };
    private static final String[] YEARS = { "год", "года", "лет" };

    public static String getFormattedRemainingTime(long remainingTime) {
        if (remainingTime <= -1) return "никогда";

        remainingTime = remainingTime / 1000;

        long seconds = remainingTime % 60;
        long minutes = (remainingTime / 60) % 60;
        long hours = (remainingTime / (60 * 60)) % 24;
        long days = remainingTime / (60 * 60 * 24) % 365;
        long years = remainingTime / (60 * 60 * 24 * 365);

        StringBuilder builder = new StringBuilder();

        String INTENT = " ";

        if (years > 0) {
            builder.append(years)
                    .append(INTENT)
                    .append(TextUtils.declensionWord(years, YEARS));
        }

        if (days > 0) {
            builder.append(days)
                    .append(INTENT)
                    .append(TextUtils.declensionWord(days, DAYS));
        }

        if (hours > 0) {
            builder.append(hours)
                    .append(INTENT)
                    .append(TextUtils.declensionWord(hours, HOURS));
        }

        if (minutes > 0) {
            builder.append(minutes)
                    .append(INTENT)
                    .append(TextUtils.declensionWord(minutes, MINUTES));
        }

        if (seconds > 0 || builder.isEmpty()) {
            builder.append(seconds)
                    .append(INTENT)
                    .append(TextUtils.declensionWord(seconds, SECONDS));
        }

        return builder.toString().trim();
    }
}
