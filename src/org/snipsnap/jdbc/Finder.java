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

package com.neotis.jdbc;

import com.neotis.cache.Cache;
import com.neotis.snip.Snip;
import com.neotis.util.ConnectionManager;

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
  PreparedStatement statement;
  Connection connection;
  Cache cache;
  Loader loader;
  private boolean caching = true;

  public Finder(String statement, Cache cache, Loader loader, boolean caching) {
    this(statement, cache, loader);
    this.caching = caching;
  }

  public Finder(String statement, Cache cache, Loader loader) {
    try {
      this.connection = ConnectionManager.getConnection();
      this.statement = this.connection.prepareStatement(statement);
      this.loader = loader;
    } catch (SQLException e) {
      System.out.println("Unable to prepare statement.");
    }
    this.cache = cache;
  }

  public void setString(int column, String value) {
    try {
      statement.setString(column, value);
    } catch (SQLException e) {
      System.out.println("Unable to set String value.");
    }
  }

  public List execute() {
    return find(statement);
  }

  public List execute(int count) {
    return find(statement, count);
  }


  public List find(PreparedStatement statement) {
    return find(statement, Integer.MAX_VALUE);
  }

  public List find(PreparedStatement statement, int count) {
    ResultSet result = null;
    List snips = new ArrayList();

    try {
      result = statement.executeQuery();
      Snip snip = null;
      while (result.next() && count-- > 0) {
        String name = result.getString("name");
        snip = cache.get(name);
        if (null == snip) {
          snip = loader.createSnip(result);
          if (caching) {
            cache.put(name, snip);
          }
        }
        snips.add(snip);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return snips;
  }
}
