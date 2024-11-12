package ch.hearc.ig.orderresto.persistence.connection;

import ch.hearc.ig.orderresto.utils.SimpleLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static PropertiesLoader propertiesLoader = new PropertiesLoader("database.properties");

    private static final String DB_URL = propertiesLoader.getProperty("db.url");
    private static final String DB_USER = propertiesLoader.getProperty("db.user");
    private static final String DB_PASSWORD = propertiesLoader.getProperty("db.password");

    private static Connection connection;

    // Singleton database connection, creates it if necessary.
    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                SimpleLogger.warning("[+] CONNECTION OPENED");
            } catch (SQLException e) {
                SimpleLogger.error("Failed to connect to database");
                throw new SQLException("Failed to connect to database", e);
            }
            connection.setAutoCommit(false);
        }
        return connection;
    }

    public static void closeConnection() throws SQLException {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                SimpleLogger.warning("[-] CONNECTION CLOSED");
            } catch (SQLException e) {
                throw new SQLException("Failed to close database connection", e);
            }
        }
    }

}
