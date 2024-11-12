package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.presentation.MainCLI;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException {

        // TODO
        // Add validation/error handling for PropertiesLoader in case properties are missing or fail to load.
        // Ensure the connection pool is always closed properly during application shutdown.
        // I will need to manage the database commits myself i think it's currently on auto-commit.

        (new MainCLI()).run();
    }
}
