/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002,2003 Fraunhofer Gesellschaft
 * Fraunhofer Institut for Computer Architecture and Software Technology
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
package org.snipsnap.net;

import org.radeox.util.Service;
import org.radeox.util.logging.Logger;
import org.snipsnap.container.Components;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.label.Label;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.snip.label.TypeLabel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ServletPluginLoader {
  // TODO make selectable via application OID
  private static Map pluginServlets;

  /**
   * Get a map of all plugins (jar and snip).
   * @return the map of plugins
   */
  public static Map getPlugins() {
    initServletPlugins();

    Map allPlugins = getSnipPlugins();
    allPlugins.putAll(pluginServlets);
    return allPlugins;
  }

  /**
   * Fina all snip plugin handlers by checking the mime type.
   * @return a map with snip name(handler) and type
   */
  private static Map getSnipPlugins() {
    Map snipPlugins = new HashMap();
    SnipSpace snipspace = (SnipSpace) Components.getComponent(SnipSpace.class);
    Iterator iterator = snipspace.getAll().iterator();
    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      Labels labels = snip.getLabels();
      boolean noLabelsAll = labels.getAll().isEmpty();

      if (!noLabelsAll) {
        Collection LabelsCat;
        // Search for all type labels
        Label label = labels.getLabel("TypeLabel");
        if (null != label && label instanceof TypeLabel) {
          // only add labels that have the type text/gsp
          String type = ((TypeLabel) label).getTypeValue();
          if ("text/gsp".equalsIgnoreCase(type) || "text/groovy".equalsIgnoreCase(type)) {
            String handler = snip.getName();
            snipPlugins.put(handler, type);
          }
        }
      }
    }
    return snipPlugins;
  }

  /**
   * Load servlets from jar resource.
   */
  private static void initServletPlugins() {
    if(null == pluginServlets) {
      pluginServlets = new HashMap();

      // load plugins from services api
      Iterator pluginServletNames = Service.providerNames(ServletPlugin.class);
      while (pluginServletNames.hasNext()) {
        String pluginLine = (String) pluginServletNames.next();
        String[] pluginInfo = pluginLine.split("\\p{Space}+");
        if (pluginInfo.length > 0) {
          pluginServlets.put(pluginInfo[0], pluginInfo.length > 1 ? pluginInfo[1] : null);
          Logger.log("found plugin: " + pluginInfo[0]);
        } else {
          Logger.warn("ignoring servlet plugin '" + pluginLine + "': missing type or servlet");
        }
      }
    }
  }
}
