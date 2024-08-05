package com.enthusiasm.plureutils.service.tpa;

import com.enthusiasm.plurecore.utils.ThreadUtils;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.concurrent.ScheduledFuture;

public class TpaService {
    private static final List<TpaRequestEntry> activeTeleportRequests = new ArrayList<>();
    private static final Map<TpaRequestEntry, ScheduledFuture<?>> timeoutTasks = new HashMap<>();

    public static boolean hasActiveRequest(TpaRequestEntry tpaRequest) {
        return activeTeleportRequests.stream()
                .anyMatch(tpaRequest::similarTo);
    }

    public static Optional<TpaRequestEntry> findFirstRequest(ServerPlayerEntity teleportFrom, ServerPlayerEntity teleportTo) {
        return activeTeleportRequests.stream()
                .filter(request -> request.teleportTo.equals(teleportTo) && request.teleportFrom.equals(teleportFrom))
                .findFirst();
    }

    public static List<TpaRequestEntry> findInitiatorRequests(ServerPlayerEntity teleportFrom) {
        return activeTeleportRequests.stream()
                .filter(request -> request.teleportFrom.equals(teleportFrom))
                .toList();
    }

    public static List<TpaRequestEntry> findReceiverRequests(ServerPlayerEntity teleportTo) {
        return activeTeleportRequests.stream()
                .filter(request -> request.teleportTo.equals(teleportTo))
                .toList();
    }

    public static void addTpaRequest(TpaRequestEntry tpaRequest) {
        activeTeleportRequests.add(tpaRequest);
        ScheduledFuture<?> timeoutTask = runTpaTimeout(tpaRequest);
        timeoutTasks.put(tpaRequest, timeoutTask);
    }

    public static void removeTpaRequest(TpaRequestEntry tpaRequest, boolean cancelTpaTimeout) {
        activeTeleportRequests.remove(tpaRequest);

        if (cancelTpaTimeout) {
            cancelTpaTimeout(tpaRequest);
        }
    }

    private static ScheduledFuture<?> runTpaTimeout(TpaRequestEntry tpaRequest) {
        return ThreadUtils.schedule(() -> {
            removeTpaRequest(tpaRequest, true);
        }, 30_000);
    }

    private static void cancelTpaTimeout(TpaRequestEntry tpaRequest) {
        ScheduledFuture<?> timeoutTask = timeoutTasks.get(tpaRequest);

        if (timeoutTask != null) {
            timeoutTask.cancel(true);
            timeoutTasks.remove(tpaRequest);
        }
    }
}
