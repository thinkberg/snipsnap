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

import com.bitmechanic.sql.ConnectionPoolManager;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;

/**
 * The connection manager handles all database connections.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class ConnectionManager {
  private static ConnectionManager instance;

  public static ConnectionManager getInstance() {
    if (null == instance) {
      instance = new ConnectionManager();
    }
    return instance;
  }

  private ConnectionPoolManager mgr = null;

  public ConnectionManager() {
    try {
      mgr = new ConnectionPoolManager(300);
    } catch (SQLException e) {
      System.out.println("Unable to create ConnectionPool");
    }
  }

  private Set names = new HashSet();

  private void update(Configuration config) {
    if (!names.contains(config.getName())) {
      try {
        Class.forName(config.getJdbcDriver());
      } catch (Exception e) {
        System.out.println("Unable to register the JDBC Driver: " + config.getJdbcDriver());
        return;
      }

      // This URL specifies we are connecting with a local database.  The
      // configuration file for the database is found at './ExampleDB.conf'
      //String url = "jdbc:mckoi:local://./conf/db.conf";
      String url = config.getJdbcUrl();
      String name = config.getName();

      try {
        mgr.addAlias(name, config.getJdbcDriver(),
                     url,
                     config.getJdbcUser(),
                     config.getJdbcPassword(),
                     10, // max connections to open
                     300, // seconds a connection can be idle before it is closed
                     120, // seconds a connection can be checked out by a thread
                     // before it is returned back to the pool
                     30, // number of times a connection can be re-used before
                     // connection to database is closed and re-opened
                     // (optional parameter)
                     false); // specifies whether to cache statements
        names.add(name);
      } catch (Exception e) {
        Logger.warn("Unable to add connection alias for '"+config.getName()+"'", e);
      }
    }
  }

  private Connection connection() {
    Application app = Application.get();
    Configuration config = app.getConfiguration();
    String appName = config.getName();
    // make sure a pool exists for this config
    update(config);
    try {
      return DriverManager.getConnection(ConnectionPoolManager.URL_PREFIX + appName);
    } catch (SQLException e) {
      Logger.warn("Unable to get connection for application '" + appName + "'", e);
      return null;
    }
  }

  public static Connection getConnection() {
    return getInstance().connection();
  }

  public static void close(Connection conn) {
    if (null != conn) {
      try {
        conn.close();
      } catch (SQLException e) {
        // We can't do anything
      }
    }
  }

  public static void close(Statement statement) {
    if (null != statement) {
      try {
        statement.close();
      } catch (SQLException e) {
        // We can't do anything
      }
    }
  }

  public static void close(ResultSet result) {
    if (null != result) {
      try {
        result.close();
      } catch (SQLException e) {
        // We can't do anything
      }
    }
  }
}
