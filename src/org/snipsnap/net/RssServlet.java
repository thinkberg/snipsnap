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

import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Load a snip for output as RSS
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class RssServlet extends HttpServlet {
  private AppConfiguration config;

  public void init(ServletConfig servletConfig) throws ServletException {
    config = AppConfiguration.getInstance();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String eTag = request.getHeader("If-None-Match");
    if (null != eTag && eTag.equals(SnipSpace.getInstance().getETag())) {
      response.setHeader("ETag", SnipSpace.getInstance().getETag());
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return;
    } else {
      String version = request.getParameter("version");
      String name = "start";
      Snip snip = SnipSpace.getInstance().load(name);

      request.setAttribute("snip", snip);
      request.setAttribute("space", SnipSpace.getInstance());
      request.setAttribute("config", config);

      request.setAttribute("url", config.getUrl("/space"));

      RequestDispatcher dispatcher;
      if ("1.0".equals(version)) {
        dispatcher = request.getRequestDispatcher("/rdf.jsp");
      } else if ("0.92".equals(version)) {
        dispatcher = request.getRequestDispatcher("/rss.jsp");
      } else {
        dispatcher = request.getRequestDispatcher("/rss2.jsp");
      }
      dispatcher.forward(request, response);
    }
  }
}