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

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.radeox.util.logging.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * The connection manager handles all database connections.
 *
 * @author Stephan J. Schmidt, Matthias L. Jugel
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

  public static synchronized void removeInstance() {
    if(null != instance) {
      instance = null;
    }
  }

  private DataSource dataSource = null;

  private ConnectionManager() {
  }

  private void update(Configuration config) {
    if (null == dataSource) {
      try {
        System.err.println("ConnectionManager: Registering JDBC driver: " + config.getJdbcDriver());
        Class.forName(config.getJdbcDriver());
      } catch (Exception e) {
        Logger.fatal("unable to register JDBC driver: " + config.getJdbcDriver(), e);
      }

      String jdbcUrl = config.getJdbcUrl();
      if(jdbcUrl.indexOf("?") != -1) {
        jdbcUrl = jdbcUrl.concat("&");
      } else {
        jdbcUrl = jdbcUrl.concat("?");
      }
      String jdbcPassword = config.getJdbcPassword();
      if (null == jdbcPassword) {
        jdbcPassword = "";
      }
      jdbcUrl = jdbcUrl.concat("user="+config.getJdbcUser()).concat("&password="+jdbcPassword);
      //System.err.println("ConnectionManager: using: "+ jdbcUrl);
      ObjectPool connectionPool = new GenericObjectPool(null);
      ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(jdbcUrl, config.getJdbcUser(), jdbcPassword);
      PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, connectionPool, null, null, false, true);
      dataSource = new PoolingDataSource(connectionPool);
    }
  }

  private Connection connection() {
    update(Application.get().getConfiguration());

    try {
      return dataSource.getConnection();
    } catch (Exception e) {
      Logger.fatal("unable to get connection: ", e);
      return null;
    }
  }

  private DataSource dataSource() {
    update(Application.get().getConfiguration());

    return dataSource;
  }

  public static DataSource getDataSource() {
    return getInstance().dataSource();
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
