package com.enthusiasm.plureutils.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurecore.utils.TimeUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.config.ConfigManager;

public class RestartService {
    private static final boolean RESTART_STATE = ConfigManager.getConfig().enableRestarts;
    private static final long RESTART_INTERVAL = ConfigManager.getConfig().restartInterval;
    private static final List<Integer> RESTART_NOTIFY_INTERVALS = ConfigManager.getConfig().restartNotifyIntervals;
    private static final List<Integer> RESTART_TITLE_NOTIFY_INTERVALS = ConfigManager.getConfig().restartTitleNotifyIntervals;
    private static final Text RESTART_MESSAGE = Text.empty()
            .append(Text.literal("Перезагрузка сервера\n"))
            .append(Text.literal("Вернемся уже через "))
            .append(Text.literal("30 ").setStyle(Style.EMPTY.withColor(FormatUtils.getColor(FormatUtils.Colors.FOCUS))))
            .append(Text.literal("cекунд!"));
    private static ScheduledFuture<?> scheduledRestartTask;
    private static final List<ScheduledFuture<?>> scheduledNotifyTasks = new ArrayList<>();
    private static MinecraftServer SERVER_INSTANCE;

    public static void onInitialize(MinecraftServer server) {
        if (!RESTART_STATE) {
            return;
        }

        SERVER_INSTANCE = server;
        scheduleRestartTask(RESTART_INTERVAL);
    }

    private static void scheduleRestartTask(long delay) {
        scheduledRestartTask = ThreadUtils.schedule(RestartService::restartServer, delay);

        RESTART_NOTIFY_INTERVALS.forEach(interval -> {
            if (interval < delay) {
                scheduledNotifyTasks.add(ThreadUtils.schedule(() -> RestartService.broadcastMessage(interval), delay - interval));
            }
        });

        RESTART_TITLE_NOTIFY_INTERVALS.forEach(interval -> {
            if (interval < delay) {
                scheduledNotifyTasks.add(ThreadUtils.schedule(() -> RestartService.broadcastTitle(interval), delay - interval));
            }
        });
    }

    private static void restartServer() {
        SERVER_INSTANCE.getPlayerManager().getPlayerList().forEach(player -> {
            player.networkHandler.disconnect(RestartService.RESTART_MESSAGE);
        });

        ThreadUtils.runOnMainThread(() -> SERVER_INSTANCE.saveAll(false, true, true));
        SERVER_INSTANCE.stop(true);
    }

    public static void postponeRestart(long postponeMillis) {
        if (scheduledRestartTask == null) return;

        var currentDelay = scheduledRestartTask.getDelay(TimeUnit.MILLISECONDS);

        shutdownCurrentTasks();
        scheduleRestartTask(
                currentDelay + postponeMillis
        );
    }

    public static void forceRestart(long forceMillis) {
        if (scheduledRestartTask == null) return;

        shutdownCurrentTasks();
        scheduleRestartTask(
            forceMillis
        );
    }

    public static long getRemainingTime() {
        return scheduledRestartTask == null ? 0 : scheduledRestartTask.getDelay(TimeUnit.MILLISECONDS);
    }

    private static void shutdownCurrentTasks() {
        if (scheduledRestartTask != null) {
            scheduledRestartTask.cancel(true);
        }

        if (!scheduledNotifyTasks.isEmpty()) {
            scheduledNotifyTasks.forEach(task -> task.cancel(true));
            scheduledNotifyTasks.clear();
        }
    }

    private static void broadcastMessage(int restartAt) {
        MutableText broadcastText = TextUtils.translation(
                "global.restart.notify-message",
                FormatUtils.Colors.DEFAULT,
                TimeUtils.getFormattedRemainingTime(restartAt)
        );

        SERVER_INSTANCE.getPlayerManager().getPlayerList().forEach(player -> {
            player.sendMessage(broadcastText);
            player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, SoundCategory.MASTER, 0.8f, 1.8f);
        });
    }

    private static void broadcastTitle(int restartAt) {
        MutableText broadcastTextTitle = Text.empty();
        MutableText broadcastTextSubTitle = Text.empty();

        broadcastTextTitle.append(Text.literal("Перезагрузка сервера").setStyle(Style.EMPTY.withColor(FormatUtils.getColor(FormatUtils.Colors.FOCUS))));
        broadcastTextSubTitle.append(Text.literal("Через "));
        broadcastTextSubTitle.append(Text.literal(TimeUtils.getFormattedRemainingTime(restartAt)));

        SERVER_INSTANCE.getPlayerManager().getPlayerList().forEach(player -> {
            Function<Text, Packet<?>> constructorTitle = TitleS2CPacket::new;
            Function<Text, Packet<?>> constructorSubTitle = SubtitleS2CPacket::new;
            try {
                player.networkHandler.sendPacket(constructorTitle.apply(Texts.parse(player.getCommandSource(), broadcastTextTitle, player, 2)));
                player.networkHandler.sendPacket(constructorSubTitle.apply(Texts.parse(player.getCommandSource(), broadcastTextSubTitle, player, 2)));
            } catch (CommandSyntaxException ignored) {}
        });
    }
}
