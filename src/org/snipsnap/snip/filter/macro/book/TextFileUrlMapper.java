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

package org.snipsnap.snip.filter.macro.book;

import org.snipsnap.snip.SnipLink;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Manages links to keys, mapping is read from a text file
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public abstract class TextFileUrlMapper implements UrlMapper {
  protected static UrlMapper instance;
  private Map services;

  public abstract String getFileName();

  public abstract String getKeyName();

  public TextFileUrlMapper() {
    services = new HashMap();

    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream(getFileName())));
      addInterMap(br);
    } catch (IOException e) {
      System.err.println("Unable to read "+getFileName());
    }
  }

  public void addInterMap(BufferedReader reader) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      if (!line.startsWith("#")) {
        int index = line.indexOf(" ");
        services.put(line.substring(0, index), SnipLink.escape(line.substring(index + 1)));
      }
    }
  }

  public Writer appendTo(Writer writer) throws IOException {
    Iterator iterator = services.entrySet().iterator();
    writer.write("{table}\n");
    writer.write("Service|Url\n");
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      writer.write((String) entry.getKey());
      writer.write("|");
      writer.write((String) entry.getValue());
      writer.write("\n");
    }
    writer.write("{table}");
    return writer;
  }

  public boolean contains(String external) {
    return services.containsKey(external);
  }

  public Writer appendUrl(Writer writer, String key)
      throws IOException {
    if (services.size() == 0) {
      writer.write(getKeyName());
      writer.write(":");
      writer.write(key);
    } else {

      SnipLink.appendImage(writer, "external-link", "&gt;&gt;");
      writer.write("(");
      Iterator iterator = services.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry entry = (Map.Entry) iterator.next();
        writer.write("<a href=\"");
        writer.write((String) entry.getValue());
        writer.write(key);
        writer.write("\">");
        writer.write((String) entry.getKey());
        writer.write("</a>");
        if (iterator.hasNext()) {
          writer.write(" &#x7c; ");
        }
      }
      writer.write(")");
    }
    return writer;
  }
}
