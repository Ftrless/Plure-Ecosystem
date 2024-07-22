package com.enthusiasm.plurecore.cache;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;
import com.enthusiasm.plurecore.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class CacheService {
    private static final Map<UUID, String> cache = new ConcurrentHashMap<>();

    private static final Gson GSON = new Gson();
    private static final ReentrantLock lock = new ReentrantLock();
    private static final String CACHE_FILE_NAME = "usernamecache.json";
    private static String CACHE_FILE_PATH;

    public static void init() {
        String minecraftRoot = System.getProperty("user.dir");
        File usersCacheFile = new File(minecraftRoot, CACHE_FILE_NAME);

        CACHE_FILE_PATH = usersCacheFile.getAbsolutePath();

        if (usersCacheFile.exists()) {
            loadCacheFromDisk();
            return;
        }

        saveCacheToDisk();
    }

    public static void addOrUpdateUser(UUID uuid, String nickname) {
        cache.put(uuid, nickname);
        saveCacheToDisk();
    }

    public static Optional<UUID> getUserByNickname(String playerName) {
        return cache.entrySet().stream()
                .filter(entry -> entry.getValue().equals(playerName))
                .findFirst()
                .map(Map.Entry::getKey);
    }

    public static Optional<String> getUserByUUID(UUID uuid) {
        return Optional.ofNullable(cache.get(uuid));
    }

    public static GameProfile getUserProfile(UUID uuid) {
        String nickname = getUserByUUID(uuid).orElse(null);

        if (nickname == null) {
            return null;
        }

        return new GameProfile(uuid, nickname);
    }

    public static List<String> getCachedUsernames() {
        return new ArrayList<>(cache.values());
    }

    public static void removeUser(UUID uuid) {
        cache.remove(uuid);
        saveCacheToDisk();
    }

    public static boolean userExists(UUID uuid) {
        return cache.containsKey(uuid);
    }

    public static void clearCache() {
        cache.clear();
        saveCacheToDisk();
    }

    private static void loadCacheFromDisk() {
        lock.lock();

        FileUtils.readFileAsync(Path.of(CACHE_FILE_PATH))
                .thenAcceptAsync((result) -> {
                    if (result == null) {
                        lock.unlock();
                        return;
                    }

                    Type type = new TypeToken<List<CacheEntry>>() {}.getType();
                    List<CacheEntry> loadedEntries = GSON.fromJson(result, type);

                    if (loadedEntries != null) {
                        loadedEntries.forEach(entry -> cache.put(entry.uuid(), entry.nickname()));
                    }
                }).thenRun(lock::unlock);
    }

    private static void saveCacheToDisk() {
        lock.lock();

        try {
            List<CacheEntry> cacheEntries = new ArrayList<>();
            cache.forEach((uuid, nickname) -> cacheEntries.add(new CacheEntry(uuid, nickname)));

            String json = GSON.toJson(cacheEntries);
            FileUtils.writeFileAsync(Path.of(CACHE_FILE_PATH), json, false)
                    .thenRun(lock::unlock);
        } catch (Exception e) {
            PlureCoreEntrypoint.LOGGER.error("Ошибка сохранения кеша: {}", e.getMessage());
            lock.unlock();
        }
    }
}
