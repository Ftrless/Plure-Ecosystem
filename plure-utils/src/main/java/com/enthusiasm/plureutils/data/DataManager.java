package com.enthusiasm.plureutils.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.enthusiasm.plureutils.data.home.HomeDataManager;
import com.enthusiasm.plureutils.data.spawn.SpawnDataManager;
import com.enthusiasm.plureutils.data.warp.WarpDataManager;

public class DataManager {
    private static WarpDataManager warpDataManager;
    private static HomeDataManager homeDataManager;
    private static SpawnDataManager spawnDataManager;

    public static Path saveDir;

    public static void onInitialize() {
        initFolder(PlureUtilsEntrypoint.SERVER);

        warpDataManager = WarpDataManager.onServerStart();
        homeDataManager = HomeDataManager.onServerStart();
        spawnDataManager = SpawnDataManager.onServerStart();
    }

    private static void initFolder(MinecraftServer server) {
        saveDir = server.getSavePath(WorldSavePath.ROOT).resolve("plure_storage");

        try {
            Files.createDirectories(saveDir);
        } catch (IOException ignored) {}
    }

    public static WarpDataManager getWarpDataManager() {
        return warpDataManager;
    }
    public static HomeDataManager getHomeDataManager() {
        return homeDataManager;
    }
    public static SpawnDataManager getSpawnDataManager() {
        return spawnDataManager;
    }
}
