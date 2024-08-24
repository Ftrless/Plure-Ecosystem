package com.enthusiasm.plureeconomy.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.enthusiasm.plurecore.database.AbstractDatabaseService;
import com.enthusiasm.plurecore.database.connection.HikariService;
import com.enthusiasm.plureeconomy.api.EconomyEntry;
import com.enthusiasm.plureeconomy.config.ConfigService;

public class DatabaseService extends AbstractDatabaseService {
    public DatabaseService(HikariService connection) {
        super(connection);
        init();
        prepareTable(ConfigService.getConfig().tableName);
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
            executeQuery(getTableSchema(tableName));
            LOGGER.info("Table '{}' created successfully.", tableName);
        }
    }

    @Override
    public String getTableSchema(String tableName) {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "name VARCHAR(200) NOT NULL, " +
                "uuid VARCHAR(56) NOT NULL, " +
                "money DOUBLE NOT NULL, " +
                "isPlayer TINYINT NOT NULL, " +
                "PRIMARY KEY (uuid)" +
                ") DEFAULT CHARSET = utf8mb4;";
    }

    public CompletableFuture<Boolean> checkPlayerExists(String uuid) {
        String query = "SELECT 1 FROM economy WHERE uuid = ? LIMIT 1";

        return executeSelectQuery(query, uuid)
                .thenApplyAsync(rs -> {
                    try (rs) {
                        return rs != null && rs.next();
                    } catch (SQLException e) {
                        LOGGER.error("Error checking player existence for uuid '{}': {}", uuid, e.getMessage());
                        return false;
                    }
                })
                .exceptionally(e -> {
                    LOGGER.error("Exception in playerExists: {}", e.getMessage());
                    return false;
                });
    }

    public void savePlayerData(String name, String uuid, double money) {
        String query = "INSERT INTO economy (name, uuid, money, isPlayer) " +
                "VALUES (?, ?, ?, ?) ";
        executePreparedQuery(query, name, uuid, money, 1);
    }

    public CompletableFuture<EconomyEntry> getPlayerData(String uuid) {
        String query = "SELECT name, money FROM economy WHERE uuid = ?";

        return executeSelectQuery(query, uuid)
                .thenApplyAsync(rs -> {
                    try (rs) {
                        if (rs != null && rs.next()) {
                            String name = rs.getString("name");
                            double money = rs.getDouble("money");

                            return new EconomyEntry(name, uuid, money);
                        }
                    } catch (SQLException e) {
                        LOGGER.error("Error fetching player data for uuid '{}': {}", uuid, e.getMessage());
                    }

                    return new EconomyEntry("EMPTY", "EMPTY", 0);
                })
                .exceptionally(e -> {
                    LOGGER.error("Exception in getPlayerData: {}", e.getMessage());
                    return new EconomyEntry("EMPTY", "EMPTY", 0);
                });
    }

    public CompletableFuture<Map<String, Double>> getMoneyTop(int samplingFrom) {
        String query = "SELECT name, money FROM economy ORDER BY money DESC LIMIT ?";
        CompletableFuture<Map<String, Double>> resultFuture = new CompletableFuture<>();

        executeSelectQuery(query, samplingFrom)
                .thenAcceptAsync(resultSet -> {
                    Map<String, Double> moneyTop = new HashMap<>();

                    try {
                        while (resultSet.next()) {
                            String name = resultSet.getString("name");
                            double money = resultSet.getDouble("money");

                            moneyTop.put(name, money);
                        }

                        resultFuture.complete(moneyTop);
                    } catch (SQLException e) {
                        LOGGER.error("Error while creating money top: {}", e.getMessage());
                        resultFuture.complete(moneyTop);
                    }
                });

        return resultFuture;
    }

    public void updatePlayerMoney(String uuid, double money) {
        String query = "UPDATE economy SET money = ? WHERE uuid = ?";
        executePreparedQuery(query, money, uuid);
    }

    public void deletePlayerData(String uuid) {
        String query = "DELETE FROM economy WHERE uuid = ?";
        executePreparedQuery(query, uuid);
    }
}
