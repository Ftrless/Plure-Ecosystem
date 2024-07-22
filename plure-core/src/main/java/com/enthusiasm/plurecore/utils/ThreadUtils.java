package com.enthusiasm.plurecore.utils;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ThreadUtils {
    private static final ExecutorService ASYNC_EXECUTOR = Executors.newWorkStealingPool();
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR = Executors.newScheduledThreadPool(2);

    /**
     * Выполняет задачу в основном потоке Minecraft.
     *
     * @param server Экземпляр сервера Minecraft
     * @param task   Задача для выполнения в основном потоке
     */
    public static void runOnMainThread(MinecraftServer server, Runnable task) {
        server.execute(task);
    }

    /**
     * Выполняет задачу асинхронно.
     *
     * @param task Задача для выполнения асинхронно
     * @return Объект Future, представляющий результат выполнения задачи
     */
    public static Future<?> runAsync(Runnable task) {
        return ASYNC_EXECUTOR.submit(task);
    }

    /**
     * Выполняет задачу асинхронно
     *
     * @param task     Задача для выполнения асинхронно
     * @param <T>      Тип результата задачи
     * @return Объект CompletableFuture, представляющий результат выполнения задачи
     */
    public static <T> CompletableFuture<T> runAsync(Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, ASYNC_EXECUTOR);
    }

    /**
     * Планирует выполнение задачи через определенное время.
     *
     * @param task  Задача для выполнения
     * @param delay Задержка в миллисекундах перед выполнением задачи
     * @return Объект ScheduledFuture, представляющий результат выполнения задачи
     */
    public static ScheduledFuture<?> schedule(Runnable task, long delay) {
        return SCHEDULED_EXECUTOR.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Планирует повторяющееся выполнение задачи.
     *
     * @param task         Задача для выполнения
     * @param initialDelay Начальная задержка в миллисекундах
     * @param period       Период в миллисекундах между последовательными выполнениями
     * @return Объект ScheduledFuture, представляющий результат выполнения задачи
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period) {
        return SCHEDULED_EXECUTOR.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * Выполняет задачу для каждого игрока в мире асинхронно.
     *
     * @param players Список игроков
     * @param task    Задача для выполнения для каждого игрока
     */
    public static void runForEachPlayerAsync(List<ServerPlayerEntity> players, Consumer<ServerPlayerEntity> task) {
        players.forEach(player -> runAsync(() -> task.accept(player)));
    }

    /**
     * Останавливает все исполнители.
     */
    public static void shutdown() {
        ASYNC_EXECUTOR.shutdown();
        SCHEDULED_EXECUTOR.shutdown();

        if (ASYNC_EXECUTOR.isShutdown()
                && SCHEDULED_EXECUTOR.isShutdown()
        ) return;

        try {
            if (!ASYNC_EXECUTOR.awaitTermination(20, TimeUnit.SECONDS)) {
                ASYNC_EXECUTOR.shutdownNow();
            }

            if (!SCHEDULED_EXECUTOR.awaitTermination(20, TimeUnit.SECONDS)) {
                SCHEDULED_EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            ASYNC_EXECUTOR.shutdownNow();
            SCHEDULED_EXECUTOR.shutdownNow();
        }
    }
}

