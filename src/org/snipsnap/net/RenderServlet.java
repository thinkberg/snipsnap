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

import org.snipsnap.graph.ContentRenderer;
import org.snipsnap.graph.MindMapContentRenderer;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpaceFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Get some data from a snip and render the content
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class RenderServlet extends HttpServlet {

  public void init(ServletConfig servletConfig) throws ServletException {
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String handler = request.getParameter("handler");
    String name = request.getParameter("name");
    name = name.replace('+', ' ');

    Snip snip = SnipSpaceFactory.getInstance().load(name);
    String content = snip.getContent();

    int start = 0;
    int end = 0;
    try {
      start = Integer.parseInt(request.getParameter("start"));
    } catch (NumberFormatException e) {
      start = 0;
    }
    try {
      end = Integer.parseInt(request.getParameter("end"));
    } catch (NumberFormatException e) {
      end = content.length();
    }

    content = content.substring(start, end);
    ContentRenderer renderer = new MindMapContentRenderer();
    renderer.render(request, response, content);

  }
}