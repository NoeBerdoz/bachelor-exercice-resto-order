package ch.hearc.ig.orderresto.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleConnector {
    private static PropertiesLoader propertiesLoader = new PropertiesLoader("database.properties");

    private static final String DB_URL = propertiesLoader.getProperty("db.url");
    private static final String DB_USER = propertiesLoader.getProperty("db.user");
    private static final String DB_PASSWORD = propertiesLoader.getProperty("db.password");

    public static void testConnection() throws SQLException {
        try (Connection connection = OracleConnector.getConnection()) {
            if (connection != null) {
                System.out.println("[+] Connection established!");
            } else {
                System.out.println("[-] Connection failed!");
            }
        } catch (SQLException e) {
            System.err.println("[-] An error occurred: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}
