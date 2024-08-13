package com.enthusiasm.plurecore.database.connection;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HikariService {
    private final ConnectionCredentials credentials;
    private HikariDataSource hikari;

    public HikariService(ConnectionCredentials configuration) {
        this.credentials = configuration;
    }

    public void init() {
        HikariConfig config;
        try {
            config = new HikariConfig();
        } catch (LinkageError error) {
            PlureCoreEntrypoint.LOGGER.error(error.getMessage());
            return;
        }

        config.setPoolName("plure-core-hikari-" + this.credentials.getDatabase());

        String[] addressSplit = this.credentials.getAddress().split(":");
        String address = addressSplit[0];
        String port = addressSplit.length > 1 ? addressSplit[1] : "3306";

        config.setDriverClassName("org.mariadb.jdbc.Driver");
        config.setJdbcUrl(String.format("jdbc:%s://%s:%s/%s?autoReconnect=false&useSSL=false", "mariadb", address, port,  this.credentials.getDatabase()));
        config.setUsername(this.credentials.getUsername());
        config.setPassword(this.credentials.getPassword());

        Map<String, Object> properties = new HashMap<>(this.credentials.getProperties());

        overrideProperties(properties);
        setProperties(config, properties);

        config.setInitializationFailTimeout(-1);
        config.setMaximumPoolSize(this.credentials.getMaxPoolSize());
        config.setMinimumIdle(this.credentials.getMinIdleConnections());
        config.setIdleTimeout(10_000);
        config.setMaxLifetime(this.credentials.getMaxLifetime());
        config.setKeepaliveTime(this.credentials.getKeepAliveTime());
        config.setConnectionTimeout(this.credentials.getConnectionTimeout());

        this.hikari = new HikariDataSource(config);
    }

    public void shutdown() {
        if (this.hikari != null) {
            this.hikari.close();
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.hikari == null) {
            throw new SQLException("Unable to get a connection from the pool: hikari == null");
        }

        Connection connection = this.hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool: connection = null");
        }

        return connection;
    }


    protected void overrideProperties(Map<String, Object> properties) {
        properties.putIfAbsent("socketTimeout", String.valueOf(TimeUnit.SECONDS.toMillis(30)));
    }

    protected void setProperties(HikariConfig config, Map<String, Object> properties) {
        for (Map.Entry<String, Object> property : properties.entrySet()) {
            config.addDataSourceProperty(property.getKey(), property.getValue());
        }
    }
}
