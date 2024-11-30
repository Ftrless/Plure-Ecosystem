package com.enthusiasm.plureutils.command.playtime;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
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

        ThreadUtils.runAsync(() -> getPlayerPlaytimeList(statsPath))
                .thenAcceptAsync(topPlayers -> {
                    AtomicInteger count = new AtomicInteger(1);
                    topPlayers.forEach((playerName, playTime) -> {
                        long days = playTime / (20 * 60 * 60 * 24);
                        long hours = (playTime % (20 * 60 * 60 * 24)) / (20 * 60 * 60);
                        long minutes = (playTime % (20 * 60 * 60)) / (20 * 60);

                        ThreadUtils.runOnMainThread(() ->
                                 PlayerUtils.sendFeedback(context, "cmd.playtime.top.element",
                                         count.getAndIncrement(), playerName, days, hours, minutes
                                 )
                        );
                    });
                })
                .exceptionally(ex -> {
                    PlureUtilsEntrypoint.LOGGER.error("Error retrieving player playtimes", ex);
                    return null;
                });
    }

    private Map<String, Long> getPlayerPlaytimeList(Path statsFolder) {
        File[] statFiles = statsFolder.toFile().listFiles((dir, name) -> name.endsWith(".json"));

        if (statFiles == null) {
            return Collections.emptyMap();
        }

        Map<String, Long> playtimeMap = new ConcurrentHashMap<>();

        Arrays.stream(statFiles).forEach(file -> {
            UUID playerUUID = UUID.fromString(file.getName().replace(".json", ""));
            CompletableFuture<Long> playTimeFuture = readPlaytimeFromFile(file);

            playTimeFuture.thenAcceptAsync(playTime ->
                    CacheService.getUserByUUID(playerUUID)
                            .ifPresent(playerName -> playtimeMap.put(playerName, playTime))
            );
        });

        CompletableFuture.allOf(playtimeMap.values().toArray(new CompletableFuture[0])).join();

        return playtimeMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing, LinkedHashMap::new));
    }

    private CompletableFuture<Long> readPlaytimeFromFile(File file) {
        CompletableFuture<Long> future = new CompletableFuture<>();

        ThreadUtils.runAsync(() -> {
            try (FileReader fileReader = new FileReader(file)) {
                JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();

                if (jsonObject.has("stats")) {
                    JsonObject statsObject = jsonObject.getAsJsonObject("stats");

                    if (statsObject.has("minecraft:custom")) {
                        JsonObject customStatsObject = statsObject.getAsJsonObject("minecraft:custom");

                        if (customStatsObject.has("minecraft:play_time")) {
                            future.complete(customStatsObject.get("minecraft:play_time").getAsLong());
                            return;
                        }
                    }
                }

                future.complete(0L);
            } catch (IOException e) {
                PlureUtilsEntrypoint.LOGGER.error("Oops, reading error:", e);
            }
        });

        return future;
    }
}
