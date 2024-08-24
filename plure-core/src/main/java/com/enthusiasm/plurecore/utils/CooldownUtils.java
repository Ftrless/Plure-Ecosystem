package com.enthusiasm.plurecore.utils;


import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.UUID;

public class CooldownUtils {
    public static HashMap<String, Long> globalCommands = new HashMap<>();
    public static HashMap<String, HashMap<UUID, Long>> targetCommands = new HashMap<>();
    public static ZoneId timeZone = ZoneId.of("GMT+3");

    public static void addGlobalCooldown(String commandName) {
        globalCommands.put(commandName, getCurrentTime());
    }

    public static long getGlobalCooldown(String commandName) {
        return globalCommands.getOrDefault(commandName, 0L);
    }

    public static boolean isGlobalCooldownExpired(String commandName, long globalCooldown) {
        long lastUseTime = getGlobalCooldown(commandName);
        long currentTime = getCurrentTime();
        long timePassed = calculateTimePassed(currentTime, lastUseTime);

        return timePassed >= globalCooldown;
    }

    public static void addTargetCooldown(String commandName, UUID playerUUID) {
        targetCommands
                .computeIfAbsent(commandName, k -> new HashMap<>())
                .put(playerUUID, getCurrentTime());
    }

    public static HashMap<UUID, Long> getTargetCooldown(String commandName) {
        return targetCommands.getOrDefault(commandName, new HashMap<>());
    }

    public static long getTargetCooldown(String commandName, UUID playerUUID) {
        return getTargetCooldown(commandName).getOrDefault(playerUUID, 0L);
    }

    public static boolean isTargetCooldownExpired(String commandName, UUID playerUUID, long cooldownDuration) {
        long lastUseTime = getTargetCooldown(commandName, playerUUID);
        long currentTime = getCurrentTime();
        long timePassed = calculateTimePassed(currentTime, lastUseTime);

        return timePassed >= cooldownDuration;
    }

    public static long getCurrentTime() {
        return Instant.now().atZone(timeZone).toEpochSecond() * 1000L;
    }

    public static String getRemainingTime(long useTime, long globalCooldown) {
        long remainingTime = globalCooldown - calculateTimePassed(getCurrentTime(), useTime);

        return TimeUtils.getFormattedRemainingTime(remainingTime);
    }

    public static long calculateTimePassed(long currentTime, long useTime) {
        return currentTime - useTime;
    }
}
