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

package org.snipsnap.jdbc;

import org.snipsnap.util.ConnectionManager;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Wraps a datasource to get lazy initialization
 *
 * @author stephan
 * @version $Id$
 */

public class LazyDataSource implements DataSource {
  private DataSource ds;

  private DataSource getDataSource() {
    if (null == ds) {
      ds = ConnectionManager.getDataSource();
    }
    return ds;
  }

  public Connection getConnection() throws SQLException {
    return getDataSource().getConnection();
  }

  public Connection getConnection(String username, String password)
      throws SQLException {
    return getDataSource().getConnection(username, password);
  }

  public PrintWriter getLogWriter() throws SQLException {
    return getDataSource().getLogWriter();
  }

  public void setLogWriter(PrintWriter out) throws SQLException {
    getDataSource().setLogWriter(out);
  }

  public void setLoginTimeout(int seconds) throws SQLException {
    getDataSource().setLoginTimeout(seconds);
  }

  public int getLoginTimeout() throws SQLException {
    return getDataSource().getLoginTimeout();
  }

}
