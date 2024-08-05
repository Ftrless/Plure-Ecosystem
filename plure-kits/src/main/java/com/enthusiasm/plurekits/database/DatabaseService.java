package com.enthusiasm.plurekits.database;

import com.enthusiasm.plurecore.database.AbstractDatabaseService;
import com.enthusiasm.plurecore.database.connection.HikariService;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DatabaseService extends AbstractDatabaseService {
    public DatabaseService(HikariService connection) {
        super(connection);
        init();
        prepareTable("kits-cooldowns");
    }

    public void prepareTable(String tableName) {
        boolean exists = false;
        try (Connection conn = getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            try (ResultSet rs = metaData.getTables(null, null, tableName, null)) {
                if (rs.next()) {
                    exists = true;
                }
            }
        } catch (SQLException e) {
            LOGGER.error("Error checking table existence '{}': {}", tableName, e.getMessage());
        }

        if (!exists) {
            executeQuery(getTableSchema());
            LOGGER.info("Table '{}' created successfully.", tableName);
        }
    }

    @Override
    public String getTableSchema() {
        return "CREATE TABLE IF NOT EXISTS kits_cooldowns (" +
                "player_uuid VARCHAR(36) NOT NULL, " +
                "kit_name VARCHAR(255) NOT NULL, " +
                "used_time BIGINT NOT NULL, " +
                "PRIMARY KEY (player_uuid, kit_name)" +
                ");";
    }

    public void saveKitCooldown(String playerUuid, String kitName, long usedTime) {
        String query = "INSERT INTO kits_cooldowns (player_uuid, kit_name, used_time) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE used_time = ?";
        executePreparedQuery(query, playerUuid, kitName, usedTime, usedTime);
    }

    public CompletableFuture<Map<String, Long>> getKitCooldownsForPlayer(String playerUuid) {
        String query = "SELECT kit_name, used_time FROM kits_cooldowns WHERE player_uuid = ?";
        Map<String, Long> kitCooldowns = new HashMap<>();

        return executeSelectQuery(query, playerUuid)
                .thenApplyAsync(rs -> {
                    try (rs) {
                        while (rs != null && rs.next()) {
                            String kitName = rs.getString("kit_name");
                            long usedTime = rs.getLong("used_time");
                            kitCooldowns.put(kitName, usedTime);
                        }
                    } catch (SQLException e) {
                        LOGGER.error("Error fetching kit cooldowns for player '{}': {}", playerUuid, e.getMessage());
                    }
                    return kitCooldowns;
                })
                .exceptionally(e -> {
                    LOGGER.error("Exception in getKitCooldownsForPlayer: {}", e.getMessage());
                    return Collections.emptyMap();
                });
    }

    public void resetKitCooldown(String playerUuid, String kitName) {
        String query = "DELETE FROM kits_cooldowns WHERE player_uuid = ? AND kit_name = ?";
        executePreparedQuery(query, playerUuid, kitName);
    }

    public void resetAllKitCooldowns(String playerUuid) {
        String query = "DELETE FROM kits_cooldowns WHERE player_uuid = ?";
        executePreparedQuery(query, playerUuid);
    }
}
