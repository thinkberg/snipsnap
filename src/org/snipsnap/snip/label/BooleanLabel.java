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

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.snipsnap.snip.Snip;
import org.snipsnap.serialization.LabelContext;

import java.util.Map;

/**
 * Boolean Label for true/false functionality
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public abstract class BooleanLabel implements Label {
  protected String name;
  protected String value;
  protected Snip snip;

  public BooleanLabel() {
    name = "";
    value = "false";
  }

  public BooleanLabel(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public void create() {
  }

  public void remove() {
  }

  public void change() {
  }


  public LabelContext getContext() {
    return new LabelContext(snip, this);
  }

  public void setSnip(Snip snip) {
    this.snip = snip;
  }

  public Snip getSnip() {
    return snip;
  }

  public String getInputProxy() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<input type=\"checkbox\" value=\"");
    buffer.append(value);
    buffer.append("\" name=\"label.boolean\"/>");
    return buffer.toString();
  }

  public String getListProxy() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<td>");
    buffer.append(name);
    buffer.append("</td><td>");
    buffer.append(value);
    buffer.append("</td>");
    return buffer.toString();
  }

  protected String checkValue(String value) {
    String lcValue = value.toLowerCase();
    if ("true".equals(lcValue) || "yes".equals(lcValue) ||
        "false".equals(lcValue) || "no".equals(lcValue)) {
      return lcValue;
    } else {
      return "false";
    }
  }

  public void handleInput(Map input) {
    if (input.containsKey("label.boolean")) {
      this.value = checkValue((String) input.get("label.boolean"));
    }
  }

  public String getType() {
    return "Boolean";
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public boolean isTrue() {
    return "true".equals(value) || "yes".equals(value);
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(String value) {
    this.value = checkValue(value);
  }

  public void index(Document document) {
    //System.out.println("Label index: " + name + ", " + value);
    document.add(Field.Text(name, "" + value));
  }
}
