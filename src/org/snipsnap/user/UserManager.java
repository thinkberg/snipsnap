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

  private Connection connection;

  private UserManager() {
    connection = ConnectionManager.getConnection();
  }

  public User getUser(HttpServletRequest request) {
    HttpSession session = request.getSession(true);
    User user = (User)session.getAttribute("user");
    if(user == null) {
      Cookie cookie =  getCookie(request, "userName");
      if(cookie != null) {
        user = load(cookie.getValue());
      }
      if(user == null) {
        user = new User("Guest", "Guest", "");
      }
      session.setAttribute("user", user);
    }
    System.out.println("User found: "+user.getLogin());
    return user;
  }

  /**
   * Helper method for getUser to extract user from request/cookie/session
   * @param request
   * @param name
   * @return
   */
  private Cookie getCookie(HttpServletRequest request, String name) {
    Cookie cookies[] = request.getCookies();
    for(int i = 0; cookies != null && i < cookies.length; i++) {
      if(cookies[i].getName().equals(name)) {
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

  private User createUser(ResultSet result) throws SQLException {
    String login = result.getString("login");
    String passwd = result.getString("passwd");
    String email = result.getString("email");
    String status = result.getString("status");
    User user = new User(login, passwd, email);
    user.setStatus(status);
    return user;
  }

  private void storageStore(User user) {
    PreparedStatement statement = null;

    try {
      statement = connection.prepareStatement("UPDATE User SET login=?, passwd=?, email=?, status=?");
      statement.setString(1, user.getLogin());
      statement.setString(2, user.getPasswd());
      statement.setString(3, user.getEmail());
      statement.setString(3, user.getStatus());

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
    }
    return;
  }

  private User storageCreate(String login, String passwd, String email) {
    PreparedStatement statement = null;
    ResultSet result = null;

    User user = new User(login, passwd, email);

    try {
      statement = connection.prepareStatement("INSERT INTO User (login,passwd, email, status) VALUES (?,?,?,?)");
      statement.setString(1, login);
      statement.setString(2, passwd);
      statement.setString(3, email);
      statement.setString(4, "");

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
    }

    return user;
  }


  private void storageRemove(User user) {
    PreparedStatement statement = null;

    try {
      statement = connection.prepareStatement("DELETE FROM User WHERE login=?");
      statement.setString(1, user.getLogin());
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
    }
    return;
  }

  private User storageLoad(String login) {
    User user = null;
    PreparedStatement statement = null;
    ResultSet result = null;

    try {
      statement = connection.prepareStatement("SELECT login, passwd, email, status FROM User WHERE login=?");
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
    }
    return user;
  }

}
