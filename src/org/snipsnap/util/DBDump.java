package org.snipsnap.util;

import org.snipsnap.config.AppConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.StringTokenizer;

public class DBDump {

  public static void main(String[] args) {

    Connection connection;
    AppConfiguration config = null;

    if (args.length > 0 && "-config".equals(args[0])) {
      if (args.length > 1) {
        try {
          config = new AppConfiguration(new File(args[1]));
        } catch (IOException e) {
          System.err.println("DBDump: unable to read configuration file: " + e);
          System.exit(-1);
        }
      }
    }


    if (args.length > 0 && "-db".equals(args[0])) {
      config = new AppConfiguration();
      config.setJDBCDriver("org.snipsnap.util.MckoiEmbeddedJDBCDriver");
      config.setJDBCURL("jdbc:mckoi:local://" + args[1]);
      config.setAdminLogin(args[2]);
      config.setAdminPassword(args[3]);
    }

    if (config == null) {
      System.err.println("usage: DBDump [-config file] [-db dbconf user pass]");
      System.exit(-1);
    }

    String URL = config.getJDBCURL();

    String username = config.getAdminLogin();
    String password = config.getAdminPassword();

    String driverClass = config.getJDBCDriver();
    try {
      System.err.println("Loading JDBC Driver: " + driverClass);
      Class.forName(driverClass);
    } catch (Exception e) {
      System.err.println("Failed to load JDBC driver:" + driverClass);
      e.printStackTrace();
      return;
    }

    Properties props = new Properties();

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

    try {
      PrintWriter out = new PrintWriter(System.out);
      out.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
      out.println("<snipspace>");

      toXml("User", "user", connection, out);
      toXml("Snip", "snip", connection, out);

      out.println("</snipspace>");
    } catch (Exception e) {
      System.err.println("Error opening out.wiki");
    }
    System.err.println("ATTENTION: Check the encoding of the file!");
  }

  private static void toXml(String table, String export, Connection connection, PrintWriter out) {
    ResultSet results;
    try {
      PreparedStatement prepStmt = connection.prepareStatement("SELECT * " +
                                                               " FROM " + table);
      results = prepStmt.executeQuery();
      toXml(export, results, out);
    } catch (SQLException e) {
      System.err.println("Problems with query ");
      e.printStackTrace();
    }
  }

  private static void toXml(String objectName, ResultSet results, PrintWriter out) throws SQLException {
    ResultSetMetaData meta = results.getMetaData();
    int size = meta.getColumnCount();
    while (results.next()) {
      out.println("<" + objectName + ">");
      for (int i = 1; i <= size; i++) {
        Object object = null;
        String value = null;
        try {
          value = results.getString(i);
        } catch (Exception e) {
          object = results.getObject(i);
        }
        String name = meta.getColumnName(i);
        if (null != value || null != object) {
          out.print("  <" + name + ">");
          if (object instanceof Timestamp) {
            Timestamp time = (Timestamp) object;
            out.print(time.getTime());
          } else {
            out.print(value);
          }
          out.println("</" + name + ">");
        }
      }
      out.println("</" + objectName + ">");
    }
  }

  private static String escape(String in) {
    StringBuffer out = new StringBuffer();
    StringTokenizer t = new StringTokenizer(in, "<>&", true);
    while (t.hasMoreTokens()) {
      String token = t.nextToken();
      if ("<".equals(token) || ">".equals(token) || "&".equals(token)) {
        out.append("&#x").append(Integer.toHexString(token.charAt(0))).append(";");
      } else {
        out.append(token);
      }
    }
    return out.toString();
  }
}
