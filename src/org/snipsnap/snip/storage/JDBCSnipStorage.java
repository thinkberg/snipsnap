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

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.Links;
import org.snipsnap.snip.Attachments;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.Permissions;
import org.snipsnap.util.ConnectionManager;
import org.snipsnap.jdbc.Finder;
import org.snipsnap.jdbc.FinderFactory;
import org.snipsnap.jdbc.JDBCCreator;
import org.snipsnap.cache.Cache;
import org.snipsnap.app.Application;

import java.sql.*;
import java.util.List;

/**
 * SnipStorage backend that uses JDBC for persisting data
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class JDBCSnipStorage implements SnipStorage, JDBCCreator {
  private FinderFactory finders;

  public JDBCSnipStorage(Cache cache) {
    this.finders = new FinderFactory("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions, " +
                               " oUser, backLinks, snipLinks, labels, attachments, viewCount " +
                               " FROM Snip ", cache, Snip.class, "name", this);
  }

  public Object createObject(ResultSet result) throws SQLException {
    return storageCreateSnip(result);
  }

  public Snip storageCreateSnip(ResultSet result) throws SQLException {
    String name = result.getString("name");
    String content = result.getString("content");

    Snip snip = new Snip(name, content);
    snip.setCTime(result.getTimestamp("cTime"));
    snip.setMTime(result.getTimestamp("mTime"));
    snip.setCUser(result.getString("cUser"));
    snip.setMUser(result.getString("mUser"));
    String commentString = result.getString("commentSnip");
    if (!result.wasNull()) {
      snip.setCommentedSnip(SnipSpace.getInstance().load(commentString));
    }
    String parentString = result.getString("parentSnip");
    if (!result.wasNull()) {
      snip.parent = SnipSpace.getInstance().load(parentString);
    }
    snip.setPermissions(new Permissions(result.getString("permissions")));
    snip.setBackLinks(new Links(result.getString("backLinks")));
    snip.setSnipLinks(new Links(result.getString("snipLinks")));
    snip.setLabels(new Labels(result.getString("labels")));
    snip.setAttachments(new Attachments(result.getString("attachments")));
    snip.setViewCount(result.getInt("viewCount"));
    return snip;
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
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return count;
  }

  public List storageAll() {
    Finder finder = finders.getFinder("ORDER BY name");
    return finder.execute();
  }

  public List storageByHotness(int size) {
    Finder finder = finders.getFinder("ORDER BY viewCount DESC");
    return finder.execute(size);
  }

  public List storageByUser(String login) {
    Finder finder = finders.getFinder("WHERE cUser=?");
    finder.setString(1, login);
    return finder.execute();
  }

  public List storageByDateSince(Timestamp date) {
    Finder finder = finders.getFinder("WHERE mTime>=?");
    finder.setDate(1, date);
    return finder.execute();
  }

  public List storageByRecent(int size) {
    Finder finder = finders.getFinder("ORDER by mTime DESC");
    return finder.execute(size);
  }

  public List storageByComments(Snip parent) {
    Finder finder = finders.getFinder("WHERE commentSnip=? ORDER BY cTime");
    finder.setString(1, parent.getName());
    return finder.execute();
  }

  public List storageByParent(Snip parent) {
    Finder finder = finders.getFinder("WHERE parentSnip=?");
    finder.setString(1, parent.getName());
    return finder.execute();
  }

  public List storageByParentNameOrder(Snip parent, int count) {
    Finder finder = finders.getFinder("WHERE parentSnip=? ORDER BY name DESC ");
    finder.setString(1, parent.getName());
    return finder.execute(count);
  }

  public List storageByParentModifiedOrder(Snip parent, int count) {
    Finder finder = finders.getFinder("WHERE parentSnip=? ORDER BY mTime DESC ");
    finder.setString(1, parent.getName());
    return finder.execute(count);
  }

  public List storageByDateInName(String start, String end) {
    Finder finder = finders.getFinder("WHERE name>=? and name<=? and parentSnip=? " +
                                      " ORDER BY name");
    finder.setString(1, start);
    finder.setString(2, end);
    finder.setString(3, "start");
    return finder.execute();
  }

  public Snip storageLoad(String name) {
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
        snip = storageCreateSnip(result);
      }
    } catch (SQLException e) {
      e.printStackTrace();
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
      e.printStackTrace();
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
    Snip snip = new Snip(name, content);
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
                                              " ) VALUES (?,?,?,?,?,"+
                                                         "?,?,?,?,?,"+
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
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }

    return snip;
  }


  public void storageRemove(Snip snip) {
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("DELETE FROM Snip WHERE name=?");
      statement.setString(1, snip.getName());

      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return;
  }

  public Object loadObject(String name) {
    return storageLoad(name);
  }
}
