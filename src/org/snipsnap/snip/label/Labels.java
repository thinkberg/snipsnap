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

import org.snipsnap.util.StringUtil;
import org.snipsnap.snip.Snip;
import org.snipsnap.container.Components;
import org.snipsnap.app.Application;

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

  public Labels(Snip snip, String labelString) {
    cache = labelString;
    deserialize(snip, labelString);
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

  public Collection getLabels(String type) {
    ArrayList result = new ArrayList();
    if (null == type) {
      return result;
    }

    Iterator iterator = this.labels.values().iterator();
    while (iterator.hasNext()) {
      Label label = (Label) iterator.next();
      if(null != label && type.equals(label.getType())) {
        result.add(label);
      }
    }
    return result;
  }

  public void removeLabel(String name) {
    cache = null;
    Label label = (Label) labels.get(name);
    label.remove();
    labels.remove(name);
  }

  public Set getIds() {
    return labels.keySet();
  }

  private Label createDefaultLabel(String name, String value) {
    Label label = ((LabelManager)Components.getComponent(LabelManager.class)).getDefaultLabel();
    label.setName(name);
    label.setValue(value);
    return label;
  }

  private Label createLabel(String type, String name, String value) {
    Label label = ((LabelManager) Components.getComponent(LabelManager.class)).getLabel(type);
    label.setName(name);
    label.setValue(value);
    return label;
  }

  private void deserialize(Snip snip, String labelString) {
    labels = new HashMap();
    if (null == labelString || "".equals(labelString)) { return; }

    StringTokenizer tokenizer = new StringTokenizer(labelString, "|");
    while (tokenizer.hasMoreTokens()) {
      String labelToken = tokenizer.nextToken();
      String[] data = StringUtil.split(labelToken, ":");
      //System.out.println("Data="+data);
      if(data.length == 3) {
        Label label = createLabel(data[0], data[1], data[2]);
        label.setSnip(snip);
        labels.put(label.getName(), label);
      } else {
        System.err.println("Labels: Broken Label: '" + labelToken + "' ignored");
      }
    }
    return;
  }

  private String serialize() {
    if (null == labels || labels.isEmpty()) { return ""; }

    StringBuffer linkBuffer = new StringBuffer();
    Iterator iterator = labels.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      String name = (String) entry.getKey();
      Label label = (Label) entry.getValue();
      String type = label.getType();
      String value = label.getValue();
      linkBuffer.append(type);
      linkBuffer.append(":");
      linkBuffer.append(name);
      linkBuffer.append(":");
      linkBuffer.append(value);
      if (iterator.hasNext()) {
        linkBuffer.append("|");
      }
    }
    //System.out.println("serialize = "+linkBuffer.toString());
    return linkBuffer.toString();
  }

  public String toString() {
    // always force serialization to ensure changed labels are reflected
    return (cache = serialize());
  }
}
