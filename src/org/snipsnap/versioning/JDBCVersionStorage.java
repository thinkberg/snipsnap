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

package org.snipsnap.versioning;

import org.snipsnap.jdbc.JDBCTemplate;
import org.snipsnap.jdbc.PreparedStatementSetter;
import org.snipsnap.jdbc.RowCallbackHandler;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipImpl;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.util.ConnectionManager;
import org.snipsnap.util.log.SQLLogger;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

/**
 * Stores versions of snips with JDBC
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class JDBCVersionStorage implements VersionStorage {
  private DataSource ds;

  public static void createStorage() {
    DataSource datasource = ConnectionManager.getDataSource();
    System.err.println("JDBCVersionStorage: dropping version SQL table");
    JDBCTemplate dropTemplate = new JDBCTemplate(datasource);
    try {
      dropTemplate.update("DROP TABLE SnipVersion");
    } catch (Exception e) {
      SQLLogger.warn("JDBCVersionStorage: unable to drop table (new install?)", e);
    }
    System.err.println("JDBCVersionStorage: creating version SQL table");
    JDBCTemplate template = new JDBCTemplate(datasource);
    template.update(
      "CREATE TABLE SnipVersion ( " +
      "       version        INTEGER," +
      "       name           VARCHAR(100) NOT NULL, " +
      "       applicationOid VARCHAR(100) NOT NULL, " +
      "       content        TEXT, " +
      "       mTime          TIMESTAMP, " +
      "       mUser          VARCHAR(55), " +
      "       labels         TEXT, " +
      "       viewCount      INTEGER" +
      ")");
    return;
  }

  public JDBCVersionStorage(DataSource ds) {
    this.ds = ds;
  }

  public Snip loadVersion(final Snip snip, final int version) {
    final String applicationOid = snip.getApplication();
    final String name = snip.getName();
    final List snips = new ArrayList();
    JDBCTemplate template = new JDBCTemplate(ds);
    template.query(
      "SELECT applicationOid, name, version, content, mTime, mUser, labels, viewCount" +
      " FROM SnipVersion " +
      " WHERE name=? AND applicationOid=? AND version=?",
      new RowCallbackHandler() {
        public void processRow(ResultSet rs) throws SQLException {
          snips.add(createSnip(snip, rs));
        }
      },
      new PreparedStatementSetter() {
        public void setValues(PreparedStatement ps) throws SQLException {
          ps.setString(1, name);
          ps.setString(2, applicationOid);
          ps.setInt(3, version);
        }
      });
    return (snips.size() > 0 ? (Snip) snips.get(0) : null);
  }

  public void storeVersion(final Snip snip) {
    synchronized(snip) {
    JDBCTemplate template = new JDBCTemplate(ds);
    template.update(
      "INSERT INTO SnipVersion (" +
      " applicationOid, name, version, content, mTime, mUser, labels, viewCount" +
      " ) VALUES (?,?,?,?,?, ?,?,?)",
      new PreparedStatementSetter() {
        public void setValues(PreparedStatement ps) throws SQLException {
          ps.setString(1, snip.getApplication());
          ps.setString(2, snip.getName());
          ps.setInt(3, snip.getVersion());
          ps.setString(4, snip.getContent());
          ps.setTimestamp(5, snip.getMTime());
          ps.setString(6, snip.getMUser());
          ps.setString(7, snip.getLabels().toString());
          ps.setInt(8, snip.getViewCount());
        }
      });
    }
    return;
  }

  /**
   * Return a list of VersionInfo objects.
   * Reads all snips from the JDBC datasource.
   *
   * @param snip Snip to get the version history for.
   * @return
   */
  public List getVersionHistory(final Snip snip) {
    final List history = new ArrayList();
    JDBCTemplate template = new JDBCTemplate(ds);
    template.query(
      "SELECT version, mTime, mUser, viewCount, content" +
      " FROM SnipVersion " +
      " WHERE name=? AND applicationOid=? ORDER BY version DESC",
      new RowCallbackHandler() {
        public void processRow(ResultSet rs) throws SQLException {
          // Replace with version object?
          VersionInfo info = new VersionInfo();
          info.setVersion(rs.getInt("version"));
          info.setMTime( rs.getTimestamp("mTime"));
          info.setMUser( rs.getString("mUser"));
          info.setViewCount(rs.getInt("viewCount"));
          info.setSize(rs.getString("content").length());
          history.add(info);
        }
      },
      new PreparedStatementSetter() {
        public void setValues(PreparedStatement ps) throws SQLException {
          ps.setString(1, snip.getName());
          ps.setString(2, snip.getApplication());
        }
      });
    return history;
  }

  private Snip createSnip(Snip snip, ResultSet rs) throws SQLException {
//    "SELECT applicationOid, name, version, content, mTime, mUser, labels, viewCount" +
    Snip newSnip = new SnipImpl(snip.getName(), "");
    newSnip.setApplication(snip.getApplication());
    newSnip.setVersion(rs.getInt("version"));
    newSnip.setContent(rs.getString("content"));
    newSnip.setMTime(rs.getTimestamp("mTime"));
    newSnip.setMUser(rs.getString("mUser"));
    newSnip.setLabels(new Labels(newSnip, rs.getString("labels")));
    newSnip.setViewCount(rs.getInt("viewCount"));
    return newSnip;
  }
}
