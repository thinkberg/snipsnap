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
    String url = "jdbc:mckoi:local://conf/db.conf";

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
