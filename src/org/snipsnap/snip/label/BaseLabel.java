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

import java.util.Map;

/**
 * Base class for Labels with simple default behaviour
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public abstract class BaseLabel implements Label {
  protected String name;
  protected String value;
  protected Snip snip;

  public BaseLabel() {
    name = "";
    value = "";
  }

  public BaseLabel(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public void setSnip(Snip snip) {
    this.snip = snip;
  }

  public Snip getSnip() {
    return snip;
  }

  public void create() {
  }

  public void remove() {
  }

  public void change() {
  }

  public String getInputProxy() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<input type=\"text\" value=\"");
    buffer.append(name);
    buffer.append("\" name=\"label.name\"/>");
    buffer.append("<input type=\"text\" value=\"");
    buffer.append(value);
    buffer.append("\" name=\"label.value\"/>");
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

  public void handleInput(Map input) {
    if (input.containsKey("label.name")) {
      this.name = (String) input.get("label.name");
    }
    if (input.containsKey("label.value")) {
      this.value = (String) input.get("label.value");
    }
  }

  public abstract String getType();

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public void index(Document document) {
    //System.out.println("Label index: " + name + ", " + value);
    document.add(Field.Text(name, value));
  }
}
