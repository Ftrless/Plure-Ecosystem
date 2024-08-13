package com.enthusiasm.plureutils.service;

import com.enthusiasm.plureutils.PlureUtilsEntrypoint;
import com.enthusiasm.plureutils.config.ConfigManager;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.BossBarManager;
import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AutowipeService {
    private static final boolean WIPE_STATE = ConfigManager.getConfig().enableAutowipe;
    public static final Identifier AUTO_WIPE_BAR = new Identifier("minecraft", "auto_wipe_bar");
    public static final Text AUTO_WIPE_BAR_COMPONENT = Text.literal("Данный мир автоматически вайпается каждый рестарт!")
            .fillStyle(Style.EMPTY.withColor(Formatting.RED));
    public static CommandBossBar AUTO_WIPE_BOSS_BAR;

    public static void runAutoWipe() {
        if (!WIPE_STATE) {
            return;
        }

        PlureUtilsEntrypoint.LOGGER.info("Running autowipe task");

        List<String> autowipeWorlds = ConfigManager.getConfig().autowipeWorlds;
        CountDownLatch latch = new CountDownLatch(autowipeWorlds.size());

        for (String worldId : autowipeWorlds) {
            try {
                wipeWorld(worldId);
            } catch (IOException e) {
                PlureUtilsEntrypoint.LOGGER.error("Error while trying to wipe world: {}", worldId);
                PlureUtilsEntrypoint.LOGGER.error(e.getMessage());
                latch.countDown();
            } finally {
                PlureUtilsEntrypoint.LOGGER.info("Successful wipe for: {}", worldId);
                latch.countDown();
            }
        }

        try {
            if (!latch.await(20, TimeUnit.SECONDS)) {
                PlureUtilsEntrypoint.LOGGER.error("Task of Wipe took Too Long Time!");
            }
        } catch (InterruptedException e) {
            PlureUtilsEntrypoint.LOGGER.error("Interrupted Error: {}", e.getMessage());
        }
    }

    private static void wipeWorld(String worldPath) throws IOException {
        WorldSavePath worldResource = new WorldSavePath(worldPath);
        Path dest = PlureUtilsEntrypoint.SERVER.getSavePath(worldResource);
        File cachedWorldsDir = PlureUtilsEntrypoint.SERVER.getFile(ConfigManager.getConfig().cachedWorldsDir);

        if (!worldPath.contains("DIM") && !worldPath.contains("dimensions")) {
            PlureUtilsEntrypoint.LOGGER.error("Path to forbidden world: {}, skipping...", worldPath);
            return;
        }

        if (!cachedWorldsDir.exists() || !cachedWorldsDir.isDirectory()) {
            PlureUtilsEntrypoint.LOGGER.warn("AutoWipe Folder is Empty!");
            return;
        }

        File source = cachedWorldsDir.toPath().resolve(worldPath).toFile();

        if (!source.exists() || !source.isDirectory()) {
            PlureUtilsEntrypoint.LOGGER.info("{} is empty, skipping...", source);
            return;
        }

        deleteFolder(dest);
        copyFolder(source.toPath(), dest);
    }

    private static void deleteFolder(Path folder) throws IOException {
        PlureUtilsEntrypoint.LOGGER.info("Deleting {}", folder);

        if (Files.exists(folder)) {
            Files.walk(folder)
                    .sorted(java.util.Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } else {
            PlureUtilsEntrypoint.LOGGER.error("Folder {} does not exist!", folder);
        }
    }

    private static void copyFolder(Path source, Path destination) throws IOException {
        PlureUtilsEntrypoint.LOGGER.info("World copy from {} to {}", source, destination);

        Files.walk(source)
                .forEach(sourcePath -> {
                    try {
                        Path destinationPath = destination.resolve(source.relativize(sourcePath));
                        if (Files.isDirectory(sourcePath)) {
                            Files.createDirectories(destinationPath);
                        } else {
                            Files.copy(sourcePath, destinationPath);
                        }
                    } catch (IOException e) {
                        PlureUtilsEntrypoint.LOGGER.error("Error while trying to copy file: {}", e.getMessage());
                    }
                });
        PlureUtilsEntrypoint.LOGGER.info("Copying of {} complete!", source);
    }

    public static void onInitializeBar() {
        BossBarManager bossBarManager = PlureUtilsEntrypoint.SERVER.getBossBarManager();
        CommandBossBar possibleBar = bossBarManager.get(AUTO_WIPE_BAR);

        if (possibleBar == null) {
            AUTO_WIPE_BOSS_BAR = bossBarManager.add(AUTO_WIPE_BAR, AUTO_WIPE_BAR_COMPONENT);
            AUTO_WIPE_BOSS_BAR.setColor(BossBar.Color.RED);
            AUTO_WIPE_BOSS_BAR.setValue(100);
            AUTO_WIPE_BOSS_BAR.setStyle(BossBar.Style.NOTCHED_20);
            AUTO_WIPE_BOSS_BAR.setMaxValue(100);
        } else {
            AUTO_WIPE_BOSS_BAR = possibleBar;
        }
    }
}
