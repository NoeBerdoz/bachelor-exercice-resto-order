package ch.hearc.ig.orderresto.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnector {
    private static final Logger logger = LoggerFactory.getLogger(OracleConnector.class);

    private static PropertiesLoader propertiesLoader = new PropertiesLoader("database.properties");

    private static final String DB_URL = propertiesLoader.getProperty("db.url");
    private static final String DB_USER = propertiesLoader.getProperty("db.user");
    private static final String DB_PASSWORD = propertiesLoader.getProperty("db.password");
    private static final String DB_POOL_SIZE = propertiesLoader.getProperty("db.pool.size");

    private static HikariDataSource dataSource;

    public static void setPoolConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(DB_URL);
        config.setUsername(DB_USER);
        config.setPassword(DB_PASSWORD);
        config.setMaximumPoolSize(Integer.parseInt(DB_POOL_SIZE));

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnectionFromPool() throws SQLException {
        if (dataSource == null) {
            setPoolConfig();
        }
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    public static boolean isDatabaseConnectable() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            logger.info("Connection established!");
            return true;
        } catch (SQLException e) {
            logger.error("An error occurred: {}", e.getMessage());
            return false;
        }
    }
}
