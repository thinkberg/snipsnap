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

import org.radeox.util.Service;
import org.radeox.util.i18n.ResourceManager;
import org.snipsnap.app.Application;
import org.snipsnap.net.ServletPluginLoader;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TypeLabel extends BaseLabel {
  private static Map types = null;

  /**
   * Get known types and their handlers (potentially no handlers)
   * The syntaxt of the file is "<type> <viewhandler>:<edithandler>"
   *
   * @return a map of the structure type:{viewhandler,edithandler}
   */
  private static Map getTypeMap() {
    if (types == null) {
      types = new HashMap();
      Iterator iter = Service.providerNames(TypeLabel.class);

      while (iter.hasNext()) {
        String typeInfo = ((String) iter.next()).trim();
        if (!"".equals(typeInfo) && !typeInfo.startsWith("#")) {
          String[] entry = typeInfo.split("\\p{Space}+|\\{Space}*:\\{Space}*");
          String[] handlers = new String[2];
          switch (entry.length) {
            case 3:
              handlers[1] = entry[2];
            case 2:
              handlers[0] = entry[1];
            case 1:
              types.put(entry[0], handlers);
              break;
          }
        }
      }
    }
    return types;
  }

  /**
   * Get known types.
   * @return a set of type names
   */
  public static Set getTypes() {
    return getTypeMap().keySet();
  }

  /**
   * Get a view handler for the provided type name.
   * @param type the type name
   * @return a view handler or null
   */
  public static String getViewHandler(String type) {
    String[] handlers = (String[]) getTypeMap().get(type);
    if(null != handlers) {
      return handlers[0];
    }
    return null;
  }

  /**
   * Get an edit handler for the provided type name.
   * @param type the type name
   * @return an edit handler or null
   */
  public static String getEditHandler(String type) {
    String[] handlers = (String[]) getTypeMap().get(type);
    if (null != handlers) {
      return handlers[1];
    }
    return null;
  }

  private String type;
  private String viewHandler;
  private String editHandler;

  public TypeLabel() {
    name = "Type";
    setValue(value);
  }

  public TypeLabel(String value) {
    this();
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
      buffer.append("<input type=\"hidden\" name=\"label.name\" value=\"");
      buffer.append(name);
      buffer.append("\"/>");
      buffer.append("<table border=\"0\" cellpadding=\"0\" cellspacing=\"2\">");
      buffer.append("<tr>");
      buffer.append("<td>");
      buffer.append(ResourceManager.getString("i18n.messages", "label.type.type"));
      buffer.append("</td>");
      buffer.append("<td>");

      Iterator iterator = getTypes().iterator();
      buffer.append("<select name=\"label.type\" size=\"1\">");
      while (iterator.hasNext()) {
        String typeString = (String) iterator.next();
        buffer.append("<option");
        if (typeString.equals(type)) {
          buffer.append(" selected=\"selected\"");
        }
        buffer.append(">");
        // @TODO: Check if type == value
        buffer.append(typeString);
        buffer.append("</option>");
      }
      buffer.append("</select>");

      buffer.append("</tr><tr>");
      buffer.append("<td>");
      buffer.append(ResourceManager.getString("i18n.messages", "label.type.view"));
      buffer.append("</td>");
      buffer.append("<td>");

      Map handlers = ServletPluginLoader.getPlugins();
      // add view handlers
      buffer.append("<select name=\"label.viewhandler\" size=\"1\">");
      buffer.append("<option value=\"\">");
      buffer.append(ResourceManager.getString("i18n.messages", "label.type.nohandler"));
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
      buffer.append(ResourceManager.getString("i18n.messages", "label.type.edit"));
      buffer.append("</td>");
      buffer.append("<td>");

      // add edit handlers
      buffer.append("<select name=\"label.edithandler\" size=\"1\">");
      buffer.append("<option value=\"\">");
      buffer.append(ResourceManager.getString("i18n.messages", "label.type.nohandler"));
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
      buffer.append(ResourceManager.getString("i18n.messages", "label.type.adminonly"));
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

}
