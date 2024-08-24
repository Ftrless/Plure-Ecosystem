package com.enthusiasm.plurekits.data.player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Util;

import com.enthusiasm.plurekits.PlureKitsEntrypoint;
import com.enthusiasm.plurekits.database.DatabaseService;

public class PlayerKitData {
    private final Map<String, Long> kitUsedTimes;
    private final ServerPlayerEntity player;
    private final DatabaseService databaseService;

    public PlayerKitData(ServerPlayerEntity player) {
        this.kitUsedTimes = new HashMap<>();
        this.player = player;
        this.databaseService = PlureKitsEntrypoint.getDatabaseService();
        loadFromDatabase();
    }

    public void useKit(String kitName) {
        long currentTime = Util.getEpochTimeMs();
        kitUsedTimes.put(kitName, currentTime);
        databaseService.saveKitCooldown(player.getUuidAsString(), kitName, currentTime);
    }

    public long getKitUsedTime(String kitName) {
        return kitUsedTimes.getOrDefault(kitName, 0L);
    }

    public void resetKitCooldown(String kitName) {
        kitUsedTimes.remove(kitName);
        databaseService.resetKitCooldown(player.getUuidAsString(), kitName);
    }

    public void resetAllKits() {
        kitUsedTimes.clear();
        databaseService.resetAllKitCooldowns(player.getUuidAsString());
    }

    private void loadFromDatabase() {
        CompletableFuture<Map<String, Long>> cooldowns = databaseService.getKitCooldownsForPlayer(player.getUuidAsString());

        cooldowns.thenAccept(kitUsedTimes::putAll);
    }
}
