/*      Rss
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
import org.snipsnap.jdbc.*;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.util.ConnectionManager;
import org.snipsnap.util.log.SQLLogger;

import javax.sql.DataSource;
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
  private DataSource ds;

  public JDBCUserStorage(DataSource ds) {
    this.ds = ds;
    finders = new FinderFactory(ds, "SELECT applicationOid, login, passwd, email, status, roles, " +
        " cTime, mTime, lastLogin, lastAccess, lastLogout " +
        " FROM SnipUser ");
  }

  public static void createStorage() {
    DataSource datasource = ConnectionManager.getDataSource();
    System.err.println("JDBCUserStorage: dropping SnipUser SQL table");
    JDBCTemplate droptemplate = new JDBCTemplate(datasource);
    try {
      droptemplate.update("DROP TABLE SnipUser");
    } catch (Exception e) {
      SQLLogger.warn("JDBCUserStorage: unable to drop table (new install?)", e);
    }

    System.err.println("JDBCUserStorage: creating SnipUser SQL table");
    JDBCTemplate template = new JDBCTemplate(datasource);
    template.update(
        "    CREATE TABLE SnipUser ( " +
        "       login          VARCHAR(100) NOT NULL, " +
        "       applicationOid VARCHAR(100) NOT NULL," +
        "       cTime          TIMESTAMP, " +
        "       mTime          TIMESTAMP, " +
        "       lastLogin      TIMESTAMP, " +
        "       lastAccess     TIMESTAMP, " +
        "       lastLogout     TIMESTAMP, " +
        "       passwd         VARCHAR(100), " +
        "       email          VARCHAR(100)," +
        "       status         VARCHAR(50), " +
        "       roles          VARCHAR(200) )");
    return;
  }

  public void storageStore(final User user) {
    JDBCTemplate template = new JDBCTemplate(ds);
    template.update(
        "UPDATE SnipUser SET login=?, passwd=?, email=?, status=?, roles=?, " +
        " cTime=?, mTime=?, lastLogin=?, lastAccess=?, lastLogout=? " +
        " WHERE login=? AND applicationOid=?",
        new PreparedStatementSetter() {
          public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswd());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getStatus());
            ps.setString(5, user.getRoles().toString());
            ps.setTimestamp(6, user.getCTime());
            ps.setTimestamp(7, user.getMTime());
            ps.setTimestamp(8, user.getLastLogin());
            ps.setTimestamp(9, user.getLastAccess());
            ps.setTimestamp(10, user.getLastLogout());
            ps.setString(11, user.getLogin());
            ps.setString(12, user.getApplication());
          }
        });
    return;
  }

  public User storageCreate(String login, String passwd, String email) {
    String applicationOid = (String) Application.get().getObject(Application.OID);

    final User user = new User(login, passwd, email);
    final Timestamp cTime = new Timestamp(new java.util.Date().getTime());
    user.setCTime(cTime);
    user.setMTime(cTime);
    user.setLastLogin(cTime);
    user.setLastAccess(cTime);
    user.setLastLogout(cTime);
    user.setApplication(applicationOid);

    JDBCTemplate template = new JDBCTemplate(ds);
    template.update(
        "INSERT INTO SnipUser " +
        " (login, passwd, email, status, roles, " +
        " cTime, mTime, lastLogin, lastAccess, lastLogout, applicationOid) " +
        " VALUES (?,?,?,?,?,?,?,?,?,?,?)",
        new PreparedStatementSetter() {
          public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getPasswd());
            ps.setString(3, user.getEmail());
            ps.setString(4, "");
            ps.setString(5, "");
            ps.setTimestamp(6, cTime);
            ps.setTimestamp(7, cTime);
            ps.setTimestamp(8, cTime);
            ps.setTimestamp(9, cTime);
            ps.setTimestamp(10, cTime);
            ps.setString(11, user.getApplication());
          }
        });
    return user;
  }

  public void storageRemove(final User user) {
    JDBCTemplate template = new JDBCTemplate(ds);
    template.update(
        "DELETE FROM SnipUser WHERE login=? AND applicationOid=?",
        new PreparedStatementSetter() {
          public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getApplication());
          }
        });
    return;
  }

  public int storageUserCount() {
    final String applicationOid = (String) Application.get().getObject(Application.OID);
    final IntHolder holder = new IntHolder(-1);

    JDBCTemplate template = new JDBCTemplate(ds);
    template.query(
        "SELECT count(*) FROM SnipUser WHERE applicationOid=?",
        new RowCallbackHandler() {
          public void processRow(ResultSet rs) throws SQLException {
            holder.setValue(rs.getInt(1));
          }
        },
        new PreparedStatementSetter() {
          public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, applicationOid);
          }
        });
    return holder.getValue();
  }

  public User storageLoad(final String login) {
    //System.err.println("storageLoad: User login="+login);
    final String applicationOid = (String) Application.get().getObject(Application.OID);
    final List users = new ArrayList();
    JDBCTemplate template = new JDBCTemplate(ds);
    template.query(
        "SELECT applicationOid, login, passwd, email, status, roles, cTime, mTime, " +
        " lastLogin, lastAccess, lastLogout " +
        " FROM SnipUser " +
        " WHERE login=? AND applicationOid=?",
        new RowCallbackHandler() {
          public void processRow(ResultSet rs) throws SQLException {
            users.add(createUser(rs));
          }
        },
        new PreparedStatementSetter() {
          public void setValues(PreparedStatement ps) throws SQLException {
            ps.setString(1, login);
            ps.setString(2, applicationOid);
          }
        });
    return (users.size() > 0 ? (User) users.get(0) : null);
  }

  public List storageAll() {
    String applicationOid = (String) Application.get().getObject(Application.OID);

    Finder finder = finders.getFinder(" WHERE applicationOid=? ORDER BY login ")
        .setString(1, applicationOid);
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
    String applicationOid = result.getString("applicationOid");
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
    user.setApplication(applicationOid);
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
