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
package org.snipsnap.app;

import org.snipsnap.jdbc.JDBCTemplate;
import org.snipsnap.jdbc.PreparedStatementSetter;
import org.snipsnap.jdbc.RowCallbackHandler;
import org.snipsnap.jdbc.UIDGenerator;
import org.snipsnap.util.ConnectionManager;
import org.snipsnap.util.log.SQLLogger;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ApplicationStorage is a DAO for applications.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class JDBCApplicationStorage implements ApplicationStorage {
  private DataSource ds;

  public static void createStorage() {
    DataSource datasource = ConnectionManager.getDataSource();
    System.err.println("JDBCApplicationManager: dropping application SQL table");
    JDBCTemplate dropTemplate = new JDBCTemplate(datasource);
    try {
      dropTemplate.update("DROP TABLE Application");
    } catch (Exception e) {
      SQLLogger.warn("JDBCApplicationStorage: unable to drop table (new install?)", e);
    }
    System.err.println("JDBCApplicationManager: creating application SQL table");
    JDBCTemplate template = new JDBCTemplate(datasource);
    template.update(
      "CREATE TABLE Application ( " +
      "     applicationOid VARCHAR(100) NOT NULL," +
      "     name           VARCHAR(100) NOT NULL, " +
      "     prefix         VARCHAR(100) )");
    return;
  }

  public JDBCApplicationStorage(DataSource ds) {
    this.ds = ds;
  }

  public Map getApplications() {
    JDBCTemplate template = new JDBCTemplate(ds);
    final Map applications = new HashMap();
    template.query("SELECT applicationOid, name, prefix FROM Application",
                   new RowCallbackHandler() {
                     public void processRow(ResultSet rs) throws SQLException {
                       Properties prefixProps = new Properties();
                       String prefix = rs.getString(3);
                       prefixProps.setProperty(ApplicationStorage.OID, rs.getString(1));
                       prefixProps.setProperty(ApplicationStorage.NAME, rs.getString(2));
                       prefixProps.setProperty(ApplicationStorage.PREFIX, prefix);
                       applications.put(prefix, prefixProps);
                     }
                   });
    return applications;
  }

  public void removeApplication(final String oid) {
    JDBCTemplate template = new JDBCTemplate(ds);
    template.update("DELETE FROM Application WHERE applicationOid=?", new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, oid);
      }
    });
    return;
  }


  public Properties createApplication(final String name, final String prefix) {
    final String oid = UIDGenerator.generate(ApplicationStorage.class);
    JDBCTemplate template = new JDBCTemplate(ds);
    template.update("INSERT INTO Application (applicationOid,name,prefix) VALUES (?,?,?)", new PreparedStatementSetter() {
      public void setValues(PreparedStatement ps) throws SQLException {
        ps.setString(1, oid);
        ps.setString(2, name);
        ps.setString(3, prefix);
      }
    });
    Properties prefixProps = new Properties();
    prefixProps.setProperty(ApplicationStorage.OID, oid);
    prefixProps.setProperty(ApplicationStorage.NAME, name);
    prefixProps.setProperty(ApplicationStorage.PREFIX, prefix);
    return prefixProps;
  }
}
