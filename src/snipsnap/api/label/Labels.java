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

package snipsnap.api.label;

import org.snipsnap.util.StringUtil;
import snipsnap.api.snip.Snip;
import snipsnap.api.container.Components;
import org.snipsnap.snip.label.LabelManager;
import snipsnap.api.app.Application;

import java.util.*;
import java.util.logging.Logger;

/**
 * Stores Label information for a snip
 *
 * @author stephan
 * @version $Id: Labels.java 1609 2004-05-18 13:28:38Z stephan $
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
    Map map = (Map) this.labels.get(label.getName());
    if (map == null) {
      map = new HashMap();
      this.labels.put(label.getName(), map);
    }
    map.put(label.getValue(), label);
  }

  public void addLabel(String name, String value) {
    // TODO: check if label with same name exists
    // additional parameter 'overwrite' or exception or return value?
    // (decision should to be made by user)
    cache = null;
    snipsnap.api.label.Label label = createDefaultLabel(name, value);
    addLabel(label);
  }

  public Label getLabel(String name) {
    Map map = (Map) this.labels.get(name);
    if (map == null) return null;
    Iterator it = map.values().iterator();
    return it.hasNext() ? (snipsnap.api.label.Label) it.next() : null;
  }

  public snipsnap.api.label.Label getLabel(String name, String value) {
    Map map = (Map) this.labels.get(name);
    if (map == null) return null;
    return (snipsnap.api.label.Label) map.get(value);
  }

  public Collection getAll() {
    Collection result = new ArrayList();

    Iterator iterator = this.labels.values().iterator();
    while (iterator.hasNext()) {
      Map map = (Map) iterator.next();
      result.addAll(map.values());
    }
    return result;
  }

  public Collection getLabels(String type) {
    ArrayList result = new ArrayList();
    if (null == type) {
      return result;
    }

    Iterator iterator = this.labels.values().iterator();
    while (iterator.hasNext()) {
      Map map = (Map) iterator.next();
      Iterator it = map.values().iterator();
      while (it.hasNext()) {
        snipsnap.api.label.Label label = (Label) it.next();
        if (null != label && type.equals(label.getType())) {
          result.add(label);
        }
      }
    }
    return result;
  }

  public void removeLabel(String name, String value) {
    cache = null;
    Map map = (Map) labels.get(name);
    if (map != null) {
      snipsnap.api.label.Label label = (Label) map.get(value);
      label.remove();
      map.remove(value);
    }
  }

  private Label createDefaultLabel(String name, String value) {
    snipsnap.api.label.Label label = ((LabelManager) snipsnap.api.container.Components.getComponent(LabelManager.class)).getDefaultLabel();
    label.setName(name);
    label.setValue(value);
    return label;
  }

  private snipsnap.api.label.Label createLabel(String type, String name, String value) {
    // TODO: ? throw an exception (e.g. LabelTypeUnkownException ) ?
    snipsnap.api.label.Label label = ((LabelManager) Components.getComponent(LabelManager.class)).getLabel(type);
    if (label != null) {
      label.setName(name);
      label.setValue(value);

    } else {
      System.err.println("Labels: Label not found: "+type);
    }
    return label;
  }

  private void deserialize(snipsnap.api.snip.Snip snip, String labelString) {
    labels = new HashMap();
    if (null == labelString || "".equals(labelString)) {
      return;
    }

    StringTokenizer tokenizer = new StringTokenizer(labelString, "|");
    while (tokenizer.hasMoreTokens()) {
      String labelToken = tokenizer.nextToken();
      String[] data = StringUtil.split(labelToken, ":");
      //System.out.println("Data="+data);
      if (data.length == 3) {
        snipsnap.api.label.Label label = createLabel(data[0], data[1], data[2]);
        label.setSnip(snip);
        addLabel(label);
      } else {
        System.err.println("Labels: Broken Label: '" + labelToken + "' ignored");
      }
    }
    return;
  }

  private String serialize() {
    if (null == this.labels || this.labels.isEmpty()) {
      return "";
    }

    StringBuffer linkBuffer = new StringBuffer();
    Iterator iterator = this.labels.entrySet().iterator();
    while (iterator.hasNext()) {
      Map.Entry entry = (Map.Entry) iterator.next();
      String name = (String) entry.getKey();
      Map map = (Map) entry.getValue();
      Iterator it = map.values().iterator();
      while (it.hasNext()) {
        snipsnap.api.label.Label label = (snipsnap.api.label.Label) it.next();
        String type = label.getType();
        String value = label.getValue();
        linkBuffer.append(type);
        linkBuffer.append(":");
        linkBuffer.append(name);
        linkBuffer.append(":");
        linkBuffer.append(value);

        linkBuffer.append("|");
      }
    }
    //System.out.println("serialize = "+linkBuffer.toString());
    // remove last '|'
    if (linkBuffer.length() > 0) {
      linkBuffer.setLength(linkBuffer.length() - 1);
    }
    return linkBuffer.toString();
  }

  public String toString() {
    // always force serialization to ensure changed labels are reflected
    return (cache = serialize());
  }
}
