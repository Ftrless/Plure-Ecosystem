package com.enthusiasm.plurecore.database;

import com.enthusiasm.plurecore.PlureCoreEntrypoint;
import com.enthusiasm.plurecore.database.connection.HikariService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class AbstractDatabaseService {
    private final HikariService connection;
    protected static final Logger LOGGER = LoggerFactory.getLogger("PlureCore-Database");

    public AbstractDatabaseService(HikariService connection) {
        this.connection = connection;
    }

    public void init() {
        this.connection.init();
    }

    public Connection getConnection() throws SQLException {
        return this.connection.getConnection();
    }

    public abstract String getTableSchema();

    public void executeQuery(String query) {
        try (Connection conn = getConnection()) {
            conn.createStatement().execute(query);
        } catch (SQLException e) {
            LOGGER.error("Error executing query '{}': {}", query, e.getMessage());
        }
    }

    public void executePreparedQuery(String query, Object... params) {
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            setParameters(pstmt, params);
            pstmt.execute();
        } catch (SQLException e) {
            LOGGER.error("Error executing prepared query '{}': {}", query, e.getMessage());
        }
    }

    public ResultSet executeSelectQuery(String query, Object... params) {
        ResultSet resultSet = null;
        try {
            Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(query);
            setParameters(pstmt, params);
            resultSet = pstmt.executeQuery();
        } catch (SQLException e) {
            LOGGER.error("Error executing select query '{}': {}", query, e.getMessage());
        }
        return resultSet;
    }

    private void setParameters(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }

    public void shutdown() {
        try {
            this.connection.shutdown();
        } catch (Exception e) {
            LOGGER.error("Exception while disabling HikariConnection: {}", e.getMessage());
        }
    }
}
