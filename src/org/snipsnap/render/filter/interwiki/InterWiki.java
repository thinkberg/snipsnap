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

package org.snipsnap.render.filter.interwiki;

import org.snipsnap.snip.SnipLink;
import org.radeox.util.logging.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Stores information and links to other wikis forming a
 * InterWiki
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class InterWiki {
  private static InterWiki instance;
  private Map interWiki;

  public static synchronized InterWiki getInstance() {
    if (null == instance) {
      instance = new InterWiki();
    }
    return instance;
  }

  public InterWiki() {
    interWiki = new HashMap();
    interWiki.put("LCOM", "http://www.langreiter.com/space/");
    interWiki.put("ESA", "http://earl.strain.at/space/");
    interWiki.put("C2", "http://www.c2.com/cgi/wiki?");
    interWiki.put("WeblogKitchen", "http://www.weblogkitchen.com/wiki.cgi?");
    interWiki.put("Meatball", "http://www.usemod.com/cgi-bin/mb.pl?");
    interWiki.put("SnipSnap", "http://snipsnap.org/space/");

    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream("conf/intermap.txt")));
      addInterMap(br);
    } catch (IOException e) {
      Logger.warn("Unable to read conf/intermap.txt");
    }
  }

  public void addInterMap(BufferedReader reader) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      int index = line.indexOf(" ");
      interWiki.put(line.substring(0, index), line.substring(index + 1));
    }
    ;
  }

  public Writer appendTo(Writer writer) throws IOException {
    Iterator iterator = interWiki.entrySet().iterator();
    writer.write("{table}\n");
    writer.write("Wiki|Url\n");
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
    return interWiki.containsKey(external);
  }

  public Writer expand(Writer writer, String external, String snip) throws IOException {
    writer.write("<a href=\"");
    writer.write((String) interWiki.get(external));
    writer.write(SnipLink.encode(snip));
    writer.write("\">");
    writer.write(snip);
    writer.write("@");
    writer.write(external);
    writer.write("</a>");
    return writer;
  }
}
