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

package org.snipsnap.snip.label;

import java.util.*;

/**
 * Stores Label information for a snip
 *
 * @author stephan
 * @version $Id$
 */

public class Labels {
  private Map labels;
  private String cache;

  public Labels() {
    this.labels = new HashMap();
  }

  public Labels(String labelString) {
    cache = labelString;
    deserialize(labelString);
  }

  public void addLabel(Label label) {
  }

  public void addLabel(String name, String value) {
    cache = null;
    labels.put(name, value);
  }

  public Label getLabel(String name) {
    return (Label)labels.get(name);
  }

  public Set getIds() {
    return labels.keySet();
  }

  public void deserialize(String labelString) {
    labels = new HashMap();
    if ("".equals(labelString)) return;

    StringTokenizer tokenizer = new StringTokenizer(labelString, "|");
    while (tokenizer.hasMoreTokens()) {
      String label = tokenizer.nextToken();
      String value = getValue(label);
      String name = getName(label);
      labels.put(name, value);
    }
    return;
  }

  private String serialize() {
    if (null == labels || labels.isEmpty()) return "";

    StringBuffer linkBuffer = new StringBuffer();
    Iterator iterator = labels.keySet().iterator();
    while (iterator.hasNext()) {
      String url = (String) iterator.next();
      linkBuffer.append(url);
      linkBuffer.append(":");
      Integer count = (Integer) labels.get(url);
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

  private String getName(String rolesString) {
    return before(rolesString, ":");
  }

  private String getValue(String labelString) {
    return after(labelString, ":");
  }

  public String toString() {
    if (null == cache) {
      cache = serialize();
    }
    return cache;
  }

}
