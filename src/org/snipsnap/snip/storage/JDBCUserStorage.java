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

package org.snipsnap.snip.storage;

import org.snipsnap.cache.Cache;
import org.snipsnap.jdbc.Finder;
import org.snipsnap.jdbc.FinderFactory;
import org.snipsnap.jdbc.JDBCCreator;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.util.ConnectionManager;
import org.radeox.util.logging.Logger;

import java.sql.*;
import java.util.List;

/**
 * JDBC SnipStorage backend for user data
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class JDBCUserStorage implements UserStorage, JDBCCreator {
  private FinderFactory finders;

  public JDBCUserStorage(Cache cache) {
    finders = new FinderFactory("SELECT login, passwd, email, status, roles, " +
        " cTime, mTime, lastLogin, lastAccess, lastLogout " +
        " FROM SnipUser ", cache, User.class, "login", this);
  }

  public Object createObject(ResultSet result) throws SQLException {
    return createUser(result);
  }

  public User createUser(ResultSet result) throws SQLException {
    String login = result.getString("login");
    String passwd = result.getString("passwd");
    String email = result.getString("email");
    Timestamp cTime = result.getTimestamp("cTime");
    Timestamp mTime = result.getTimestamp("mTime");
    Timestamp lastLogin = result.getTimestamp("lastLogin");
    Timestamp lastAccess = result.getTimestamp("lastAccess");
    Timestamp lastLogout = result.getTimestamp("lastLogout");
    String status = result.getString("status");
    User user = new User(login, passwd, email);
    user.setStatus(status);
    user.setRoles(new Roles(result.getString("roles")));
    user.setCTime(cTime);
    user.setMTime(mTime);
    user.setLastLogin(lastLogin);
    user.setLastAccess(lastAccess);
    user.setLastLogout(lastLogout);
    return user;
  }

  public void storageStore(User user) {
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("UPDATE SnipUser SET login=?, passwd=?, email=?, status=?, roles=?, " +
          " cTime=?, mTime=?, lastLogin=?, lastAccess=?, lastLogout=? " +
          " WHERE login=?");

      statement.setString(1, user.getLogin());
      statement.setString(2, user.getPasswd());
      statement.setString(3, user.getEmail());
      statement.setString(4, user.getStatus());
      statement.setString(5, user.getRoles().toString());
      statement.setTimestamp(6, user.getCTime());
      statement.setTimestamp(7, user.getMTime());
      statement.setTimestamp(8, user.getLastLogin());
      statement.setTimestamp(9, user.getLastAccess());
      statement.setTimestamp(10, user.getLastLogout());
      statement.setString(11, user.getLogin());

      statement.execute();
    } catch (SQLException e) {
      Logger.warn("JDBCUserStorage: unable to get store user "+user.getLogin(), e);
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return;
  }

  public User storageCreate(String login, String passwd, String email) {
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();

    User user = new User(login, passwd, email);
    Timestamp cTime = new Timestamp(new java.util.Date().getTime());
    user.setCTime(cTime);
    user.setMTime(cTime);
    user.setLastLogin(cTime);
    user.setLastAccess(cTime);
    user.setLastLogout(cTime);

    try {
      statement = connection.prepareStatement("INSERT INTO SnipUser " +
          " (login, passwd, email, status, roles, " +
          " cTime, mTime, lastLogin, lastAccess, lastLogout) " +
          " VALUES (?,?,?,?,?,?,?,?,?,?)");
      statement.setString(1, login);
      statement.setString(2, passwd);
      statement.setString(3, email);
      statement.setString(4, "");
      statement.setString(5, "");
      statement.setTimestamp(6, cTime);
      statement.setTimestamp(7, cTime);
      statement.setTimestamp(8, cTime);
      statement.setTimestamp(9, cTime);
      statement.setTimestamp(10, cTime);

      statement.execute();
    } catch (SQLException e) {
      Logger.warn("JDBCUserStorage: unable to get create user "+login, e);
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }

    return user;
  }


  public void storageRemove(User user) {
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("DELETE FROM SnipUser WHERE login=?");
      statement.setString(1, user.getLogin());
      statement.execute();
    } catch (SQLException e) {
      Logger.warn("JDBCUserStorage: unable to get remove user "+user.getLogin(), e);
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return;
  }

  public int storageUserCount() {
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();
    int count = -1;

    try {
      statement = connection.prepareStatement("SELECT count(*) FROM SnipUser");
      result = statement.executeQuery();
      if (result.next()) {
        count = result.getInt(1);
      }
    } catch (SQLException e) {
      Logger.warn("JDBCUserStorage: unable to get user count", e);
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return count;
  }

  public User storageLoad(String login) {
    Logger.debug("storageLoad() User=" + login);
    User user = null;
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("SELECT login, passwd, email, status, roles, cTime, mTime, lastLogin, lastAccess, lastLogout " +
          " FROM SnipUser " +
          " WHERE login=?");
      statement.setString(1, login);

      result = statement.executeQuery();
      if (result.next()) {
        user = createUser(result);
      }
    } catch (SQLException e) {
      Logger.warn("JDBCUserStorage: unable to get load user "+login,e);
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return user;
  }

  public List storageAll() {
    Finder finder = finders.getFinder(" ORDER BY login");
    return finder.execute();
  }

  public Object loadObject(String name) {
    return storageLoad(name);
  }
}
