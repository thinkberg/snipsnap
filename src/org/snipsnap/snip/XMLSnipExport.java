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

import org.snipsnap.util.ConnectionManager;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.StringTokenizer;

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
   * @param document the document to load from
   * @param overwrite whether or not to overwrite existing content
   */
  public static void store(OutputStream out, int exportMask) throws IOException {

    Connection connection = ConnectionManager.getConnection();
    ResultSet results = null;

    try {
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(out, "iso-8859-1"));
      pw.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\" ?>");
      pw.println("<snipspace>");

      if((exportMask & USERS) != 0) toXml("SnipUser", "user", connection, pw);
      if((exportMask & SNIPS) != 0) toXml("Snip", "snip", connection, pw);

      pw.println("</snipspace>");
      pw.flush();
      pw.close();
    } catch (Exception e) {
      System.err.println("error writing output");
      e.printStackTrace();
    }
    System.err.println("ATTENTION: Check the encoding of the file!");
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
      System.err.println("Problems with query ");
      e.printStackTrace();
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
   * Escape special characters in output.
   */
  private static String escape(String in) {
    StringBuffer out = new StringBuffer();
    StringTokenizer t = new StringTokenizer(in, "<>&", true);
    while (t.hasMoreTokens()) {
      String token = t.nextToken();
      if ("<".equals(token) || ">".equals(token) || "&".equals(token)) {
        out.append("&#x").append(Integer.toHexString(token.charAt(0))).append(";");
      } else {
        out.append(token);
      }
    }
    return out.toString();
  }
}
