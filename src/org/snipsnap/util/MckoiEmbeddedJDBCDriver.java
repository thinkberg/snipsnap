/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */
package org.snipsnap.util;

import com.mckoi.database.control.DBController;
import com.mckoi.database.control.DBSystem;
import com.mckoi.database.control.DefaultDBConfig;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Iterator;

/**
 * Adaption of the Mckoi JDBC Driver for embedded-only database drivers.
 * Created with help from toby@mckoi.com
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class MckoiEmbeddedJDBCDriver implements Driver {
  private static MckoiEmbeddedJDBCDriver driver = null;

  static {
    register();
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        try {
          deregister();
        } catch (SQLException e) {
          System.err.println("MckoiEmbeddedJDBCDriver: unable to deregister driver: " + e);
          e.printStackTrace();
        }
      }
    });
  }

  public static void register() {
    if (null == driver) {
      System.err.println("Registering JDBC Driver");
      try {
        java.sql.DriverManager.registerDriver(driver = new MckoiEmbeddedJDBCDriver());
      } catch (SQLException e) {
        System.err.println("MckoiEmbeddedJDBCDriver: unable to register driver: " + e);
      }
    }
  }

  public static void deregister() throws SQLException {
    System.err.println("Deregistering JDBC Driver");
    Iterator it = databases.values().iterator();
    while (it.hasNext()) {
      ((DBSystem) it.next()).close();
    }
    databases.clear();

    java.sql.DriverManager.deregisterDriver(driver);
    driver = null;
  }

  // we accept embedded (local) databases only
  public final static String MCKOI_PREFIX = "jdbc:mckoi:local://";

  // map of all locally existing databases
  private static Map databases = new HashMap();

  /**
   * Check whether a database for this URL exists and return it or create/start the database
   * @param url JDBC URL
   * @param info properties object
   * @return the database
   * @throws IOException
   */
  private static DBSystem getDatabase(String url, Properties info) throws IOException {
    int varStart = url.indexOf('?');
    String vars = "";
    if (varStart != -1) {
      vars = url.substring(varStart + 1);
      url = url.substring(0, varStart);
    }

    if (databases.get(url) == null) {
      String file = url.substring(MCKOI_PREFIX.length());
      File configFile = new File(file);
      DefaultDBConfig config = new DefaultDBConfig(configFile.getParentFile());
      config.loadFromFile(new File(file));
      info = parseUrl(vars, info);
      // get DB Controller
      DBController controller = DBController.getDefault();
      DBSystem dbsystem = null;
      if (!controller.databaseExists(config) && "true".equals(info.getProperty("create"))) {
        dbsystem = controller.createDatabase(config, info.getProperty("user", ""), info.getProperty("password", ""));
      } else {
        dbsystem = controller.startDatabase(config);
      }
      databases.put(url, dbsystem);
    }
    return (DBSystem) databases.get(url);
  }


  /**
   * Taken from com.mckoi.database.jdbc.MDriver
   * @param vars variables from url
   * @param info an initial properties object
   * @return the new updated properties
   */
  private static Properties parseUrl(String vars, Properties info) {
    if (vars != null && vars.length() > 0) {
      // Parse the url variables.
      StringTokenizer tok = new StringTokenizer(vars, "&");
      while (tok.hasMoreTokens()) {
        String token = tok.nextToken().trim().toLowerCase();
        int split_point = token.indexOf("=");
        if (split_point > 0) {
          String key = token.substring(0, split_point);
          String value = token.substring(split_point + 1);
          // Put the key/value pair in the 'info' object.
          info.put(key, value);
        } else {
          System.err.println("Ignoring url variable: '" + token + "'");
        }
      } // while (tok.hasMoreTokens())
    }
    return info;
  }

  public Connection connect(String url, Properties info)
          throws SQLException {
    if (acceptsURL(url)) {
      try {
        DBSystem dbsystem = MckoiEmbeddedJDBCDriver.getDatabase(url, info);
        return dbsystem.getConnection(info.getProperty("user", ""), info.getProperty("password", ""));
      } catch (IOException e) {
        System.err.println("MckoiEmbeddedJDBCDriver: unable to get connection: " + e);
        throw new SQLException("MckoiEmbeddedJDBCDriver: unable to get connection: " + e);
      }
    }
    return null;
  }

  public boolean acceptsURL(String url) throws SQLException {
    return url.startsWith(MCKOI_PREFIX);
  }

  /**
   * Ignore for now, just like MDriver from Mckoi
   */
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info)
          throws SQLException {
    return new DriverPropertyInfo[0];
  }

  public int getMajorVersion() {
    return 0;
  }

  public int getMinorVersion() {
    return 1;
  }

  /**
   * We are not officially certified ...
   * @return false
   */
  public boolean jdbcCompliant() {
    return false;
  }
}
