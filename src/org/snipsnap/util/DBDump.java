package org.snipsnap.util;

import java.sql.*;
import java.util.Properties;

public class DBDump {

  public static void main(String[] args) {

    Connection connection;

    String URL = "jdbc:mckoi:local://./applications/SnipSnap/db.conf";

    String username = args[0];
    String password = args[1];

    String driverClass = "com.mckoi.JDBCDriver";
    try {
      System.err.println("Loading JDBC Driver: " + driverClass);
      Class.forName(driverClass);
    } catch (Exception e) {
      System.err.println("Failed to load JDBC driver:" + driverClass);
      e.printStackTrace();
      return;
    }

    Properties props = new Properties();
    System.err.println("Setting user: " + username);
    System.err.println("Setting password: " + password);

    props.put("user", username);
    props.put("password", password);

    try {
      System.err.println("Connecting to: " + URL);
      connection = DriverManager.getConnection(URL, props);

    } catch (Exception e) {
      System.err.println("Problems connecting to " + URL);
      e.printStackTrace();
      return;
    }

    ResultSet results = null;

    System.out.println("<snipspace");
    toXml("Snip", "snip", connection);
    toXml("User", "user", connection);

    System.out.println("</snipspace>");
  }

  private static void toXml(String table, String export, Connection connection) {
    ResultSet results;
    try {
      PreparedStatement prepStmt = connection.prepareStatement("SELECT * " +
                                                               " FROM "+table);
      results = prepStmt.executeQuery();
      toXml(export, results);
    } catch (SQLException e) {
      System.err.println("Problems with query ");
      e.printStackTrace();
    }
  }

  private static void toXml(String objectName, ResultSet results) throws SQLException {
    ResultSetMetaData meta = results.getMetaData();
    int size = meta.getColumnCount();
    while (results.next()) {
      System.out.println("<"+objectName+">");
      for (int i = 1; i <= size; i++) {
        Object object = results.getObject(i);
        String name = meta.getColumnName(i);
        if (null != object) {
          System.out.print("  <" + name + ">");
          if (object instanceof Timestamp) {
            Timestamp time = (Timestamp) object;
            System.out.print(time.getTime());
          } else {
            System.out.print(object.toString());
          }
          System.out.println("</" + name + ">");
        }
      }
      System.out.println("<"+objectName+">");
    }
  }
}
