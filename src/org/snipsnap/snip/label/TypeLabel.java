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

import org.radeox.util.i18n.ResourceManager;
import org.radeox.util.Service;
import org.snipsnap.app.Application;
import org.snipsnap.net.ServletPluginLoader;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

public class TypeLabel extends BaseLabel {
  private final static String COMMENT_CHAR = "#";
  private static List types = getTypes();

  private String type;
  private String viewHandler;
  private String editHandler;

  public TypeLabel() {
    super();
  }

  public TypeLabel(String name, String value) {
    setName(name);
    setValue(value);
  }

  public String getType() {
    return "TypeLabel";
  }

  public void setValue(String value) {
    if (null != value && !"".equals(value)) {
      String values[] = value.split(",");
      type = (values.length > 0 ? values[0] : "");
      editHandler = (values.length > 1 ? values[0] : "");
      viewHandler = (values.length > 2 ? values[1] : "");
    }
    super.setValue(value);
  }

  public String getValue() {
    return
      (isNull(type) ? "" : type) +
      (isNull(viewHandler) ? (isNull(editHandler) ? "" : ",") : "," + viewHandler) +
      (isNull(editHandler) ? "" : "," + editHandler);
  }

  private boolean isNull(String var) {
    return var == null || "".equals(var);
  }

  public String getTypeValue() {
    return type;
  }

  public String getViewHandler() {
    return viewHandler;
  }

  public String getEditHandler() {
    return editHandler;
  }

  public String getInputProxy() {
    StringBuffer buffer = new StringBuffer();
    if (Application.get().getUser().isAdmin()) {
      buffer.append("<input type=\"hidden\" name=\"label.name\" value=\"mime-type\"/>");
      buffer.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"2\">");
      buffer.append("<tr>");
      buffer.append("<td>");
      buffer.append(ResourceManager.getString("i18n.messages", "label.mimetype.type"));
      buffer.append("</td>");
      buffer.append("<td>");

      Iterator iterator = types.iterator();
      buffer.append("<select name=\"label.type\" size=\"1\">");
      while (iterator.hasNext()) {
        String typeString = (String) iterator.next();
        buffer.append("<option");
        if (typeString.equals(type)) {
          buffer.append(" selected=\"selected\"");
        }
        buffer.append(">");
        // @TODO: Check if type == value
        buffer.append(type);
        buffer.append("</option>");
      }
      buffer.append("</select>");

      buffer.append("</tr><tr>");
      buffer.append("<td>");
      buffer.append(ResourceManager.getString("i18n.messages", "label.mimetype.view"));
      buffer.append("</td>");
      buffer.append("<td>");

      Map handlers = ServletPluginLoader.getPlugins();
      // add view handlers
      buffer.append("<select name=\"label.viewhandler\" size=\"1\">");
      buffer.append("<option value=\"\">");
      buffer.append(ResourceManager.getString("i18n.messages", "label.mimetype.nohandler"));
      buffer.append("</option>");
      Iterator it = handlers.keySet().iterator();
      while (it.hasNext()) {
        String handlername = (String) it.next();
        buffer.append("<option");
        if (handlername.equals(getViewHandler())) {
          buffer.append(" selected=\"selected\"");
        }
        buffer.append(">");
        buffer.append(handlername);
        buffer.append("</option>");
      }

      buffer.append(handlers);
      buffer.append("</select>");
      buffer.append("</td>");
      buffer.append("</tr><tr>");
      buffer.append("<td>");
      buffer.append(ResourceManager.getString("i18n.messages", "label.mimetype.edit"));
      buffer.append("</td>");
      buffer.append("<td>");

      // add edit handlers
      buffer.append("<select name=\"label.edithandler\" size=\"1\">");
      buffer.append("<option value=\"\">");
      buffer.append(ResourceManager.getString("i18n.messages", "label.mimetype.nohandler"));
      buffer.append("</option>");
      it = handlers.keySet().iterator();
      while (it.hasNext()) {
        String handlername = (String) it.next();
        buffer.append("<option");
        if (handlername.equals(getEditHandler())) {
          buffer.append(" selected=\"selected\"");
        }
        buffer.append(">");
        buffer.append(handlername);
        buffer.append("</option>");
      }
      buffer.append("</select>");
      buffer.append("</td></tr></table>");
    } else {
      buffer.append(ResourceManager.getString("i18n.messages", "label.mimetype.adminonly"));
    }

    return buffer.toString();
  }

  public String getListProxy() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<td>");
    buffer.append(name);
    buffer.append("</td><td>");
    buffer.append(type);
    buffer.append("</td>");
    return buffer.toString();
  }

  public void handleInput(Map input) {
    // ADMIN ONLY!
    if (Application.get().getUser().isAdmin()) {
      super.handleInput(input);
      if (input.containsKey("label.type")) {
        type = (String) input.get("label.type");
      }
      if (input.containsKey("label.viewhandler")) {
        viewHandler = (String) input.get("label.viewhandler");
      }
      if (input.containsKey("label.edithandler")) {
        editHandler = (String) input.get("label.edithandler");
      }
      // tricky ... set the actual value by using our own getValue() method
      super.setValue(getValue());
    }
  }

  private static List getTypes() {
    List result = new LinkedList();
    Iterator iter = Service.providerNames(TypeLabel.class);

    while (iter.hasNext()) {
      String curTypeName = (String) iter.next();
      if (isValid(curTypeName)) {
        result.add(curTypeName);
      }
    }
    return result;
  }

  private static boolean isValid(String str) {
    if (str == null) {
      return false;
    }

    String line = str.trim();
    return !(line.startsWith(COMMENT_CHAR)
      || line.equals(""));
  }
}
