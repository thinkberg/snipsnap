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
package com.neotis.util;

import com.bitmechanic.sql.ConnectionPoolManager;
import com.neotis.config.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

  public ConnectionManager() {
    ConnectionPoolManager mgr = null;
    try {
      mgr = new ConnectionPoolManager(300);
    } catch (SQLException e) {
      System.out.println("Unable to create ConnectionPool");
    }

    // Register the Mckoi JDBC Driver
    try {
      Class.forName("com.mckoi.JDBCDriver").newInstance();
    } catch (Exception e) {
      System.out.println(
        "Unable to register the JDBC Driver.\n" +
        "Make sure the classpath is correct.\n" +
        "For example on Win32;  java -cp ../../mckoidb.jar;. SimpleApplicationDemo\n" +
        "On Unix;  java -cp ../../mckoidb.jar:. SimpleApplicationDemo");
      return;
    }

    // This URL specifies we are connecting with a local database.  The
    // configuration file for the database is found at './ExampleDB.conf'
    String url = "jdbc:mckoi:local://conf/db.conf";

    // The username/password for the database.  This is set when the database
    // is created (see SimpleDatabaseCreateDemo).
    Configuration config = new Configuration("./conf/local.conf");

    String name = "snipsnap";

    try {
      mgr.addAlias(name, "com.mckoi.JDBCDriver",
                   url,
                   config.getUserName(), config.getPassword(),
                   10, // max connections to open
                   300, // seconds a connection can be idle before it is closed
                   120, // seconds a connection can be checked out by a thread
                   // before it is returned back to the pool
                   30, // number of times a connection can be re-used before
                   // connection to database is closed and re-opened
                   // (optional parameter)
                   false); // specifies whether to cache statements
    } catch (Exception e) {
      System.out.println("Unable to add connection alias.");
      e.printStackTrace();
    }
  }


  public Connection connection() {
    try {
      return DriverManager.getConnection(ConnectionPoolManager.URL_PREFIX +
              "snipsnap", null, null);
    } catch (SQLException e) {
      System.out.println("Unable to get connection.");
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
