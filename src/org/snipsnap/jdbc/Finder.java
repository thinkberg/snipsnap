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

import org.radeox.util.logging.Logger;
import org.snipsnap.util.ConnectionManager;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Encapsulates finder select queries to the database
 *
 * @author stephan
 * @version $Id$
 */

public class Finder {
  private PreparedStatement statement;
  private String statementString;
  private Connection connection;

  public Finder(DataSource ds, String statement) {
    try {
      this.connection = ds.getConnection();
      this.statementString = statement;
      this.statement = this.connection.prepareStatement(statement);
    } catch (SQLException e) {
      Logger.warn("Unable to prepare statement: " + statementString);
    }
  }

  public Finder setDate(int column, Timestamp date) {
    try {
      statement.setTimestamp(column, date);
    } catch (SQLException e) {
      Logger.warn("Unable to set Timestamp value: " + statementString);
    }
    return this;
  }

  public Finder setString(int column, String value) {
    try {
      statement.setString(column, value);
    } catch (SQLException e) {
      Logger.warn("Unable to set String value: " + statementString);
    }
    return this;
  }

  public ResultSet execute() {
    try {
      return statement.executeQuery();
    } catch (SQLException e) {
      Logger.warn("Unable to execute Query "+statementString);
      return null;
    }
  }

  public void close() {
    ConnectionManager.close(statement);
    ConnectionManager.close(connection);
  }
}
