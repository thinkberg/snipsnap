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
package org.snipsnap.user;

import org.snipsnap.util.ConnectionManager;
import org.snipsnap.util.log.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

  public List getAll() {
    return storageAll();
  }

  private final static String COOKIE_NAME = "SnipSnapUser";
  private final static String ATT_USER = "user";
  private final static int SECONDS_PER_YEAR = 60 * 60 * 24 * 365;

  private MessageDigest digest;
  private Map authHash = new HashMap();
  private List delayed;

  protected UserManager() {
    try {
      digest =  MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      System.err.println("UserManager: unable to load digest algorithm: "+e);
      digest = null;
    }

    delayed = new LinkedList();
    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      public void run() {
        synchronized (delayed) {
          ListIterator iterator = delayed.listIterator();
          while (iterator.hasNext()) {
            User user = (User) iterator.next();
            systemStore(user);
            iterator.remove();
          }
        }
      }
      // execute after 5 minutes and then
      // every 5 minutes
    }, 5 * 60 * 1000, 5 * 60 * 1000);
  }

  private String getDigest(User user) {
    // create a string representation of the MD5 hash of current user
    StringBuffer md5hex = new StringBuffer();
    if (digest != null) {
      String tmp = user.getLogin() + user.getPasswd() + user.getLastLogin().toString();
      // System.out.println("encoding: "+tmp);
      byte buf[] = digest.digest(tmp.getBytes());
      md5hex.setLength(0);
      for (int i = 0; i < buf.length; i++) {
        md5hex.append(Integer.toHexString(buf[i]).toUpperCase());
      }
    }
    // System.out.println("md5hash: "+md5hex.toString());
    return md5hex.toString();
  }

  // update the auth hash by removing all entries and updating from the database
  private void updateAuthHash() {
    authHash.clear();
    Iterator users = getAll().iterator();
    while (users.hasNext()) {
      User user = (User) users.next();
      authHash.put(getDigest(user), user);
    }
  }

  /**
   * Get user from session or cookie.
   */
  public User getUser(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession(true);
    User user = (User) session.getAttribute(ATT_USER);
    if (null == user) {
      Cookie cookie = getCookie(request, COOKIE_NAME);
      if (cookie != null) {
        String auth = cookie.getValue();
        if (!authHash.containsKey(auth)) {
          updateAuthHash();
        }

        user = (User) authHash.get(auth);
        if (user != null) {
          // System.err.println("UserManager: valid hash: "+auth);
          user = authenticate(user.getLogin(), user.getPasswd());
          setCookie(request, response, user);
        } else {
          System.err.println("UserManager: invalid hash: "+auth);
        }
      }

      if (null == user) {
        user = new User("Guest", "Guest", "");
        removeCookie(request, response);
      }
      session.setAttribute(ATT_USER, user);
    }
    return user;
  }


  /**
   * Set cookie with has of encoded user/pass and last login time.
   */
  public void setCookie(HttpServletRequest request, HttpServletResponse response, User user) {
    String auth = getDigest(user);
    // @TODO find better solution by removing by value
    updateAuthHash();

    authHash.put(auth, user);
    Cookie cookie = new Cookie(COOKIE_NAME, auth);
    cookie.setMaxAge(SECONDS_PER_YEAR);
    cookie.setPath("/");
    cookie.setComment("SnipSnap User");
    response.addCookie(cookie);
  }

  public void removeCookie(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = getCookie(request, COOKIE_NAME);
    if (cookie != null) {
      cookie.setMaxAge(0);
      cookie.setPath("/");
      cookie.setComment("SnipSnap User");
      response.addCookie(cookie);
    }
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
/*
      System.out.println("Cookie: [" + name + "] " +
                         cookies[i].getName() + "/" +
                         cookies[i].getPath() + "/" +
                         cookies[i].getValue() + " " +
                         cookies[i].getMaxAge() + "s");
*/
      if (cookies[i].getName().equals(name)) {
        return cookies[i];
      }
    }
    return null;
  }

  public User authenticate(String login, String passwd) {
    User user = storageLoad(login);
    if (null != user && user.getPasswd().equals(passwd)) {
      user.lastLogin();
      storageStore(user);
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
    user.setMTime(new Timestamp(new java.util.Date().getTime()));
    storageStore(user);
    return;
  }

  public void delayedStrore(User user) {
    synchronized (delayed) {
      if (!delayed.contains(user)) {
        delayed.add(user);
      }
    }
  }

  public void systemStore(User user) {
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
    Timestamp cTime = result.getTimestamp("cTime");
    Timestamp mTime = result.getTimestamp("mTime");
    Timestamp lastLogin = result.getTimestamp("lastLogin");
    Timestamp lastAccess = result.getTimestamp("lastAccess");
    String status = result.getString("status");
    User user = new User(login, passwd, email);
    user.setStatus(status);
    user.setRoles(new Roles(result.getString("roles")));
    user.setCTime(cTime);
    user.setMTime(mTime);
    user.setLastLogin(lastLogin);
    user.setLastAccess(lastAccess);
    return user;
  }

  private void storageStore(User user) {
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("UPDATE SnipUser SET login=?, passwd=?, email=?, status=?, roles=?, " +
                                              " cTime=?, mTime=?, lastLogin=?, lastAccess=? " +
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
      statement.setString(10, user.getLogin());

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
    Timestamp cTime = new Timestamp(new java.util.Date().getTime());
    user.setCTime(cTime);
    user.setMTime(cTime);
    user.setLastLogin(cTime);
    user.setLastAccess(cTime);

    try {
      statement = connection.prepareStatement("INSERT INTO SnipUser " +
                                              " (login, passwd, email, status, roles, " +
                                              " cTime, mTime, lastLogin, lastAccess) " +
                                              " VALUES (?,?,?,?,?,?,?,?,?)");
      statement.setString(1, login);
      statement.setString(2, passwd);
      statement.setString(3, email);
      statement.setString(4, "");
      statement.setString(5, "");
      statement.setTimestamp(6, cTime);
      statement.setTimestamp(7, cTime);
      statement.setTimestamp(8, cTime);
      statement.setTimestamp(9, cTime);

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
      statement = connection.prepareStatement("DELETE FROM SnipUser WHERE login=?");
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
      statement = connection.prepareStatement("SELECT login, passwd, email, status, roles, cTime, mTime, lastLogin, lastAccess " +
                                              " FROM SnipUser " +
                                              " WHERE login=?");
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
      statement = connection.prepareStatement("SELECT login, passwd, email, status, roles, cTime, mTime, lastLogin, lastAccess " +
                                              " FROM SnipUser " +
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
