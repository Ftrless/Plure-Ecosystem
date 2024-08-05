package com.enthusiasm.plureutils.service;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurecore.utils.text.FormatUtils;
import com.enthusiasm.plurecore.utils.text.TextUtils;
import com.enthusiasm.plureutils.config.ConfigManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.MutableText;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class RestartService {
    private static final boolean RESTART_STATE = ConfigManager.getConfig().enableRestarts;
    private static final long RESTART_INTERVAL = ConfigManager.getConfig().restartInterval;
    private static ScheduledFuture<?> scheduledRestartTask;
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
    }

    private static void restartServer() {
        SERVER_INSTANCE.stop(true);
    }

    public static void postponeRestart(long postponeMillis) {
        if (scheduledRestartTask == null) return;

        var currentDelay = scheduledRestartTask.getDelay(TimeUnit.MILLISECONDS);
        scheduledRestartTask.cancel(true);

        scheduleRestartTask(
                currentDelay + postponeMillis
        );
    }

    public static void forceRestart(long forceMillis) {
        if (scheduledRestartTask == null) return;

        scheduledRestartTask.cancel(true);

        scheduleRestartTask(
               forceMillis
        );
    }

    public static long getRemainingTime() {
        return scheduledRestartTask == null ? 0 : scheduledRestartTask.getDelay(TimeUnit.MILLISECONDS);
    }

    private static void broadcastMessage(String message, String restartAt) {
        MutableText broadcastText = TextUtils.translation(message, FormatUtils.Colors.DEFAULT, restartAt);

        SERVER_INSTANCE.getPlayerManager().getPlayerList().forEach(player -> player.sendMessage(broadcastText));
    }
}
