package org.snipsnap.util;

import org.snipsnap.config.AppConfiguration;
import org.snipsnap.app.Application;
import org.snipsnap.snip.XMLSnipExport;
import org.radeox.util.logging.Logger;

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

/**
 * Dump current database contents. The database is read directly and meta-data
 * is used to detect xml tag names. First all users are dumped and then all
 * snips.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
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

    Application app = Application.get();
    app.setConfiguration(config);

    try {
      XMLSnipExport.store(System.out);
    } catch (IOException e) {
      Logger.warn("DBDump: unable to dump database", e);
    }
    System.exit(0);
  }
}
