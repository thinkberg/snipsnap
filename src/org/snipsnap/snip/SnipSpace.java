package com.neotis.snip;

import java.util.HashMap;
import java.sql.*;

public class SnipSpace {
  private Connection connection;

  private static SnipSpace instance;

  public static synchronized SnipSpace getInstance() {
    if (null == instance) {
      instance = new SnipSpace();
    }
    return instance;
  }

  private Connection getConnection() {
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

  private SnipSpace() {
    connection = getConnection();
  }

  public Snip load(String name) {
    Snip snip = null;

    try {
      Statement statement = connection.createStatement();
      ResultSet result;

      result = statement.executeQuery("SELECT name, content FROM Snip");
      if (result.next()) {
        snip = new Snip(result.getString("name"), result.getString("content"));
      }

    } catch (SQLException e) {
    }
    return snip;
  }

  public void store(Snip snip) {
  }

  public Snip create() {
    return null;
  }

  public void remove(Snip snip) {

  }
}
