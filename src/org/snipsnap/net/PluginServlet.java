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
package org.snipsnap.net;

import groovy.text.SimpleTemplateEngine;
import groovy.text.Template;
import org.radeox.util.Service;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.label.MIMETypeLabel;
import org.snipsnap.user.Security;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PluginServlet extends HttpServlet {
  private Map extTypeMap = new HashMap();
  private Map pluginServlets = new HashMap();
  private Map servletCache = new HashMap();

  private final static Roles EXEC_ROLES = new Roles(Roles.ADMIN);

  SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();

  public void init() throws ServletException {
    super.init();

    // currently supported script types (with extensions)
    extTypeMap.put(".gsp", "text/gsp");
    extTypeMap.put(".groovy", "text/groovy");

    // load plugins from services api
    Iterator pluginServletNames = Service.providerNames(ServletPlugin.class);
    while (pluginServletNames.hasNext()) {
      String pluginLine = (String) pluginServletNames.next();
      String[] pluginInfo = pluginLine.split("\\p{Space}");
      if (pluginInfo.length > 0) {
        pluginServlets.put(pluginInfo[0], pluginInfo.length > 1 ?  pluginInfo[1] : null);
        Logger.log("found plugin: "+pluginInfo[0]);
      } else {
        Logger.warn("ignoring servlet plugin '" + pluginLine + "': missing type or servlet");
      }
    }
  }

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    // get actual name of the plugin to call
    String pluginName = (String) request.getAttribute("javax.servlet.include.path_info");
    if (null == pluginName) {
      pluginName = request.getPathInfo();
    }

    if (pluginName.startsWith("/")) {
      pluginName = pluginName.substring(1);
    }

    BufferedWriter writer = new BufferedWriter(response.getWriter());
    String handlerMIMEType = (String) pluginServlets.get(pluginName);
    if (null == handlerMIMEType) {
      SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
      if (space.exists(pluginName)) {
        Snip snip = space.load(pluginName);
        if(Security.existsPermission(Permissions.EDIT_SNIP, snip, EXEC_ROLES)) {
          String mimeType = getMIMEType(snip);
          if ("text/gsp".equalsIgnoreCase(mimeType)) {
            try {
              writer.write(handleGroovyTemplate(snip.getContent()));
            } catch (IOException e) {
              writer.write("<span class=\"error\">" + e.getLocalizedMessage() + "</span>");
            }
            writer.flush();
            return;
          }
        } else {
          throw new ServletException("a snip plugin must be locked by admin");
        }
      }
    }

    if (null == handlerMIMEType) {
      int extIndex = pluginName.indexOf(".");
      if (extIndex != -1) {
        handlerMIMEType = (String) extTypeMap.get(pluginName.substring(extIndex));
      }
    }

    if ("text/gsp".equalsIgnoreCase(handlerMIMEType)) {
      try {
        writer.write(handleGroovyTemplate(getTemplateSource(pluginName)));
      } catch (IOException e) {
        writer.write("<span class=\"error\">" + e.getLocalizedMessage() + "</span>");
      }
    } else {
      ServletPlugin servletPlugin = (ServletPlugin) servletCache.get(pluginName);
      if (null == servletPlugin) {
        try {
          servletPlugin = getServletPlugin(pluginName);
        } catch (Exception e) {
          Logger.warn("unable to load servlet plugin", e);
          throw new ServletException("unable to load servlet plugin", e);
        }
        servletCache.put(pluginName, servletPlugin);
      }

      if (null != servletPlugin) {
        try {
          servletPlugin.service(request, response);
        } catch (Exception e) {
          Logger.warn("error while executing servlet plugin", e);
          throw new ServletException("error while executing servlet plugin", e);
        }
      } else {

      }
    }
    writer.flush();
  }

  private ServletPlugin getServletPlugin(String pluginName) throws Exception {
    Class pluginClass = Class.forName(pluginName);
    return (ServletPlugin) pluginClass.newInstance();
  }

  private String getMIMEType(Snip snip) {
    Collection mimeTypes = snip.getLabels().getLabels("mime-type");
    if (!mimeTypes.isEmpty()) {
      Iterator handlerIt = mimeTypes.iterator();
      while (handlerIt.hasNext()) {
        MIMETypeLabel mimeType = (MIMETypeLabel) handlerIt.next();
        return mimeType.getMIMEType();
      }
    }
    return null;
  }

  private String handleGroovyTemplate(String source) {
    try {
      Template groovyTemplate = templateEngine.createTemplate(source);
      groovyTemplate.setBinding(Application.get().getParameters());
      return groovyTemplate.toString();
    } catch (Exception e) {
      e.printStackTrace();
      return e.getMessage();
    }
  }

  /**
   * Read the template source from either a snip or if not existent try the
   * jar/classpath based file read.
   * @param name the name of the resource to load
   * @return a string with the template source
   * @throws IOException
   */
  private String getTemplateSource(String name) throws IOException {
    InputStream resource = getClass().getResourceAsStream(name);
    if (null != resource) {
      // if there is no snip to load, try jar/classpath based file read
      BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
      StringBuffer content = new StringBuffer();
      char buffer[] = new char[1024];
      int length = 0;
      while ((length = reader.read(buffer)) != -1) {
        content.append(buffer, 0, length);
      }
      return content.toString();
    }

    throw new IOException("unable to load template source: '" + name + "'");
  }
}
