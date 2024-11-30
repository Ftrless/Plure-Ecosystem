package com.enthusiasm.plureutils.command.playtime;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurecore.utils.TimeUtils;
import com.enthusiasm.plureutils.PlureUtilsEntrypoint;

public class PlaytimeTop implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getServer();
        Path statsPath = server.getSavePath(WorldSavePath.STATS);

        PlayerUtils.sendFeedback(context, "cmd.playtime.top.header");

        getPlayerPlaytimeList(statsPath)
                .thenAcceptAsync(topPlayers -> {
                    AtomicInteger count = new AtomicInteger(1);
                    topPlayers.forEach((playerName, playTime) -> {
                        PlayerUtils.sendFeedback(context, "cmd.playtime.top.element",
                                count.getAndIncrement(), playerName, TimeUtils.getFormattedRemainingTime(playTime * 50)
                        );
                    });
                })
                .exceptionally(ex -> {
                    PlureUtilsEntrypoint.LOGGER.error("Error retrieving player playtimes", ex);
                    return null;
                });
    }

    private CompletableFuture<Map<String, Long>> getPlayerPlaytimeList(Path statsFolder) {
        File[] statFiles = statsFolder.toFile().listFiles((dir, name) -> name.endsWith(".json"));

        if (statFiles == null) {
            return CompletableFuture.completedFuture(Collections.emptyMap());
        }

        List<CompletableFuture<Optional<Map.Entry<String, Long>>>> futures = Arrays.stream(statFiles)
                .map(this::readPlayerPlaytimeEntry)
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .flatMap(Optional::stream)
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (existing, replacement) -> existing,
                                LinkedHashMap::new
                        )));
    }

    private CompletableFuture<Optional<Map.Entry<String, Long>>> readPlayerPlaytimeEntry(File file) {
        return ThreadUtils.runAsync(() -> {
            try (FileReader fileReader = new FileReader(file)) {
                JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
                UUID playerUUID = UUID.fromString(file.getName().replace(".json", ""));

                long playTime = jsonObject
                        .getAsJsonObject("stats")
                        .getAsJsonObject("minecraft:custom")
                        .get("minecraft:play_time")
                        .getAsLong();

                return CacheService.getUserByUUID(playerUUID)
                        .map(playerName -> Map.entry(playerName, playTime));
            } catch (IOException | NullPointerException | IllegalArgumentException e) {
                PlureUtilsEntrypoint.LOGGER.error("Error reading playtime data from file: " + file.getName(), e);
                return Optional.empty();
            }
        });
    }
}
