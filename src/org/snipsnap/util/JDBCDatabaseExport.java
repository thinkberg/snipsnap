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
package org.snipsnap.util;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import snipsnap.api.config.Configuration;
import org.snipsnap.snip.storage.Serializer;
import org.snipsnap.snip.storage.SnipDataSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class JDBCDatabaseExport {
  private final static Map DBSER = new HashMap();

  static {
    DBSER.put("name", SnipDataSerializer.SNIP_NAME);
    DBSER.put("applicationOid",SnipDataSerializer.SNIP_APPLICATION);
    DBSER.put("content", SnipDataSerializer.SNIP_CONTENT);
    DBSER.put("cTime", SnipDataSerializer.SNIP_CTIME);
    DBSER.put("mTime", SnipDataSerializer.SNIP_MTIME);
    DBSER.put("cUser", SnipDataSerializer.SNIP_CUSER);
    DBSER.put("mUser", SnipDataSerializer.SNIP_MUSER);
    DBSER.put("oUser", SnipDataSerializer.SNIP_OUSER);
    DBSER.put("backLinks", SnipDataSerializer.SNIP_BACKLINKS);
    DBSER.put("snipLinks", SnipDataSerializer.SNIP_SNIPLINKS);
    DBSER.put("labels", SnipDataSerializer.SNIP_LABELS);
    DBSER.put("attachments", SnipDataSerializer.SNIP_ATTACHMENTS);
    DBSER.put("viewCount", SnipDataSerializer.SNIP_VIEWCOUNT);
    DBSER.put("permissions", SnipDataSerializer.SNIP_PERMISSIONS);
    DBSER.put("version", SnipDataSerializer.SNIP_VERSION);

    DBSER.put("parentSnip", SnipDataSerializer.SNIP_PARENT);
    DBSER.put("commentSnip", SnipDataSerializer.SNIP_COMMENTED);
  }
  public static void main(String[] args) {
    Properties configData = new Properties();
    try {
      configData.load(new FileInputStream(args[0]));
    } catch (IOException e) {
      System.err.println("JDBCDatabaseExport: missing configuration file: " + e.getMessage());
      System.exit(1);
    }

    export(configData, args[1], new File(args[0]).getParent());
    System.exit(0);
  }

  private static String replaceTokens(String value, String webInf) {
    if (value != null) {
      int idx = value.indexOf("%WEBINF%");
      if (idx != -1) {
        StringBuffer replaced = new StringBuffer();
        if (idx > 0) {
          replaced.append(value.substring(0, idx));
        }
        replaced.append(webInf);
        int endIdx = idx + "%WEBINF%".length();
        if (endIdx < value.length()) {
          replaced.append(value.substring(endIdx));
        }
        return replaced.toString();
      }
    }

    return value;
  }

  public static void export(Properties config, String appOid, String webInf) {
    Iterator configIt = config.keySet().iterator();
    while(configIt.hasNext()) {
      String key = (String)configIt.next();
      String value = config.getProperty(key);
      config.setProperty(key, replaceTokens(value, webInf));
    }

    try {
      Class.forName(config.getProperty(Configuration.APP_JDBC_DRIVER));
      Properties jdbcInfo = new Properties();
      jdbcInfo.setProperty("user", config.getProperty(snipsnap.api.config.Configuration.APP_JDBC_USER));
      jdbcInfo.setProperty("password", config.getProperty(Configuration.APP_JDBC_PASSWORD));
      Connection connection = DriverManager.getConnection(config.getProperty(Configuration.APP_JDBC_URL), jdbcInfo);
      store(System.out, appOid, connection);
    } catch (Exception e) {
      System.err.println("JDBCDatabaseExport: can't connect to database: " + e);
      e.printStackTrace();
    }
  }

  /**
   * Store snips and users from the SnipSpace to an xml document into a stream.
   * @param out outputstream to write to
   */
  public static void store(OutputStream out, String appOid, Connection connection) {
    try {
      OutputFormat outputFormat = new OutputFormat();
      outputFormat.setEncoding("UTF-8");
      outputFormat.setNewlines(true);

      XMLWriter xmlWriter = new XMLWriter(out, outputFormat);
      xmlWriter.startDocument();
      Element root = DocumentHelper.createElement("snipspace");
      xmlWriter.writeOpen(root);

//      storeUsers(xmlWriter, connection);
      storeSnips(xmlWriter, appOid, connection);

      xmlWriter.writeClose(root);
      xmlWriter.endDocument();
      xmlWriter.flush();
      xmlWriter.close();
    } catch (Exception e) {
      System.err.println("JDBCDatabaseExport: error while writing document: " + e.getMessage());
    }
  }

  private static void storeSnips(XMLWriter xmlWriter, String appOid, Connection connection) {
    try {
      ResultSet results = readTable("Snip", appOid, connection);
      serializeSnips(xmlWriter, results, connection, new SnipDataSerializer());
    } catch (Exception e) {
      System.err.println("JDBCDabaseExport: error serializing snips: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Make an XML respresentation from a table.
   */
  private static ResultSet readTable(String table, String appOid, Connection connection) throws SQLException {
    PreparedStatement prepStmt = connection.prepareStatement("SELECT * FROM " + table + " WHERE applicationOid='"+appOid+"'");
    return prepStmt.executeQuery();
  }

  /**
   * Make an XML representation from a result set.
   */
  private static void serializeSnips(XMLWriter xmlWriter, ResultSet results, Connection connection, Serializer serializer) throws IOException, SQLException {
    ResultSetMetaData meta = results.getMetaData();

    while (results.next()) {
      // loop through columns and create map entries
      Map elementMap = serializeData(results, meta);
      // store map content entries and close element
      Element snipElement = serializer.serialize(elementMap);
      // store versions information
//      Element versionsElement = snipElement.addElement("versions");
//      PreparedStatement prepStmt = connection.prepareStatement("SELECT * FROM SnipVersion WHERE name='" + elementMap.get(SnipDataSerializer.SNIP_NAME)+"' AND applicationOid='" + appOid +"'" );

      xmlWriter.write(snipElement);
    }
  }

  private static Map serializeData(ResultSet results, ResultSetMetaData meta) throws SQLException {
    Map elementMap = new HashMap();
    int size = meta.getColumnCount();

    for (int i = 1; i <= size; i++) {
      Object object = results.getObject(i);
      String name = meta.getColumnName(i);
      if (null != object) {
        if (object instanceof Timestamp) {
          Timestamp time = (Timestamp) object;
          elementMap.put(DBSER.get(name), "" + time.getTime());
        } else {
          elementMap.put(DBSER.get(name), object);
        }
      }
    }
    return elementMap;
  }
}
