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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Manages links to and from snips. Links can be external to internal
 * and internal to internal.
 *
 * @author stephan
 * @version $Id$
 */

public class Links {
  private Map linkMap;
  private String cache = null;

  public Links() {
    linkMap = new HashMap();
  }

  public Links(String links) {
   cache = links;
   linkMap = deserialize(links);
  }

  public void addLink(String url) {
    cache = null;
    if (linkMap.containsKey(url)) {
      int currentCount = ((Integer) linkMap.get(url)).intValue();
      currentCount++;
      linkMap.put(url, new Integer(currentCount));
    } else {
      linkMap.put(url, new Integer(0));
    }
  }

  public Map deserialize(String links) {
    if ("".equals(links)) return new HashMap();

    Map linkcounts = new HashMap();

    StringTokenizer tokenizer = new StringTokenizer(links, "|");
    while (tokenizer.hasMoreTokens()) {
      String urlString = tokenizer.nextToken();
      Integer count = getCount(urlString);
      String url = getUrl(urlString);
      linkcounts.put(url, count);
    }
   return linkcounts;
  }

  private String serialize() {
    if (null == linkMap || linkMap.isEmpty()) return "";

    StringBuffer linkBuffer = new StringBuffer();
    Iterator iterator = linkMap.keySet().iterator();
    while (iterator.hasNext()) {
      String url = (String) iterator.next();
      linkBuffer.append(url);
      linkBuffer.append(":");
      Integer count = (Integer) linkMap.get(url);
      linkBuffer.append(count);
      if (iterator.hasNext()) {
        linkBuffer.append("|");
      }
    }
    return linkBuffer.toString();
  }

  private String after(String string, String delimiter) {
    return string.substring(string.indexOf(delimiter) + 1);
  }

  private String before(String string, String delimiter) {
    return string.substring(0, string.indexOf(delimiter));
  }

  private String getUrl(String rolesString) {
    return before(rolesString, ":");
  }

  private Integer getCount(String urlString) {
    return Integer.getInteger(after(urlString, ":"));
  }

  public String toString() {
    if (null == cache ) {
      cache = serialize();
    }
    return cache;
  }
}
