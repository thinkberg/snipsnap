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
package org.snipsnap.snip;

import org.radeox.util.logging.Logger;
import org.snipsnap.util.ConnectionManager;
import org.snipsnap.container.Components;
import org.snipsnap.snip.storage.SnipSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Iterator;
import java.beans.XMLEncoder;

/**
 * Import
 */
public class XMLSnipExport {
  public final static int USERS = 0x01;
  public final static int SNIPS = 0x02;

  public static void store(OutputStream out) throws IOException {
    store(out, USERS | SNIPS);
  }

  /**
   * Store snips and users from the SnipSpace to an xml document into a stream.
   * @param out outputstream to write to
   * @param exportMask mask of what to store and what to ignore
   */
  public static void store(OutputStream out, int exportMask) {

    Connection connection = ConnectionManager.getConnection();
    ResultSet results = null;

    try {
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "UTF-8"));
      pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
      pw.println("<snipspace>");

      if ((exportMask & USERS) != 0) { toXml("SnipUser", "user", connection, pw); }
      if ((exportMask & SNIPS) != 0) { toXml("Snip", "snip", connection, pw); }

      pw.println("</snipspace>");
      pw.flush();
      pw.close();
    } catch (Exception e) {
      Logger.warn("error writing output", e);
    }
  }

  /**
   * Make an XML respresentation from a table.
   */
  private static void toXml(String table, String export, Connection connection, PrintWriter out) {
    ResultSet results;
    try {
      PreparedStatement prepStmt = connection.prepareStatement("SELECT * " +
                                                               " FROM " + table);
      results = prepStmt.executeQuery();
      toXml(export, results, out);
    } catch (SQLException e) {
      Logger.warn("Problems with query", e);
    }
  }

  /**
   * Make an XML representation from a result set.
   */
  private static void toXml(String objectName, ResultSet results, PrintWriter out) throws SQLException {
    ResultSetMetaData meta = results.getMetaData();
    int size = meta.getColumnCount();
    while (results.next()) {
      out.println("<" + objectName + ">");
      for (int i = 1; i <= size; i++) {
        Object object = results.getObject(i);
        String name = meta.getColumnName(i);
        if (null != object) {
          out.print("  <" + name + ">");
          if (object instanceof Timestamp) {
            Timestamp time = (Timestamp) object;
            out.print(time.getTime());
          } else {
            out.print(escape(object.toString()));
          }
          out.println("</" + name + ">");
        }
      }
      out.println("</" + objectName + ">");
    }
  }

  /**
   * Encode special characters in output.
   */
  private static String escape(String in) {
    StringBuffer out = new StringBuffer();
    StringTokenizer t = new StringTokenizer(in, "\0<>&", true);
    boolean nullBytes = false;
    while (t.hasMoreTokens()) {
      String token = t.nextToken();
      if (token.equals("\0")) {
        nullBytes = true;
      } else if ("<".equals(token) || ">".equals(token) || "&".equals(token)) {
        out.append("&#x").append(Integer.toHexString(token.charAt(0))).append(";");
      } else {
        out.append(token);
      }
    }

    // Had we some errors in our data?
    if (nullBytes) {
      System.err.println("Found null-byte in data: removing it.");
    }
    return out.toString();
  }

  private static DocumentBuilder documentBuilder = null;
  public static Document getBackupDocument() {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (FactoryConfigurationError error) {
      System.err.println("Unable to create document builder factory: " + error);
    } catch (ParserConfigurationException e) {
      System.err.println("Unable to create document builder");
      e.printStackTrace();
    }

    Document backupDoc = documentBuilder.newDocument();
    Node root = backupDoc.appendChild(backupDoc.createElement("snipspace"));

    SnipSpace space = (SnipSpace)Components.getComponent(SnipSpace.class);
    SnipSerializer serializer = SnipSerializer.getInstance();
    Iterator snipListIterator = space.getAll().iterator();
    while (snipListIterator.hasNext()) {
      Snip snip = (Snip) snipListIterator.next();
      Node node = serializer.serialize(backupDoc, snip);
      root.appendChild(node);
    }
    return backupDoc;
  }
}
