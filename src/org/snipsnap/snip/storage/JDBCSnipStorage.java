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
import org.snipsnap.app.Application;
import org.snipsnap.interceptor.Aspects;
import org.snipsnap.jdbc.Finder;
import org.snipsnap.jdbc.FinderFactory;
import org.snipsnap.snip.Links;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipFactory;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.Permissions;
import org.snipsnap.util.ConnectionManager;
import org.snipsnap.util.log.SQLLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SnipStorage backend that uses JDBC for persisting data
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class JDBCSnipStorage implements SnipStorage, CacheableStorage {
  private FinderFactory finders;
  private Map cache = new HashMap();

  public static void createStorage() {
    Connection connection = ConnectionManager.getConnection();
    try {
      // Create a Statement object to execute the queries on,
      Statement statement = connection.createStatement();
      System.err.println("JDBCSnipStorage: creating SQL tables");

      // Create a Person table,
      statement.executeQuery(
          "    CREATE TABLE Snip ( " +
          "       name        VARCHAR(100) NOT NULL, " +
          "       content     TEXT, " +
          "       cTime       TIMESTAMP, " +
          "       mTime       TIMESTAMP, " +
          "       cUser       VARCHAR(55), " +
          "       mUser       VARCHAR(55), " +
          "       oUser       VARCHAR(55), " +
          "       parentSnip  VARCHAR(100), " +
          "       commentSnip VARCHAR(100), " +
          "       backLinks   TEXT, " +
          "       snipLinks   TEXT, " +
          "       labels      TEXT, " +
          "       attachments TEXT, " +
          "       viewCount   INTEGER, " +
          "       permissions VARCHAR(200) )");
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

  public JDBCSnipStorage() {
    this.finders = new FinderFactory("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions, " +
        " oUser, backLinks, snipLinks, labels, attachments, viewCount " +
        " FROM Snip ");
  }

  public void setCache(Map cache) {
    this.cache = cache;
  }

  public int storageCount() {
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();
    int count = -1;
    try {
      statement = connection.prepareStatement("SELECT count(*) " +
          " FROM Snip ");
      result = statement.executeQuery();
      if (result.next()) {
        count = result.getInt(1);
      }
    } catch (SQLException e) {
      SQLLogger.warn("JDBCSnipStorage: unable to get count", e);
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return count;
  }

  public List storageAll() {
    Finder finder = finders.getFinder("ORDER BY name");
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByHotness(int size) {
    Finder finder = finders.getFinder("ORDER BY viewCount DESC");
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByUser(String login) {
    Finder finder = finders.getFinder("WHERE cUser=?")
           .setString(1, login);
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByDateSince(Timestamp date) {
    Finder finder = finders.getFinder("WHERE mTime>=?")
           .setDate(1, date);
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByRecent(int size) {
    Finder finder = finders.getFinder("ORDER by mTime DESC");
    List list = createObjects(finder.execute(), size);
    finder.close();
    return list;
  }

  public List storageByComments(Snip parent) {
    Finder finder = finders.getFinder("WHERE commentSnip=? ORDER BY cTime")
           .setString(1, parent.getName());
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByParent(Snip parent) {
    Finder finder = finders.getFinder("WHERE parentSnip=?")
          .setString(1, parent.getName());
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByParentNameOrder(Snip parent, int count) {
    Finder finder = finders.getFinder("WHERE parentSnip=? ORDER BY name DESC ")
           .setString(1, parent.getName());
    List list = createObjects(finder.execute(), count);
    finder.close();
    return list;
  }

  public List storageByParentModifiedOrder(Snip parent, int count) {
    Finder finder = finders.getFinder("WHERE parentSnip=? ORDER BY mTime DESC ")
           .setString(1, parent.getName());
    List list = createObjects(finder.execute(), count);
    finder.close();
    return list;
  }

  public List storageByDateInName(String start, String end) {
    Finder finder = finders.getFinder("WHERE name>=? and name<=? and parentSnip=? " +
        " ORDER BY name")
          .setString(1, start)
          .setString(2, end)
          .setString(3, Application.get().getConfiguration().getStartSnip());
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  // Basic manipulation methods Load,Store,Create,Remove
  public Snip[] match(String pattern) {
    //@TODO implement this with LIKE
    return new Snip[]{};
  }

  public Snip[] match(String start, String end) {
    //@TODO implement this with LIKE
    return new Snip[]{};
  }

  public Snip storageLoad(String name) {
    // Logger.debug("LOAD "+name);

    if (cache.containsKey(name.toUpperCase())) {
      return (Snip) cache.get(name.toUpperCase());
    }

    Application app = Application.get();
    long start = app.start();
    Snip snip = null;
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions, " +
          " oUser, backLinks, snipLinks, labels, attachments, viewCount " +
          " FROM Snip " +
          " WHERE UPPER(name)=?");
      statement.setString(1, name.toUpperCase());

      result = statement.executeQuery();
      if (result.next()) {
        snip = createSnip(result);
      }
    } catch (SQLException e) {
      SQLLogger.warn("JDBCSnipStorage: unable to load snip " + name, e);
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    app.stop(start, "storageLoad - " + name);
    return snip;
  }

  public void storageStore(Snip snip) {
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("UPDATE Snip SET name=?, content=?, cTime=?, mTime=?, " +
          " cUser=?, mUser=?, parentSnip=?, commentSnip=?, permissions=?, " +
          " oUser=?, backLinks=?, snipLinks=?, labels=?, attachments=?, viewCount=? " +
          " WHERE name=?");
      statement.setString(1, snip.getName());
      statement.setString(2, snip.getContent());
      statement.setTimestamp(3, snip.getCTime());
      statement.setTimestamp(4, snip.getMTime());
      statement.setString(5, snip.getCUser());
      statement.setString(6, snip.getMUser());
      Snip parent = snip.getParent();
      if (null == parent) {
        statement.setNull(7, Types.VARCHAR);
      } else {
        statement.setString(7, parent.getName());
      }
      Snip comment = snip.getCommentedSnip();
      if (null == comment) {
        statement.setNull(8, Types.VARCHAR);
      } else {
        statement.setString(8, comment.getName());
      }
      statement.setString(9, snip.getPermissions().toString());
      statement.setString(10, snip.getOUser());
      statement.setString(11, snip.getBackLinks().toString());
      statement.setString(12, snip.getSnipLinks().toString());
      statement.setString(13, snip.getLabels().toString());
      statement.setString(14, snip.getAttachments().toString());
      statement.setInt(15, snip.getViewCount());
      statement.setString(16, snip.getName());
      statement.execute();
    } catch (SQLException e) {
      SQLLogger.warn("JDBCSnipStorage: unable to store snip " + snip.getName(), e);

    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return;
  }

  public Snip storageCreate(String name, String content) {
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();

    Application app = Application.get();
    String login = app.getUser().getLogin();
    Snip snip = SnipFactory.createSnip(name, content);
    Timestamp cTime = new Timestamp(new java.util.Date().getTime());
    Timestamp mTime = (Timestamp) cTime.clone();
    snip.setCTime(cTime);
    snip.setMTime(mTime);
    snip.setCUser(login);
    snip.setMUser(login);
    snip.setOUser(login);
    snip.setPermissions(new Permissions());
    snip.setBackLinks(new Links());
    snip.setSnipLinks(new Links());
    snip.setLabels(new Labels());
    snip.setAttachments(new Attachments());

    try {
      statement = connection.prepareStatement("INSERT INTO Snip (name, content, cTime, mTime, " +
          " cUser, mUser, parentSnip, commentSnip, permissions, " +
          " oUser, backLinks, snipLinks, labels, attachments, viewCount " +
          " ) VALUES (?,?,?,?,?," +
          "?,?,?,?,?," +
          "?,?,?,?,?)");
      statement.setString(1, name);
      statement.setString(2, content);
      statement.setTimestamp(3, cTime);
      statement.setTimestamp(4, mTime);
      statement.setString(5, login);
      statement.setString(6, login);
      Snip parent = snip.getParent();
      if (null == parent) {
        statement.setNull(7, Types.VARCHAR);
      } else {
        statement.setString(7, parent.getName());
      }
      Snip comment = snip.getCommentedSnip();
      if (null == comment) {
        statement.setNull(8, Types.VARCHAR);
      } else {
        statement.setString(8, comment.getName());
      }
      statement.setString(9, snip.getPermissions().toString());
      statement.setString(10, login);
      statement.setString(11, snip.getBackLinks().toString());
      statement.setString(12, snip.getSnipLinks().toString());
      statement.setString(13, snip.getLabels().toString());
      statement.setString(14, snip.getAttachments().toString());
      statement.setInt(15, snip.getViewCount());
      statement.execute();
    } catch (SQLException e) {
      SQLLogger.warn("JDBCSnipStorage: unable to get create snip " + name, e);
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }

    return (Snip) Aspects.newInstance(snip, Snip.class);
  }


  public void storageRemove(Snip snip) {
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("DELETE FROM Snip WHERE name=?");
      statement.setString(1, snip.getName());

      statement.execute();
    } catch (SQLException e) {
      SQLLogger.warn("JDBCSnipStorage: unable to remove " + snip.getName(), e);
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return;
  }

  public List createObjects(ResultSet result) {
    return createObjects(result, Integer.MAX_VALUE);
  }

  public synchronized List createObjects(ResultSet result, int size) {
    List resultList = new ArrayList();
    if (null != result) {
      try {
        while (result.next() && size-- > 0) {
          //String name = result.getString(1);
          resultList.add(createSnip(result));
        }
      } catch (SQLException e) {
        Logger.warn("Finder: SQL Error", e);
      }
    }
    return resultList;
  }

  public synchronized Snip createSnip(ResultSet result) throws SQLException {
    String name = result.getString("name");
    // Snip proxy = getProxy(name);
    // if (Apsects.hasTarget(proxy)) {
    //    return proxy;
    // }
    if (cache.containsKey(name.toUpperCase())) {
      return (Snip) cache.get(name.toUpperCase());
    }
    String content = result.getString("content");

    Snip snip = SnipFactory.createSnip(name, content);
    snip.setCTime(result.getTimestamp("cTime"));
    snip.setMTime(result.getTimestamp("mTime"));
    snip.setCUser(result.getString("cUser"));
    snip.setMUser(result.getString("mUser"));
    String commentString = result.getString("commentSnip");
    if (!result.wasNull()) {
      snip.setCommentedName(commentString);
    }
    String parentString = result.getString("parentSnip");
    if (!result.wasNull()) {
      snip.setParentName(parentString);
    }
    snip.setPermissions(new Permissions(result.getString("permissions")));
    snip.setBackLinks(new Links(result.getString("backLinks")));
    snip.setSnipLinks(new Links(result.getString("snipLinks")));
    snip.setLabels(new Labels(result.getString("labels")));
    snip.setAttachments(new Attachments(result.getString("attachments")));
    snip.setViewCount(result.getInt("viewCount"));

    // Aspects.setTarget(proxy, snip);
    // return proxy;
    snip = (Snip) Aspects.newInstance(snip, Snip.class);

    cache.put(name.toUpperCase(), snip);
    return snip;
  }

  // Return a proxy instance
  private Snip getProxy(String name) {
    if (cache.containsKey(name)) {
      return (Snip) cache.get(name);
    } else {
      Snip snip = null; // Aspects.new.....
      cache.put(name, snip);
      return snip;
    }
  }
}
