package com.enthusiasm.plurecore.cache;

import com.enthusiasm.plurecore.utils.FileUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheService {
    private static final Map<UUID, String> cache = new ConcurrentHashMap<>();

    private static final Gson GSON = new Gson();
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
        String content = FileUtils.readFileAsync(Path.of(CACHE_FILE_PATH));

        if (content == null) {
            return;
        }

        Type type = new TypeToken<List<CacheEntry>>() {}.getType();
        List<CacheEntry> loadedEntries = GSON.fromJson(content, type);

        if (loadedEntries != null) {
            loadedEntries.forEach(entry -> cache.put(entry.uuid(), entry.nickname()));
        }
    }

    private static void saveCacheToDisk() {
        List<CacheEntry> cacheEntries = new ArrayList<>();
        cache.forEach((uuid, nickname) -> cacheEntries.add(new CacheEntry(uuid, nickname)));

        String json = GSON.toJson(cacheEntries);
        FileUtils.writeFileAsync(Path.of(CACHE_FILE_PATH), json, false);
    }
}
