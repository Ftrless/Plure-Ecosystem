package com.enthusiasm.plurelogger.storage.nbt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import com.enthusiasm.plurecore.utils.FolderUtils;
import com.enthusiasm.plurelogger.storage.nbt.entity.DeathEntity;
import com.enthusiasm.plurelogger.utils.Logger;

public class NbtService {
    private static final String BASE_DEATH_DIRECTORY = FolderUtils.getDataFolder("deaths").toAbsolutePath().toString();
    private static final LoadingCache<String, List<DeathEntity>> deathCache = CacheBuilder.newBuilder()
            .expireAfterAccess(120_000, TimeUnit.MILLISECONDS)
                .build(new CacheLoader<>() {
                    @Override
                    @NotNull
                    public List<DeathEntity> load(String playerName) throws IOException {
                        return loadDeathLogsForPlayer(playerName);
                    }
            });

    public static List<DeathEntity> getDeathLogsForPlayer(String playerName) throws ExecutionException {
        return deathCache.get(playerName);
    }

    public static void saveDeathLog(String playerName, DeathEntity deathEntity) throws IOException {
        Path playerDirectory = Paths.get(BASE_DEATH_DIRECTORY, playerName);
        Files.createDirectories(playerDirectory);

        String fileName = deathEntity.date() + ".dat";
        Path filePath = playerDirectory.resolve(fileName);

        NbtCompound nbtData = deathEntity.toNbt();

        try {
            File file = filePath.toFile();
            NbtIo.writeCompressed(nbtData, file);

            deathCache.invalidate(playerName);
        } catch (IOException e) {
            throw new IOException("Error while saving death log for " + playerName, e);
        }
    }

    private static List<DeathEntity> loadDeathLogsForPlayer(String playerName) throws IOException {
        Path playerDirectory = Paths.get(BASE_DEATH_DIRECTORY, playerName);

        if (!Files.exists(playerDirectory) || !Files.isDirectory(playerDirectory)) {
            return List.of();
        }

        return Files.list(playerDirectory)
                .filter(path -> path.toString().endsWith(".dat"))
                .map(path -> {
                    try {
                        NbtCompound nbtData = NbtIo.readCompressed(path.toFile());
                        return DeathEntity.fromNbt(nbtData);
                    } catch (IOException e) {
                        Logger.logError("Error while reading death log for " + playerName, e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
