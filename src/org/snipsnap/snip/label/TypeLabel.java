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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.radeox.util.Service;

/**
 * TypeLabel assigns a type to a Snip
 * @author Marco Mosconi
 * @version $Id$
 */
public class TypeLabel extends BaseLabel {
  private final static String COMMENT_CHAR = "#";
  
  private static List types = getTypes();
  
  public TypeLabel() {
    name = "Type";
    value = "";
  }

  public TypeLabel(String value) {
    this();
    this.value = value;
  }

  public String getType() {
    return "TypeLabel";
  }

  public String getInputProxy() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<input type=\"hidden\" value=\"");
    buffer.append(name);
    buffer.append("\" name=\"label.name\"/>");
    buffer.append("Type: ");
    Iterator iterator = types.iterator();
    buffer.append("<select name=\"label.value\" size=\"1\">");
    while (iterator.hasNext()) {
      String type = (String) iterator.next();
      buffer.append("<option");
      if(type.equals(value)) {
        buffer.append(" selected=\"selected\"");
      }
      buffer.append(">");
      // @TODO: Check if type == value
      buffer.append(type);
      buffer.append("</option>");
    }
    buffer.append("</select>");
    return buffer.toString();
  }

  public String getListProxy() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<td>");
    buffer.append("Type");
    buffer.append("</td><td>");
    buffer.append(value);
    buffer.append("</td>");
    return buffer.toString();
  }

  public void setName(String name) {
    // name should not be set manually
  }
  
  private static List getTypes() {
    List     result = new LinkedList();
	Iterator iter   = Service.providerNames(TypeLabel.class);

	while (iter.hasNext()) {
	  String curTypeName = ((String) iter.next()).trim();
	  if (!curTypeName.startsWith(COMMENT_CHAR)) {
	    result.add(curTypeName);
	  }
    }
    return result;
  }
}