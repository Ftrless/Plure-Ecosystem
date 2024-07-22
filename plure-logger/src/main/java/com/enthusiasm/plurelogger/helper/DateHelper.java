package com.enthusiasm.plurelogger.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateHelper {
    public static final String LONG_PATTERN_DATE = "dd.MM.yyyy-HH:mm:ss";
    public static final String SHORT_PATTERN_DATE = "dd.MM.yyyy";

    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(LONG_PATTERN_DATE);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+3"));

        return dateFormat.format(new Date());
    }
}
