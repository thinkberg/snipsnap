package com.neotis.util;

import java.sql.*;

public class ConnectionManager {
  public static Connection getConnection() {
// Register the Mckoi JDBC Driver
    try {
      Class.forName("com.mckoi.JDBCDriver").newInstance();
    } catch (Exception e) {
      System.out.println(
          "Unable to register the JDBC Driver.\n" +
          "Make sure the classpath is correct.\n" +
          "For example on Win32;  java -cp ../../mckoidb.jar;. SimpleApplicationDemo\n" +
          "On Unix;  java -cp ../../mckoidb.jar:. SimpleApplicationDemo");
      return null;
    }

// This URL specifies we are connecting with a local database.  The
// configuration file for the database is found at './ExampleDB.conf'
    String url = "jdbc:mckoi:local://ExampleDB.conf";

// The username/password for the database.  This is set when the database
// is created (see SimpleDatabaseCreateDemo).
    String username = "funzel";
    String password = "funzel";

// Make a connection with the database.
    Connection connection;
    try {
      connection = DriverManager.getConnection(url, username, password);
    } catch (SQLException e) {
      System.out.println(
          "Unable to make a connection to the database.\n" +
          "The reason: " + e.getMessage());
      return null;
    }
    return connection;
  }

  public static void close(Statement statement) {
    if (null != statement) {
      try {
        statement.close();
      } catch (SQLException e) {
      }
    }
  }
  public static void close(ResultSet result) {
    if (null != result) {
      try {
        result.close();
      } catch (SQLException e) {
      }
    }
  }
}
