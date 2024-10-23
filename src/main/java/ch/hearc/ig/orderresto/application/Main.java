package ch.hearc.ig.orderresto.application;

import ch.hearc.ig.orderresto.presentation.MainCLI;
import ch.hearc.ig.orderresto.utils.OracleConnector;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

  public static void main(String[] args) throws SQLException {

    OracleConnector.testConnection();

    (new MainCLI()).run();
  }
}
