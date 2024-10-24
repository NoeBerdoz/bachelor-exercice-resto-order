package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.utils.OracleConnector;

import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException {

    // TODO
    // Add validation/error handling for PropertiesLoader in case properties are missing or fail to load.
    // Integrate a proper logging system (SLF4J with a logger implementation like Logback).
    // Ensure the connection pool is always closed properly during application shutdown.

    OracleConnector.isDatabaseConnectable();
    OracleConnector.getConnectionFromPool();
    System.out.println("[+] Open Connection from pool!");
    OracleConnector.closePool();
    System.out.println("[-] Closed connection from pool!");

    (new MainCLI()).run();
  }
}
