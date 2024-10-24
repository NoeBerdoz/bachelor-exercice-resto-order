package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.utils.OracleConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws SQLException {

        // TODO
        // Add validation/error handling for PropertiesLoader in case properties are missing or fail to load.
        // Integrate a proper logging system (SLF4J with a logger implementation like Logback).
        // Ensure the connection pool is always closed properly during application shutdown.
        // I will need to manage the database commits myself i think

        OracleConnector.isDatabaseConnectable();

        OracleConnector.getConnectionFromPool();
        logger.info("Open Connection from pool!");
        System.out.println("[+] Open Connection from pool!");
        OracleConnector.closePool();
        System.out.println("[-] Closed connection from pool!");

        (new MainCLI()).run();
    }
}
