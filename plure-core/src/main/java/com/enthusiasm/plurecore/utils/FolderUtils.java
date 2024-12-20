package com.enthusiasm.plurecore.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Future;

import net.fabricmc.loader.api.FabricLoader;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;

public class FolderUtils {
    public static Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Path getDataFolder() {
        return FabricLoader.getInstance().getGameDir().resolve("world").resolve("plure_storage");
    }

    public static Path getDataFolder(String folder) {
        return getDataFolder()
                .resolve(folder);
    }

    public static Future<?> createFolderAsync(String folderPath) {
        return ThreadUtils.runAsync(() -> {
            try {
                Files.createDirectories(Paths.get(folderPath));
            } catch (IOException e) {
                PlureCoreEntrypoint.LOGGER.error("Ошибка создании директории: {}", e.getMessage());
            }
        });
    }
}
