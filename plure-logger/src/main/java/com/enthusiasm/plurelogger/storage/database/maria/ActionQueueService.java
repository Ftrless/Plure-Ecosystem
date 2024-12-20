package com.enthusiasm.plurelogger.storage.database.maria;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import com.enthusiasm.plurelogger.actions.IActionType;
import com.enthusiasm.plurelogger.config.ConfigWrapper;
import com.enthusiasm.plurelogger.utils.Logger;

public class ActionQueueService {
    private static final BlockingQueue<IActionType> queue = new LinkedBlockingQueue<>();
    private static ScheduledFuture<?> scheduledTask;

    public static int getSize() {
        return queue.size();
    }

    public static void start() {
        scheduledTask = ThreadUtils.scheduleAtFixedRate(ActionQueueService::drainBatch, 0, getBatchDelay());
    }

    public static boolean addToQueue(IActionType action) {
        if (action.isBlacklisted()) {
            return false;
        }

        return queue.add(action);
    }

    public static void drainBatch() {
        List<IActionType> batch = new ArrayList<>();
        queue.drainTo(batch, getBatchSize());

        try {
            DatabaseService.logActionBatch(batch);
        } catch (Exception e) {
            Logger.logError("Error draining action batch: ", e);
        }
    }

    public static void stop() {
        scheduledTask.cancel(true);
    }

    private static int getBatchSize() {
        return ConfigWrapper.getConfig().batchSize;
    }

    private static long getBatchDelay() {
        return ConfigWrapper.getConfig().batchDelay;
    }
}
