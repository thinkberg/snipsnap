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
import snipsnap.api.app.Application;
import org.snipsnap.container.Components;
import snipsnap.api.snip.SnipSpace;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GroovyTemplateServlet extends HttpServlet {

  SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    String groovyFile = (String) request.getAttribute("javax.servlet.include.path_info");
    if (null == groovyFile) {
      groovyFile = request.getPathInfo();
    }

    if (groovyFile.startsWith("/")) {
      groovyFile = groovyFile.substring(1);
    }

    String templateSource = getTemplateSource(groovyFile);
    try {
      Template groovyTemplate = templateEngine.createTemplate(templateSource);
      groovyTemplate.make(Application.get().getParameters()).writeTo(response.getWriter());
    } catch (Exception e) {
      e.printStackTrace();
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
    SnipSpace space = (snipsnap.api.snip.SnipSpace) Components.getComponent(snipsnap.api.snip.SnipSpace.class);
    if (space.exists(name)) {
      return space.load(name).getContent();
    }

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
