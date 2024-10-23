package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.utils.OracleConnector;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException {

    OracleConnector.testNewConnection();
    OracleConnector.setPollConfig();
    OracleConnector.getConnectionFromPool();
    System.out.println("[+] Open Connection from pool!");
    OracleConnector.closePool();
    System.out.println("[-] Closed connection from pool!");

    (new MainCLI()).run();
  }
}
