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

import org.radeox.util.logging.Logger;
import org.snipsnap.jdbc.Finder;
import org.snipsnap.jdbc.FinderFactory;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC SnipStorage backend for user data
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class JDBCUserStorage implements UserStorage {
  private FinderFactory finders;

  public JDBCUserStorage() {
    finders = new FinderFactory("SELECT login, passwd, email, status, roles, " +
        " cTime, mTime, lastLogin, lastAccess, lastLogout " +
        " FROM SnipUser ");
  }

  public static void createStorage() {
    Connection connection = ConnectionManager.getConnection();
    try {
      Statement statement = connection.createStatement();
      System.out.println("JDBCUserStorage: creating user SQL tables");
      statement.executeQuery(
          "    CREATE TABLE SnipUser ( " +
          "       cTime      TIMESTAMP, " +
          "       mTime      TIMESTAMP, " +
          "       lastLogin  TIMESTAMP, " +
          "       lastAccess TIMESTAMP, " +
          "       lastLogout TIMESTAMP, " +
          "       login      VARCHAR(100) NOT NULL, " +
          "       passwd     VARCHAR(100), " +
          "       email      VARCHAR(100)," +
          "       status     VARCHAR(50), " +
          "       roles      VARCHAR(200) )");

      // Close the statement and the connection.
      statement.close();
    } catch (SQLException e) {
      System.out.println(
          "An error occured\n" +
          "The SQLException message is: " + e.getMessage());
    } finally {
      try {
        connection.close();
      } catch (SQLException e2) {
        e2.printStackTrace(System.err);
      }
    }
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
      Logger.warn("JDBCUserStorage: unable to get store user " + user.getLogin(), e);
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
      statement.setString(1, user.getLogin());
      statement.setString(2, user.getPasswd());
      statement.setString(3, user.getEmail());
      statement.setString(4, "");
      statement.setString(5, "");
      statement.setTimestamp(6, cTime);
      statement.setTimestamp(7, cTime);
      statement.setTimestamp(8, cTime);
      statement.setTimestamp(9, cTime);
      statement.setTimestamp(10, cTime);

      statement.execute();
    } catch (SQLException e) {
      Logger.warn("JDBCUserStorage: unable to get create user " + login, e);
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
      Logger.warn("JDBCUserStorage: unable to get remove user " + user.getLogin(), e);
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
      Logger.warn("JDBCUserStorage: unable to get load user " + login, e);
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return user;
  }

  public List storageAll() {
    Finder finder = finders.getFinder(" ORDER BY login");
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }


  public List createObjects(ResultSet result) {
    return createObjects(result, Integer.MAX_VALUE);
  }

  public List createObjects(ResultSet result, int size) {
    List resultList = new ArrayList();
    if (null != result) {
      try {
        User user = null;
        while (result.next() && size-- > 0) {
          user = createUser(result);
          resultList.add(user);
        }
      } catch (SQLException e) {
        Logger.warn("Finder: SQL Error", e);
      }
    }
    return resultList;
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
}
