package com.enthusiasm.plurelogger.utils;

import com.mojang.logging.LogUtils;

public class Logger {
    private static final org.slf4j.Logger LOGGER = LogUtils.getLogger();

    public static void logDebug(String message) {
        LOGGER.debug(message);
    }

    public static void logDebug(String message, Object ...args) {
        LOGGER.debug(message, args);
    }

    public static void logInfo(String message) {
        LOGGER.info(message);
    }

    public static void logInfo(String message, Object ...args) {
        LOGGER.info(message, args);
    }

    public static void logWarn(String message) {
        LOGGER.warn(message);
    }

    public static void logWarn(String message, Object ...args) {
        LOGGER.warn(message, args);
    }

    public static void logWarn(String message, Throwable throwable) {
        LOGGER.warn(message, throwable);
    }

    public static void logError(String message, Object ...args) {
        LOGGER.error(message, args);
    }

    public static void logError(String message, Throwable throwable) {
        LOGGER.error(message, throwable);
    }
}
