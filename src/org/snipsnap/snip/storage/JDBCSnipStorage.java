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
import org.snipsnap.jdbc.*;
import org.snipsnap.snip.Links;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipFactory;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.Permissions;
import org.snipsnap.util.ApplicationAwareMap;
import org.snipsnap.util.ConnectionManager;
import org.snipsnap.util.log.SQLLogger;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SnipStorage backend that uses JDBC for persisting data
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class JDBCSnipStorage implements SnipStorage, CacheableStorage {
  private DataSource ds;
  private FinderFactory finders;
  private ApplicationAwareMap cache;

  private static final String SNIP_SELECT = "SELECT applicationOid, name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions, " +
      " oUser, backLinks, snipLinks, labels, attachments, viewCount, version " +
      " FROM Snip ";

  public JDBCSnipStorage(DataSource ds) {
    this.ds = ds;
    this.finders = new FinderFactory(ds, SNIP_SELECT);
  }

  public static void createStorage() {
    DataSource datasource = ConnectionManager.getDataSource();

    System.err.println("JDBCSnipStorage: dropping Snip SQL table");
    JDBCTemplate dropTemplate = new JDBCTemplate(datasource);
    try {
      dropTemplate.update("DROP TABLE Snip");
    } catch (Exception e) {
      SQLLogger.warn("JDBCSnipStorage: unable to drop table (new install?)", e);
    }

    System.err.println("JDBCSnipStorage: creating Snip SQL table");
    JDBCTemplate template = new JDBCTemplate(datasource);
    template.update(
        "    CREATE TABLE Snip ( " +
        "       name           VARCHAR(100) NOT NULL, " +
        "       applicationOid VARCHAR(100) NOT NULL, " +
        "       content        TEXT, " +
        "       cTime          TIMESTAMP, " +
        "       mTime          TIMESTAMP, " +
        "       cUser          VARCHAR(55), " +
        "       mUser          VARCHAR(55), " +
        "       oUser          VARCHAR(55), " +
        "       parentSnip     VARCHAR(100), " +
        "       commentSnip    VARCHAR(100), " +
        "       backLinks      TEXT, " +
        "       snipLinks      TEXT, " +
        "       labels         TEXT, " +
        "       attachments    TEXT, " +
        "       viewCount      INTEGER, " +
        "       permissions    VARCHAR(200), " +
        "       version        INTEGER" +
        " )");
    return;
  }

  public void setCache(ApplicationAwareMap cache) {
    this.cache = cache;
  }

  public int storageCount() {
    final String applicationOid = (String) Application.get().getObject(Application.OID);
    final IntHolder holder = new IntHolder(-1);

    JDBCTemplate template = new JDBCTemplate(ds);
    template.query(
        "SELECT count(*) " +
        "   FROM Snip WHERE applicationOid=?"
        , new RowCallbackHandler() {
          public void processRow(ResultSet rs) throws SQLException {
            holder.setValue(rs.getInt(1));
          }
        }, new PreparedStatementSetter() {
          public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, applicationOid);
          }
        }
    );
    return holder.getValue();
  }

  public List storageAll(final String applicationOid) {
    final List list = new ArrayList();
    JDBCTemplate template = new JDBCTemplate(ds);
    template.query(SNIP_SELECT + "WHERE applicationOid=? ORDER BY name", new RowCallbackHandler() {
      public void processRow(ResultSet rs) throws SQLException {
        list.add(createSnip(rs));
      }
    }, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, applicationOid);
      }
    }
    );
    return list;
  }

  public List storageAll() {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    return storageAll(applicationOid);
  }

  public List storageByHotness(int size) {
    final String applicationOid = (String) Application.get().getObject(Application.OID);
    JDBCTemplate template = new JDBCTemplate(ds);
    final List list = new ArrayList();
    template.query(SNIP_SELECT + "WHERE applicationOid=? ORDER BY viewCount DESC", new RowCallbackHandler() {
      public void processRow(ResultSet rs) throws SQLException {
        list.add(createSnip(rs));
      }
    }, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, applicationOid);
      }
    });
    return list;
  }

  public List storageByUser(String login) {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    Finder finder = finders.getFinder("WHERE cUser=? AND applicationOid=?")
        .setString(1, login)
        .setString(2, applicationOid);
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByDateSince(Timestamp date) {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    Finder finder = finders.getFinder("WHERE mTime>=? AND applicationOid=?")
        .setDate(1, date)
        .setString(2, applicationOid);
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByRecent(int size) {
    final String applicationOid = (String) Application.get().getObject(Application.OID);
    JDBCTemplate template = new JDBCTemplate(ds);
    final List list = new ArrayList();
    template.query(SNIP_SELECT + "WHERE applicationOid=? ORDER BY mTime DESC", new RowCallbackHandler() {
      public void processRow(ResultSet rs) throws SQLException {
        list.add(createSnip(rs));
      }
    }, new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, applicationOid);
      }
    });
    return list;
  }

  public List storageByComments(Snip parent) {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    Finder finder = finders.getFinder("WHERE commentSnip=? AND applicationOid=? ORDER BY cTime")
        .setString(1, parent.getName())
        .setString(2, applicationOid);
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByParent(Snip parent) {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    Finder finder = finders.getFinder("WHERE parentSnip=? AND applicationOid=?")
        .setString(1, parent.getName())
        .setString(2, applicationOid);
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  public List storageByParentNameOrder(Snip parent, int count) {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    Finder finder = finders.getFinder("WHERE parentSnip=? AND applicationOid=? ORDER BY name DESC ")
        .setString(1, parent.getName())
        .setString(2, applicationOid);
    List list = createObjects(finder.execute(), count);
    finder.close();
    return list;
  }

  public List storageByParentModifiedOrder(Snip parent, int count) {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    Finder finder = finders.getFinder("WHERE parentSnip=? AND applicationOid=? ORDER BY mTime DESC ")
        .setString(1, parent.getName())
        .setString(2, applicationOid);
    List list = createObjects(finder.execute(), count);
    finder.close();
    return list;
  }

  public List storageByDateInName(String start, String end) {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    Finder finder = finders.getFinder("WHERE name>=? and name<=? and parentSnip=? AND applicationOid=?" +
        " ORDER BY name")
        .setString(1, start)
        .setString(2, end)
        .setString(3, Application.get().getConfiguration().getStartSnip())
        .setString(4, applicationOid);
    List list = createObjects(finder.execute());
    finder.close();
    return list;
  }

  // Basic manipulation methods Load,Store,Create,Remove
  public Snip[] match(String pattern) {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    Finder finder = finders.getFinder("WHERE name LIKE ? AND applicationOid=?" +
        " ORDER BY name")
        .setString(1, pattern.toUpperCase() + "%")
        .setString(2, applicationOid);
    List list = createObjects(finder.execute());
    finder.close();
    return (Snip[]) list.toArray(new Snip[list.size()]);
  }

  public Snip[] match(String start, String end) {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    //@TODO implement this with LIKE
    Finder finder = finders.getFinder("WHERE name>=? and name<=? AND applicationOid=?" +
        " ORDER BY name")
        .setString(1, start.toUpperCase())
        .setString(2, end.toUpperCase())
        .setString(3, applicationOid);
    List list = createObjects(finder.execute());
    finder.close();
    return (Snip[]) list.toArray(new Snip[list.size()]);
  }

  public Snip storageLoad(String name) {
    // Logger.debug("LOAD "+name);

    if (cache.getMap().containsKey(name.toUpperCase())) {
      return (Snip) cache.getMap().get(name.toUpperCase());
    }

    Application app = Application.get();
    String applicationOid = (String) app.getObject(Application.OID);
    long start = app.start();
    Snip snip = null;
    PreparedStatement statement = null;
    ResultSet result = null;
    Connection connection = null;

    try {
      connection = ds.getConnection();

      statement = connection.prepareStatement(SNIP_SELECT +
          " WHERE UPPER(name)=? AND applicationOid=?");
      statement.setString(1, name.toUpperCase());
      statement.setString(2, applicationOid);

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

  public void storageStore(final Snip snip) {
    //System.out.println("storing: "+snip+": "+ snip.getApplication());
    JDBCTemplate template = new JDBCTemplate(ds);
    template.update(
        " UPDATE Snip SET name=?, content=?, cTime=?, mTime=?, " +
        " cUser=?, mUser=?, parentSnip=?, commentSnip=?, permissions=?, " +
        " oUser=?, backLinks=?, snipLinks=?, labels=?, attachments=?, viewCount=?, version=? " +
        " WHERE name=? AND applicationOid=?",
        new PreparedStatementSetter() {
          public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, snip.getName());
            ps.setString(2, snip.getContent());
            ps.setTimestamp(3, snip.getCTime());
            ps.setTimestamp(4, snip.getMTime());
            ps.setString(5, snip.getCUser());
            ps.setString(6, snip.getMUser());
            Snip parent = snip.getParent();
            if (null == parent) {
              ps.setNull(7, Types.VARCHAR);
            } else {
              ps.setString(7, parent.getName());
            }
            Snip comment = snip.getCommentedSnip();
            if (null == comment) {
              ps.setNull(8, Types.VARCHAR);
            } else {
              ps.setString(8, comment.getName());
            }
            ps.setString(9, snip.getPermissions().toString());
            ps.setString(10, snip.getOUser());
            ps.setString(11, snip.getBackLinks().toString());
            ps.setString(12, snip.getSnipLinks().toString());
            ps.setString(13, snip.getLabels().toString());
            ps.setString(14, snip.getAttachments().toString());
            ps.setInt(15, snip.getViewCount());
            ps.setInt(16, snip.getVersion());
            ps.setString(17, snip.getName());
            ps.setString(18, snip.getApplication());
          }
        });
    return;
  }

  public Snip storageCreate(final String name, final String content) {
    Application app = Application.get();
    String applicationOid = (String) app.getObject(Application.OID);
    final String login = app.getUser().getLogin();
    final Snip snip = SnipFactory.createSnip(name, content);
    final Timestamp cTime = new Timestamp(new java.util.Date().getTime());
    final Timestamp mTime = (Timestamp) cTime.clone();
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
    snip.setApplication(applicationOid);

    JDBCTemplate template = new JDBCTemplate(ds);
    template.update(
        "INSERT INTO Snip (name, content, cTime, mTime, " +
        " cUser, mUser, parentSnip, commentSnip, permissions, " +
        " oUser, backLinks, snipLinks, labels, attachments, viewCount, applicationOid, version " +
        " ) VALUES (?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?, ?,?)",
        new PreparedStatementSetter() {
          public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, name);
            ps.setString(2, content);
            ps.setTimestamp(3, cTime);
            ps.setTimestamp(4, mTime);
            ps.setString(5, login);
            ps.setString(6, login);
            Snip parent = snip.getParent();
            if (null == parent) {
              ps.setNull(7, Types.VARCHAR);
            } else {
              ps.setString(7, parent.getName());
            }
            Snip comment = snip.getCommentedSnip();
            if (null == comment) {
              ps.setNull(8, Types.VARCHAR);
            } else {
              ps.setString(8, comment.getName());
            }
            ps.setString(9, snip.getPermissions().toString());
            ps.setString(10, login);
            ps.setString(11, snip.getBackLinks().toString());
            ps.setString(12, snip.getSnipLinks().toString());
            ps.setString(13, snip.getLabels().toString());
            ps.setString(14, snip.getAttachments().toString());
            ps.setInt(15, snip.getViewCount());
            ps.setString(16, snip.getApplication());
            ps.setInt(17, snip.getVersion());
          }
        });
    return (Snip) Aspects.newInstance(snip, Snip.class);
  }

  public void storageRemove(final Snip snip) {
    JDBCTemplate template = new JDBCTemplate(ds);
    template.update(
        "DELETE FROM Snip WHERE name=? AND applicationOid=?",
        new PreparedStatementSetter() {
          public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, snip.getName());
            ps.setString(2, snip.getApplication());
          }
        });
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
    String applicationOid = result.getString("applicationOid");
    String name = result.getString("name");
    // Snip proxy = getProxy(name);
    // if (Apsects.hasTarget(proxy)) {
    //    return proxy;
    // }
    // @TODO use 1,2,3, instead of 'name';
    if (cache.getMap(applicationOid).containsKey(name.toUpperCase())) {
      return (Snip) cache.getMap(applicationOid).get(name.toUpperCase());
    }

    String content = result.getString("content");

    Snip snip = SnipFactory.createSnip(name, content);
    snip.setApplication(applicationOid);
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
    snip.setVersion(result.getInt("version"));

    // Aspects.setTarget(proxy, snip);
    // return proxy;
    snip = (Snip) Aspects.newInstance(snip, Snip.class);

    cache.getMap(applicationOid).put(name.toUpperCase(), snip);
    return snip;
  }

  // Return a proxy instance
  private Snip getProxy(String name) {
    if (cache.getMap().containsKey(name)) {
      return (Snip) cache.getMap().get(name);
    } else {
      Snip snip = null; // Aspects.new.....
      cache.getMap().put(name, snip);
      return snip;
    }
  }
}
