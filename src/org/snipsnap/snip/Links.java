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

import java.io.UTFDataFormatException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
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
  private SortedSet links;

  public Links() {
    cache = "";
  }

  public Links(String links) {
    cache = links;
  }

  public int getSize() {
    if (null == linkMap) {
      linkMap = deserialize(cache);
    }
    return linkMap.size();
  }


  public void addLink(String url) {
    if (null == linkMap) {
      linkMap = deserialize(cache);
    }
    cache = null;

    if (linkMap.containsKey(url)) {
      int currentCount = 0;
      Integer tmp = ((Integer) linkMap.get(url));
      if (tmp != null) {
        currentCount = tmp.intValue();
      }
      currentCount++;
      linkMap.put(url, new Integer(currentCount));
    } else {
      linkMap.put(url, new Integer(1));
    }

    // If there is a sorted cached key set, then add url
    if (null != links) {
      // remove url first to force resorting
      if (links.contains(url)) {
        links.remove(url);
      }
      links.add(url);
    }
  }

  /**
   * Return an iterator to Links. The iterator is sorted
   * by the count in the linkMap map. The sorted keyset is
   * only generated on demand.
   *
   * @return Iterator over the urls of Links
   */
  public Iterator iterator() {
    if (null == linkMap) {
      linkMap = deserialize(cache);
    }
    List keys = new LinkedList(linkMap.keySet());
    Collections.sort(keys, new Comparator() {
      public int compare(Object o1, Object o2) {
        // use "-" because we want decreasing order
        return -((Integer) linkMap.get(o1)).compareTo(((Integer) linkMap.get(o2)));
      }
    });
    return keys.iterator();
  }

  public int getIntCount(String url) {
    if (null == linkMap) {
      linkMap = deserialize(cache);
    }
    int currentCount = 0;
    if (linkMap.containsKey(url)) {
      currentCount = ((Integer) linkMap.get(url)).intValue();
    } else {
      currentCount = -1;
    }
    return currentCount;
  }

  public Map newLinkMap() {
    return new HashMap();
  }

  public Map deserialize(String links) {
    if ("".equals(links)) {
      return newLinkMap();
    }

    Map linkcounts = newLinkMap();
    boolean errors = false;
    StringTokenizer tokenizer = new StringTokenizer(links, "|");
    while (tokenizer.hasMoreTokens()) {
      String urlString = tokenizer.nextToken();
      try {
        Integer count = getCount(urlString);
        String url = getUrl(urlString);
        for (int c = 0; c < url.length(); c++) {
          char ch = urlString.charAt(c);
          if (ch < 0x20 && !(ch == 0x0a || ch == 0x0d || ch == 0x09)) {
            errors = true;
            Logger.warn("ignoring '" + urlString + "' while deserializing: illegal character");
            break;
          }
        }
        linkcounts.put(url, count);
      } catch (Exception e) {
        errors = true;
        Logger.warn("ignoring '"+urlString+"' while deserializing, format errors");
        break;
      }
    }
    // make sure correct data is in the cache
    if (errors) {
      cache = null;
    }
    return linkcounts;
  }

  private String serialize() {
    if (null == linkMap || linkMap.isEmpty()) {
      return "";
    }

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
    // Split at the last delimiter, e.g. "I:want:some:coke:3"
    // splits "I:want:some:coke" and "3"
    return string.substring(string.lastIndexOf(delimiter) + 1);
  }

  private String before(String string, String delimiter) {
    return string.substring(0, string.lastIndexOf(delimiter));
  }

  private String getUrl(String rolesString) {
    return before(rolesString, ":");
  }

  private Integer getCount(String urlString) {
    try {
      return new Integer(after(urlString, ":"));
    } catch (NumberFormatException e) {
      return new Integer(1);
    }
  }

  public String toString() {
    if (null == cache) {
      cache = serialize();
    }
    return cache;
  }
}
