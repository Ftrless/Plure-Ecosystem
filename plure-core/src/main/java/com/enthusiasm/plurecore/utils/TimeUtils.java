package com.enthusiasm.plurecore.utils;

import com.enthusiasm.plurecore.utils.text.TextUtils;

import java.util.Locale;

public class TimeUtils {
    public static final String[] SECONDS = { "секунду", "секунды", "секунд" };
    public static final String[] MINUTES = { "минуту", "минуты", "минут" };
    public static final String[] HOURS = { "час", "часа", "часов" };
    public static final String[] DAYS = { "день", "дня", "дней" };
    public static final String[] YEARS = { "год", "года", "лет" };

    public static long parseDuration(String text) throws NumberFormatException {
        text = text.toLowerCase(Locale.ROOT);

        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            String[] times = text.replaceAll("([a-z]+)", "$1|").split("\\|");
            long time = 0;

            for (String x : times) {
                String numberOnly = x.replaceAll("[a-z]", "");
                String suffixOnly = x.replaceAll("[^a-z]", "");

                time += (long) switch (suffixOnly) {
                    case "с" -> Double.parseDouble(numberOnly) * 60 * 60 * 24L * 365L * 100L * 1000L;
                    case "y", "year", "years" -> Double.parseDouble(numberOnly) * 60 * 60 * 24L * 365L * 1000L;
                    case "mo", "month", "months" -> Double.parseDouble(numberOnly) * 60 * 60 * 24L * 30L * 1000L;
                    case "w", "week", "weeks" -> Double.parseDouble(numberOnly) * 60 * 60 * 24L * 7L * 1000L;
                    case "d", "day", "days" -> Double.parseDouble(numberOnly) * 60 * 60 * 24 * 1000L;
                    case "h", "hour", "hours" -> Double.parseDouble(numberOnly) * 60 * 60 * 1000L;
                    case "m", "minute", "minutes" -> Double.parseDouble(numberOnly) * 60 * 1000L;
                    default -> Double.parseDouble(numberOnly);
                };
            }

            return time;
        }
    }

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
                    .append(TextUtils.declensionWord(years, YEARS))
                    .append(INTENT);
        }

        if (days > 0) {
            builder.append(days)
                    .append(INTENT)
                    .append(TextUtils.declensionWord(days, DAYS))
                    .append(INTENT);
        }

        if (hours > 0) {
            builder.append(hours)
                    .append(INTENT)
                    .append(TextUtils.declensionWord(hours, HOURS))
                    .append(INTENT);
        }

        if (minutes > 0) {
            builder.append(minutes)
                    .append(INTENT)
                    .append(TextUtils.declensionWord(minutes, MINUTES))
                    .append(INTENT);
        }

        if (seconds > 0 || builder.isEmpty()) {
            builder.append(seconds)
                    .append(INTENT)
                    .append(TextUtils.declensionWord(seconds, SECONDS));
        }

        return builder.toString().trim();
    }
}
