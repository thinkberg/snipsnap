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

import org.snipsnap.cache.Cache;
import org.snipsnap.snip.Snip;
import org.snipsnap.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
  private Cache cache;
  private Loader loader;
  private Class type;
  private String keyName;
  private boolean caching = true;

  public Finder(String statement, Cache cache, Loader loader, boolean caching, String key) {
    this(statement, cache, loader, key);
    this.caching = caching;
  }

  public Finder(String statement, Cache cache, Loader loader, String key) {
    try {
      this.connection = ConnectionManager.getConnection();
      this.statementString = statement;
      this.statement = this.connection.prepareStatement(statement);
      this.loader = loader;
      this.type = loader.getLoaderType();
      this.keyName = key;
    } catch (SQLException e) {
      System.out.println("Unable to prepare statement: " + statementString);
    }
    this.cache = cache;
  }

  public void setDate(int column, Timestamp date) {
    try {
      statement.setTimestamp(column, date);
    } catch (SQLException e) {
      System.out.println("Unable to set Timestamp value: " + statementString);
    }
  }

  public void setString(int column, String value) {
    try {
      statement.setString(column, value);
    } catch (SQLException e) {
      System.out.println("Unable to set String value: " + statementString);
    }
  }

  public List execute() {
    return find(statement);
  }

  public List execute(int count) {
    return find(statement, count);
  }

  public List find(PreparedStatement statement) {
    return find(statement, Integer.MAX_VALUE, new ArrayList());
  }

  public List find(PreparedStatement statement, int count) {
    return find(statement, count, new ArrayList(count));
  }

  public List find(PreparedStatement statement, int count, List resultList) {
    ResultSet result = null;

    //System.err.println("execute("+statementString+")");
    try {
      result = statement.executeQuery();
      Object object = null;
      while (result.next() && count-- > 0) {
        String name = result.getString(this.keyName);
        object = cache.get(type, name);
        if (null == object) {
          object = loader.createObject(result);
          if (caching) {
            cache.put(type, name, object);
          }
        }

        resultList.add(object);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return resultList;
  }
}
