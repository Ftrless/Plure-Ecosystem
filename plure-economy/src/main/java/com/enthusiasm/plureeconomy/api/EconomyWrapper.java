package com.enthusiasm.plureeconomy.api;

import com.enthusiasm.plureeconomy.PlureEconomyEntrypoint;
import com.enthusiasm.plureeconomy.database.DatabaseService;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class EconomyWrapper {
    private static final DatabaseService databaseService = PlureEconomyEntrypoint.getDatabaseService();

    public static CompletableFuture<Double> getPlayerMoney(ServerPlayerEntity player) {
        CompletableFuture<Double> future = new CompletableFuture<>();

        databaseService
                .getPlayerData(player.getUuidAsString())
                .thenAcceptAsync(economyEntry -> future.complete(economyEntry == null ? null : economyEntry.money()));

        return future;
    }

    public static void updatePlayerMoney(ServerPlayerEntity player, double currentBalance, double amount, EconomyActions action) {
        double newBalance = switch (action) {
            case ADD -> currentBalance + amount;
            case TAKE -> currentBalance - amount;
            case SET -> amount;
        };

        databaseService.updatePlayerMoney(player.getUuidAsString(), newBalance);
    }

    public static void transferPlayerMoney(ServerPlayerEntity playerFrom, ServerPlayerEntity playerTo, double amount) {
        CompletableFuture<Double> futureFrom = getPlayerMoney(playerFrom);
        CompletableFuture<Double> futureTo = getPlayerMoney(playerTo);

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futureFrom, futureTo);

        combinedFuture.thenAcceptAsync(v -> {
            double moneyFrom = futureFrom.join();
            double moneyTo = futureTo.join();

            updatePlayerMoney(playerFrom, moneyFrom, amount, EconomyActions.TAKE);
            updatePlayerMoney(playerTo, moneyTo, amount, EconomyActions.ADD);
        });
    }

    public static CompletableFuture<Map<String, Double>> getMoneyTop() {
        return getMoneyTop(10);
    }

    public static CompletableFuture<Map<String, Double>> getMoneyTop(int samplingFrom) {
        return databaseService.getMoneyTop(samplingFrom);
    }
}
