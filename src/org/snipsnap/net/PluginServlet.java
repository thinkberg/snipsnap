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
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.label.TypeLabel;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;
import org.snipsnap.user.Security;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PluginServlet extends HttpServlet {
  private Map extTypeMap = new HashMap();
  private Map servletCache = new HashMap();

  private final static Roles EXEC_ROLES = new Roles(Roles.ADMIN);

  SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();

  public void init() throws ServletException {
    super.init();

    // currently supported script types (with extensions)
    extTypeMap.put(".gsp", "text/gsp");
    extTypeMap.put(".groovy", "text/groovy");
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

    // check for the plugin in the snip space which overrides other plugins
    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
    if (space.exists(pluginName)) {
      Snip snip = space.load(pluginName);
      // only execute plugins who are locked by an Admin
      if (Security.existsPermission(Permissions.EDIT_SNIP, snip, EXEC_ROLES)) {
        String mimeType = getMIMEType(snip);
        if ("text/gsp".equalsIgnoreCase(mimeType)) {
          BufferedWriter writer = new BufferedWriter(response.getWriter());
          try {
            writer.write(handleGroovyTemplate(snip.getContent()));
          } catch (IOException e) {
            writer.write("<span class=\"error\">" + e.getLocalizedMessage() + "</span>");
          }
          writer.flush();
        } else {
          // plugins of types other than text/gsp are included into the response
          RequestDispatcher dispatcher = request.getRequestDispatcher("/raw/" + pluginName);
          dispatcher.include(request, response);
        }
        return;
      }
    }

    // check for registered plugins
    Map plugins = ServletPluginLoader.getPlugins();
    if (plugins.containsKey(pluginName)) {
      String handlerMIMEType = (String) plugins.get(pluginName);

      // try to find a mime type for the requested plugin
      if (null == handlerMIMEType) {
        int extIndex = pluginName.indexOf(".");
        if (extIndex != -1) {
          handlerMIMEType = (String) extTypeMap.get(pluginName.substring(extIndex));
        }
      }

      if ("text/gsp".equalsIgnoreCase(handlerMIMEType)) {
        BufferedWriter writer = new BufferedWriter(response.getWriter());
        try {
          writer.write(handleGroovyTemplate(getTemplateSource(pluginName)));
        } catch (Exception e) {
          writer.write("<span class=\"error\">" + e.getLocalizedMessage() + "</span>");
        }
        writer.flush();
        return;
      } else {
        // a non-script plugin (i.e. servlet or simply a file)
        ServletPlugin servletPlugin = (ServletPlugin) servletCache.get(pluginName);
        if (null == servletPlugin) {
          try {
            servletPlugin = getServletPlugin(pluginName);
            servletCache.put(pluginName, servletPlugin);
          } catch (Exception e) {
            // ignore plugins not found ...
          }
        }

        // a servlet plugin is executed, everything else is included into the response
        if (null != servletPlugin) {
          try {
            servletPlugin.service(request, response);
          } catch (Exception e) {
            Logger.warn("error while executing servlet plugin", e);
            throw new ServletException("error while executing servlet plugin", e);
          }
        } else {
          if (null != handlerMIMEType) {
            response.setContentType(handlerMIMEType);
          }
          OutputStream out = response.getOutputStream();
          InputStream fileIs = PluginServlet.class.getResourceAsStream("/" + pluginName);
          if (null != fileIs) {
            byte[] buffer = new byte[1024];
            int bytes = 0;
            while ((bytes = fileIs.read(buffer)) != -1) {
              out.write(buffer, 0, bytes);
            }
            out.flush();
            return;
          } else {
            throw new ServletException("unable to load servlet plugin: not found");
          }
        }
      }
    }

    response.sendError(HttpServletResponse.SC_FORBIDDEN);
  }

  private ServletPlugin getServletPlugin(String pluginName) throws Exception {
    Class pluginClass = Class.forName(pluginName);
    return (ServletPlugin) pluginClass.newInstance();
  }

  private String getMIMEType(Snip snip) {
    Collection mimeTypes = snip.getLabels().getLabels("TypeLabel");
    if (!mimeTypes.isEmpty()) {
      Iterator handlerIt = mimeTypes.iterator();
      while (handlerIt.hasNext()) {
        TypeLabel mimeType = (TypeLabel) handlerIt.next();
        return mimeType.getTypeValue();
      }
    }
    return null;
  }

  private String handleGroovyTemplate(String source) throws Exception {
    try {
      Template groovyTemplate = templateEngine.createTemplate(source);
      groovyTemplate.setBinding(Application.get().getParameters());
      return groovyTemplate.toString();
    } catch(Error e) {
      e.printStackTrace();
      throw new ServletException("groovy error", e);
    }
  }

  /**
   * Read the template source from either a snip or if not existent try the
   * jar/classpath based file read.
   *
   * @param name the name of the resource to load
   * @return a string with the template source
   * @throws IOException
   */
  private String getTemplateSource(String name) throws IOException {
    InputStream resource = getClass().getResourceAsStream("/" + name);
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
