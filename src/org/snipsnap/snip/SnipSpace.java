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
package com.neotis.snip;

import com.neotis.app.Application;
import com.neotis.cache.Cache;
import com.neotis.jdbc.Finder;
import com.neotis.jdbc.Loader;
import com.neotis.snip.filter.LinkTester;
import com.neotis.user.Permissions;
import com.neotis.user.Security;
import com.neotis.user.Roles;
import com.neotis.util.ConnectionManager;
import com.neotis.util.Queue;
import org.apache.lucene.search.Hits;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

/**
 * SnipSpace handles all the data storage.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class SnipSpace implements LinkTester, Loader {
  private Map missing;
  private Queue changed;
  private Cache cache;
  private SnipIndexer indexer;

  private static SnipSpace instance;

  public static synchronized SnipSpace getInstance() {
    if (null == instance) {
      instance = new SnipSpace();
      instance.init();
    }
    return instance;
  }

  private SnipSpace() {
  }

  private void init() {
    missing = new HashMap();
    changed = new Queue(50);
    cache = new Cache((Loader) this);
    changed.fill(storageByRecent(50));
    indexer = new SnipIndexer();
  }

  public List getChanged() {
    return getChanged(15);
  }

  public List getChanged(int count) {
    return changed.get(count);
  }

  public List getAll() {
    return storageAll();
  }

  public List getByDate(String start, String end) {
    return storageByDateInName(start, end);
  }

  public List getComments(Snip snip) {
    return storageByComments(snip);
  }

  public List getByUser(String login) {
    return storageByUser(login);
  }

  public List getChildren(Snip snip) {
    return storageByParent(snip);
  }

  public List getChildrenDateOrder(Snip snip, int count) {
    return storageByParentNameOrder(snip, count);
  }

  public Snip post(String content, Application app) {
    Date date = new Date(new java.util.Date().getTime());
    return post(content, date, app);
  }

  public void reIndex() {
    List snips = getAll();
    Iterator iterator = snips.iterator();
    System.out.println("Reindexing");
    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      System.out.print("  " + snip.getName() + " ... " );
      indexer.reIndex(snip);
      System.out.println("ok.");
    }
  }

  public Hits search(String queryString) {
    return indexer.search(queryString);
  }

  public Snip post(String content, Date date, Application app) {
    Snip start = load("start");
    return post(start, content, date, app);
  }

  public Snip post(Snip weblog, String content, Date date, Application app) {
    String name = Snip.toName(date);
    Snip snip = null;
    if (exists(name)) {
      snip = load(name);
      snip.setContent(snip.getContent() + "\n\n" + content);

    } else {
      snip = create(name, content, app);
    }
    snip.setParent(weblog);
    snip.addPermission(Permissions.EDIT, Roles.OWNER);
    store(snip);

    // Ping weblogs.com that we changed our site
    // WeblogsPing.ping();
    return snip;
  }

  public boolean exists(String name) {
    if (missing.containsKey(name)) {
      return false;
    }

    if (null == load(name)) {
      missing.put(name, new Integer(0));
      return false;
    } else {
      return true;
    }
  }

  public Snip load(String name) {
    return cache.load(name);
  }

  public void store(Snip snip, Application app) {
    snip.setMUser(app.getUser());
    store(snip);
    return;
  }

  public void store(Snip snip) {
    changed.add(snip);
    snip.setMTime(new Timestamp(new java.util.Date().getTime()));
    storageStore(snip);
    indexer.reIndex(snip);
    return;
  }

  public Snip create(String name, String content, Application app) {
    Snip snip = storageCreate(name, content, app);
    cache.put(name, snip);
    if (missing.containsKey(name)) {
      missing.remove(name);
    }
    changed.add(snip);
    indexer.index(snip);
    return snip;
  }

  public void remove(Snip snip) {
    cache.remove(snip.getName());
    changed.remove(snip);
    storageRemove(snip);
    indexer.removeIndex(snip);
    return;
  }

// Storage System dependend Methods

  public Snip createSnip(ResultSet result) throws SQLException {
    String name = result.getString("name");
    String content = result.getString("content");

    Snip snip = new Snip(name, content);
    snip.setCTime(result.getTimestamp("cTime"));
    snip.setMTime(result.getTimestamp("mTime"));
    snip.setCUser(result.getString("cUser"));
    snip.setMUser(result.getString("mUser"));
    String commentString = result.getString("commentSnip");
    if (!result.wasNull()) {
      snip.setCommentedSnip(load(commentString));
    }
    String parentString = result.getString("parentSnip");
    if (!result.wasNull()) {
      snip.parent = load(parentString);
    }
    snip.setPermissions(new Permissions(result.getString("permissions")));
    return snip;
  }

  private List storageAll() {
    Finder finder = new Finder("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions " +
                               " FROM Snip " +
                               " ORDER BY name", cache, (Loader) this, false);
    return finder.execute();
  }

  private List storageByRecent(int size) {
    Finder finder = new Finder("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions " +
                               " FROM Snip " +
                               " ORDER by mTime DESC", cache, (Loader) this);

    return finder.execute(size);
  }

  private List storageByUser(String login) {
    Finder finder = new Finder("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions " +
                               " FROM Snip " +
                               " WHERE cUser=?", cache, (Loader) this);
    finder.setString(1, login);
    return finder.execute();
  }

  private List storageByComments(Snip parent) {
    Finder finder = new Finder("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions " +
                               " FROM Snip " +
                               " WHERE commentSnip=?", cache, (Loader) this);
    finder.setString(1, parent.getName());
    return finder.execute();
  }

  private List storageByParent(Snip parent) {
    Finder finder = new Finder("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions " +
                               " FROM Snip " +
                               " WHERE parentSnip=?", cache, (Loader) this);
    finder.setString(1, parent.getName());
    return finder.execute();
  }

  private List storageByParentNameOrder(Snip parent, int count) {
    Finder finder = new Finder("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions " +
                               " FROM Snip " +
                               " WHERE parentSnip=? " +
                               " ORDER BY name DESC ", cache, (Loader) this);
    finder.setString(1, parent.getName());
    return finder.execute(count);
  }

  private List storageByDateInName(String start, String end) {
    Finder finder = new Finder("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions " +
                               " FROM Snip " +
                               " WHERE name>=? and name<=? and parentSnip=? " +
                               " ORDER BY name", cache, (Loader) this);
    finder.setString(1, start);
    finder.setString(2, end);
    finder.setString(3, "start");
    return finder.execute();
  }

  public Snip storageLoad(String name) {
    Snip snip = null;
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions " +
                                              " FROM Snip " +
                                              " WHERE name=?");
      statement.setString(1, name);

      result = statement.executeQuery();
      if (result.next()) {
        snip = createSnip(result);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(result);
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return snip;
  }

  private void storageStore(Snip snip) {
    PreparedStatement statement = null;
    Connection connection = ConnectionManager.getConnection();

    try {
      statement = connection.prepareStatement("UPDATE Snip SET name=?, content=?, cTime=?, mTime=?, " +
                                              " cUser=?, mUser=?, parentSnip=?, commentSnip=?, permissions=? " +
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
      statement.setString(10, snip.getName());
      statement.execute();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectionManager.close(statement);
      ConnectionManager.close(connection);
    }
    return;
  }

  private Snip storageCreate(String name, String content, Application app) {
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = ConnectionManager.getConnection();

    String login = app.getUser().getLogin();
    Snip snip = new Snip(name, content);
    Timestamp cTime = new Timestamp(new java.util.Date().getTime());
    Timestamp mTime = (Timestamp) cTime.clone();
    snip.setCTime(cTime);
    snip.setMTime(mTime);
    snip.setCUser(login);
    snip.setMUser(login);
    snip.setPermissions(new Permissions());

    try {
      statement = connection.prepareStatement("INSERT INTO Snip (name, content, cTime, mTime, " +
                                              " cUser, mUser, parentSnip, commentSnip, permissions) " +
                                              " VALUES (?,?,?,?,?,?,?,?,?)");
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


  private void storageRemove(Snip snip) {
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
}