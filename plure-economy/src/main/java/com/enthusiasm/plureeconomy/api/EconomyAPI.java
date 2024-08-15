package com.enthusiasm.plureeconomy.api;

import com.enthusiasm.plureeconomy.PlureEconomyEntrypoint;
import com.enthusiasm.plureeconomy.database.DatabaseService;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class EconomyAPI {
    private static final DatabaseService databaseService = PlureEconomyEntrypoint.getDatabaseService();
    public static final String[] DECLENSIONED_NAME = new String[]{ "коин", "коина", "коинов" };

    public static CompletableFuture<Boolean> checkPlayerExists(ServerPlayerEntity player) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        databaseService
                .checkPlayerExists(player.getUuidAsString())
                .thenAcceptAsync(future::complete);

        return future;
    }

    public static void savePlayer(ServerPlayerEntity player) {
        databaseService
                .savePlayerData(player.getEntityName(), player.getUuidAsString(), 5);
    }

    public static CompletableFuture<Double> getPlayerMoney(ServerPlayerEntity player) {
        CompletableFuture<Double> future = new CompletableFuture<>();

        databaseService
                .getPlayerData(player.getUuidAsString())
                .thenAcceptAsync(economyEntry -> future.complete(economyEntry.name().equals("EMPTY") ? null : economyEntry.money()));

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

    public static boolean transferPlayerMoney(ServerPlayerEntity playerFrom, ServerPlayerEntity playerTo, double amount) {
        if (playerFrom == null || playerTo == null) {
            return false;
        }

        CompletableFuture<Double> futureFrom = getPlayerMoney(playerFrom);
        CompletableFuture<Double> futureTo = getPlayerMoney(playerTo);

        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(futureFrom, futureTo);

        combinedFuture.thenAcceptAsync(v -> {
            double moneyFrom = futureFrom.join();
            double moneyTo = futureTo.join();

            updatePlayerMoney(playerFrom, moneyFrom, amount, EconomyActions.TAKE);
            updatePlayerMoney(playerTo, moneyTo, amount, EconomyActions.ADD);
        });

        return true;
    }

    public static CompletableFuture<Map<String, Double>> getMoneyTop() {
        return getMoneyTop(10);
    }

    public static CompletableFuture<Map<String, Double>> getMoneyTop(int samplingFrom) {
        return databaseService.getMoneyTop(samplingFrom);
    }
}
