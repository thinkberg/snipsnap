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
import org.snipsnap.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GroovyTemplateServlet extends HttpServlet {

  SimpleTemplateEngine templateEngine = new SimpleTemplateEngine();

  protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    SnipSpace space = (SnipSpace)Components.getComponent(SnipSpace.class);

    String groovyFile = (String) request.getAttribute("javax.servlet.include.servlet_path");
    if(null == groovyFile) {
      groovyFile = request.getServletPath();
    }

    if(groovyFile.startsWith("/")) {
      groovyFile = groovyFile.substring(1);
    }

    if(groovyFile.endsWith(".gsp")) {
      groovyFile = groovyFile.substring(0, groovyFile.lastIndexOf("."));
    }

    Snip templateSnip = space.load(groovyFile);
    if(null != templateSnip) {
      String templateSource = templateSnip.getContent();
      try {
        Template groovyTemplate = templateEngine.createTemplate(templateSource);
        groovyTemplate.setBinding(Application.get().getParameters());
        response.getWriter().write(groovyTemplate.toString());
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
