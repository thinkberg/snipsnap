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
    // TODO: check if label with same name exists
    // additional parameter 'overwrite' or exception or return value?
    // (decision should to be made by user)
    cache = null;
    labels.put(label.getName(), label);
  }

  public void addLabel(String name, String value) {
    // TODO: check if label with same name exists
    // additional parameter 'overwrite' or exception or return value?
    // (decision should to be made by user)
    cache = null;
    Label label = createDefaultLabel(name, value);
    labels.put(name, label);
  }

  public Label getLabel(String name) {
    return (Label) labels.get(name);
  }

  public Collection getAll() {
    return labels.values();
  }

  public void removeLabel(String name) {
    cache = null;
	  labels.remove(name);
  }

  public Set getIds() {
    return labels.keySet();
  }

  private Label createDefaultLabel(String name, String value)
  {
    Label label = LabelManager.getInstance().getDefaultLabel();
    label.setName( name );
    label.setValue( value );
    return label;
  }

  private void deserialize(String labelString) {
    labels = new HashMap();
    if ("".equals(labelString)) return;

    StringTokenizer tokenizer = new StringTokenizer(labelString, "|");
    while (tokenizer.hasMoreTokens()) {
      String labelToken = tokenizer.nextToken();
      Label label = createDefaultLabel( getName( labelToken ), getValue( labelToken ) );
      labels.put( label.getName(), label );
    }
    return;
  }

  private String serialize() {
    if (null == labels || labels.isEmpty()) return "";

    StringBuffer linkBuffer = new StringBuffer();
    Iterator iterator = labels.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      String name = (String) entry.getKey();
      Label label = (Label) entry.getValue();
      String type = label.getType();
      String value = label.getValue();
      linkBuffer.append(name);
      linkBuffer.append(":");
      linkBuffer.append(value);
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

  private String getName(String labelString) {
    return before(labelString, ":");
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
