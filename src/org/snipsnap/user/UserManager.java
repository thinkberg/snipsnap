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
package com.neotis.user;

import com.neotis.util.ConnectionManager;

import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * User manager handles all register, creation and authentication of users.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class UserManager {
  private static UserManager instance;

  public static synchronized UserManager getInstance() {
    if (null == instance) {
      instance = new UserManager();
    }
    return instance;
  }

  private Set ROLES = new HashSet();

  private UserManager() {
    ROLES.add("Editor");
    ROLES.add("NoComment");
  }

  public List getAll() {
    return storageAll();
  }

  public Set getAllRoles() {
    return ROLES;
  }

  public User getUser(HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    User user = (User) session.getAttribute("user");
    if (user == null) {
      Cookie cookie = getCookie(request, "userName");
      if (cookie != null && cookie.getMaxAge() > 0) {
        user = load(cookie.getValue());
      }
      if (user == null) {
        user = new User("Guest", "Guest", "");
      }
      session.setAttribute("user", user);
    }
    return user;
  }

  /**
   * Helper method for getUser to extract user from request/cookie/session
   * @param request
   * @param name
   * @return
   */
  public Cookie getCookie(HttpServletRequest request, String name) {
    Cookie cookies[] = request.getCookies();
    for (int i = 0; cookies != null && i < cookies.length; i++) {
      if (cookies[i].getName().equals(name)) {
        return cookies[i];
      }
    }
    return null;
  }

  public User authenticate(String login, String passwd) {
    User user = storageLoad(login);
    if (null != user && user.getPasswd().equals(passwd)) {
      return user;
    } else {
      return null;
    }
  }

  public boolean isAuthenticated(User user) {
    return user != null && !"Guest".equals(user.getLogin());
  }

  public User create(String login, String passwd, String email) {
    return storageCreate(login, passwd, email);
  }

  public void store(User user) {
    storageStore(user);
    return;
  }

  public void remove(User user) {
    storageRemove(user);
    return;
  }

  public User load(String login) {
    return storageLoad(login);
  }

  // Storage System dependend Methods

  private String serialize(Set roles) {
    if (null == roles || roles.isEmpty()) return "";

    StringBuffer buffer = new StringBuffer();
    Iterator iterator = roles.iterator();
    while (iterator.hasNext()) {
      String role = (String) iterator.next();
      buffer.append(role);
      if (iterator.hasNext()) buffer.append(":");
    }
    return buffer.toString();
  }

  private Set deserialize(String roleString) {
    if (null == roleString || "".equals(roleString)) return new HashSet();

    StringTokenizer st = new StringTokenizer(roleString, ":");
    Set roles = new HashSet();

    while (st.hasMoreTokens()) {
      roles.add(st.nextToken());
    }

    return roles;
  }

  private User createUser(ResultSet result) throws SQLException {
    String login = result.getString("login");
    String passwd = result.getString("passwd");
    String email = result.getString("email");
    String status = result.getString("status");
    Set roles = deserialize(result.getString("roles"));
    User user = new User(login, passwd, email);
    user.setStatus(status);
    user.setRoles(roles);
    return user;
  }

  private void storageStore(User user) {
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("UPDATE User SET login=?, passwd=?, email=?, status=?, roles=?");
      statement.setString(1, user.getLogin());
      statement.setString(2, user.getPasswd());
      statement.setString(3, user.getEmail());
      statement.setString(3, user.getStatus());
      statement.setString(4, serialize(user.getRoles()));

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return;
  }

  private User storageCreate(String login, String passwd, String email) {
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();

    User user = new User(login, passwd, email);

    try {
      statement = connection.prepareStatement("INSERT INTO User (login,passwd, email, status, roles) VALUES (?,?,?,?,?)");
      statement.setString(1, login);
      statement.setString(2, passwd);
      statement.setString(3, email);
      statement.setString(4, "");
      statement.setString(5, "");

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }

    return user;
  }


  private void storageRemove(User user) {
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("DELETE FROM User WHERE login=?");
      statement.setString(1, user.getLogin());
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return;
  }

  private User storageLoad(String login) {
    User user = null;
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("SELECT login, passwd, email, status, roles FROM User WHERE login=?");
      statement.setString(1, login);

      result = statement.executeQuery();
      if (result.next()) {
        user = createUser(result);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return user;
  }

  private List storageAll() {
    List users = new ArrayList();

    ResultSet result = null;
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("SELECT login, passwd, email, status, roles FROM User "+
                                              " ORDER BY login");

      result = statement.executeQuery();
      User user = null;
      while (result.next()) {
        user = createUser(result);
        users.add(user);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }

    return users;
  }

}
