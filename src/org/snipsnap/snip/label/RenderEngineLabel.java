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
import org.snipsnap.container.Components;
import org.radeox.engine.RenderEngine;

import java.util.Map;

/**
 * Boolean Label for true/false functionality
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class RenderEngineLabel implements Label {
  protected String name;
  protected String engine;

  public RenderEngineLabel() {
    name = "";
    engine = Components.DEFAULT_ENGINE;
  }

  public RenderEngineLabel(String name, String engine) {
    this.name = name;
    this.engine = engine;
  }

  public String getInputProxy() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<select name=\"label.renderEngine\">");
    buffer.append("<option value=\"org.snipsnap.render.PlainTextRenderEngine\">");
    buffer.append("Plain Text</option>");
    buffer.append("<option value=\"defaultRenderEngine\">default</option>");
    buffer.append("</select>");
    return buffer.toString();
  }

  public String getListProxy() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<td>");
    buffer.append(name);
    buffer.append("</td><td>");
    buffer.append(engine.substring(engine.lastIndexOf(".")));
    buffer.append("</td>");
    return buffer.toString();
  }

  public void handleInput(Map input) {
    if (input.containsKey("label.renderEngine")) {
      this.engine = (String) input.get("label.renderEngine");
    }
  }

  public String getType() {
    return "RenderEngine";
  }

  public String getName() {
    return name;
  }

  public String getValue() {
    return engine;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setValue(String value) {
    this.engine = value;
  }

  public void index(Document document) {
    System.out.println("Label index: " + name + ", " + engine);
    document.add(Field.Text(name, "" + engine));
  }
}