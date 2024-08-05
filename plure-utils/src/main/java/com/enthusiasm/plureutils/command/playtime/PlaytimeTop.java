//package com.enthusiasm.command.utils;
//
//import com.enthusiasm.EnthusiasmUtils;
//import com.enthusiasm.util.TextUtils;
//import com.mojang.authlib.GameProfile;
//import com.mojang.authlib.properties.Property;
//import com.mojang.brigadier.Command;
//import com.mojang.brigadier.context.CommandContext;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import net.minecraft.nbt.NbtCompound;
//import net.minecraft.nbt.NbtHelper;
//import net.minecraft.nbt.NbtIo;
//import net.minecraft.server.command.ServerCommandSource;
//import net.minecraft.server.network.ServerPlayerEntity;
//import net.minecraft.stat.Stats;
//import net.minecraft.text.MutableText;
//import net.minecraft.util.WorldSavePath;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class PlaytimeTop implements Command<ServerCommandSource> {
//    @Override
//    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
//        ServerPlayerEntity targetPlayer = context.getSource().getPlayerOrThrow();
//
//        exec(context, targetPlayer);
//
//        return SINGLE_SUCCESS;
//    }
//
//    public void exec(CommandContext<ServerCommandSource> context, ServerPlayerEntity senderPlayer) throws CommandSyntaxException {
//        EnthusiasmUtils.LOGGER.info("Getting top 5 player");
//        Map<String, Long> topPlayers = getPlayerPlaytimeList(context.getSource().getServer().getSavePath(WorldSavePath.PLAYERDATA));
//        EnthusiasmUtils.LOGGER.info("Getted top 5 player");
//
//        MutableText header = TextUtils.translation("cmd.playtime.top", FormatUtils.Colors.DEFAULT);
//
//        int i = 1;
//        for (Map.Entry<String, Long> entry : topPlayers.entrySet()) {
//            long playTime = entry.getValue();
//            long days = playTime / (20 * 60 * 60 * 24);
//            long hours = (playTime % (20 * 60 * 60 * 24)) / (20 * 60 * 60);
//            long minutes = (playTime % (20 * 60 * 60)) / (20 * 60);
//
//            header.append(TextUtils.translation("cmd.playtime.top.element", FormatUtils.Colors.DEFAULT, i++, entry.getKey(), days, hours, minutes));
//        }
//
//        context.getSource().sendFeedback(header, false);
//    }
//
//        private Map<String, Long> getPlayerPlaytimeList(Path playerdataFolder) {
//            EnthusiasmUtils.LOGGER.info("Getting .dat files");
//            File[] playerFiles = playerdataFolder.toFile().listFiles((dir, name) -> name.endsWith(".dat"));
//            EnthusiasmUtils.LOGGER.info("Getted .dat files");
//
//            if (playerFiles == null) {
//                return Collections.emptyMap();
//            }
//
//            EnthusiasmUtils.LOGGER.info("Formatting top");
//
//            return Arrays.stream(playerFiles)
//                    .collect(Collectors.toMap(
//                            file -> {
//                                EnthusiasmUtils.LOGGER.info("Formatting nickname");
//                                GameProfile gameProfile = readGameProfileFromFile(file);
//
//                                if (gameProfile != null) {
//                                    EnthusiasmUtils.LOGGER.info("Formatted nickname");
//                                    return gameProfile.getName();
//                                } else {
//                                    return null;
//                                }
//                            },
//                            file -> {
//                                EnthusiasmUtils.LOGGER.info("Formatting playtime");
//                                GameProfile gameProfile = readGameProfileFromFile(file);
//
//                                if (gameProfile != null) {
//                                    EnthusiasmUtils.LOGGER.info(gameProfile.getProperties().get("playtime").toString());
//                                    EnthusiasmUtils.LOGGER.info(gameProfile.getProperties().get(String.valueOf(Stats.PLAY_TIME)).toString());
//                                    Collection<Property> properties = gameProfile.getProperties().get(String.valueOf(Stats.PLAY_TIME));
//
//                                    if (!properties.isEmpty()) {
//                                        EnthusiasmUtils.LOGGER.info("Formatted playtime");
//                                        return properties.stream()
//                                                .map(property -> Long.parseLong(property.getValue()))
//                                                .findFirst()
//                                                .orElse(0L);
//                                    }
//                                }
//
//                                return 0L;
//                            },
//                            (existing, replacement) -> existing,
//                            LinkedHashMap::new
//                    ));
//    }
//
//    private GameProfile readGameProfileFromFile(File file) {
//        try (FileInputStream fileInputStream = new FileInputStream(file)) {
//            NbtCompound compoundTag = NbtIo.readCompressed(fileInputStream);
//            return NbtHelper.toGameProfile(compoundTag.getCompound("data"));
//        } catch (IOException e) {
//            return null;
//        }
//    }
//}

package com.enthusiasm.plureutils.command.playtime;

import com.enthusiasm.plurecore.cache.CacheService;
import com.enthusiasm.plurecore.utils.PlayerUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class PlaytimeTop implements Command<ServerCommandSource> {
    @Override
    public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        exec(context);

        return SINGLE_SUCCESS;
    }

    private void exec(CommandContext<ServerCommandSource> context) {
        MinecraftServer server = context.getSource().getServer();
        Map<String, Long> topPlayers = getPlayerPlaytimeList(server.getSavePath(WorldSavePath.STATS), server);

        PlayerUtils.sendFeedback(context, "cmd.playtime.top.feedback");

        int i = 1;
        for (Map.Entry<String, Long> entry : topPlayers.entrySet()) {
            long playTime = entry.getValue();
            long days = playTime / (20 * 60 * 60 * 24);
            long hours = (playTime % (20 * 60 * 60 * 24)) / (20 * 60 * 60);
            long minutes = (playTime % (20 * 60 * 60)) / (20 * 60);

            PlayerUtils.sendFeedback(context, "cmd.playtime.top.element", i++, entry.getKey(), days, hours, minutes);
        }
    }

    private Map<String, Long> getPlayerPlaytimeList(Path worldFolder, MinecraftServer server) {
        File[] statFiles = worldFolder.toFile().listFiles((dir, name) -> name.endsWith(".json"));

        if (statFiles == null) {
            return Collections.emptyMap();
        }

        Map<String, Long> playtimeMap = new HashMap<>();

        for (File file : statFiles) {
            String fileName = file.getName().replace(".json", "");
            UUID playerUUID = UUID.fromString(fileName);

            long playTime = readPlaytimeFromFile(file);

            String playerName = CacheService.getUserByUUID(playerUUID).orElse(null);

            if (playerName == null) {
                continue;
            }

            playtimeMap.put(playerName, playTime);
        }

        return playtimeMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing, LinkedHashMap::new));
    }

    private long readPlaytimeFromFile(File file) {
        try (FileReader fileReader = new FileReader(file)) {
            JsonObject jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();

            if (jsonObject.has("stats")) {
                JsonObject statsObject = jsonObject.getAsJsonObject("stats");

                if (statsObject.has("minecraft:custom")) {
                    JsonObject customStatsObject = statsObject.getAsJsonObject("minecraft:custom");

                    if (customStatsObject.has("minecraft:play_time")) {
                        return customStatsObject.getAsJsonPrimitive("minecraft:play_time").getAsLong();
                    }
                }
            }
        } catch (IOException e) {
            PlureUtilsEntrypoint.LOGGER.error("Oops, reading error:", e);
        }

        return 0L;
    }
}

