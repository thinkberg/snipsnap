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

import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import snipsnap.api.snip.SnipLink;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Layouter and main handler for web sites.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Layouter extends HttpServlet {

  public final static String ATT_PAGE = "page";

  public void init(ServletConfig config) throws ServletException {
    super.init(config);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    // page attribute overrides pathinfo
    String layout = (String) request.getAttribute(ATT_PAGE);
    if (null == layout) {
      layout = SnipLink.decode(request.getPathInfo());
    }
    Configuration config = snipsnap.api.app.Application.get().getConfiguration();

    if (null == layout || "/".equals(layout)) {
      response.sendRedirect(config.getUrl("/space/" + config.getStartSnip()));
      return;
    }

    if(layout.endsWith(".jsp")) {
      request.setAttribute(ATT_PAGE, layout);
    } else {
      request.setAttribute(ATT_PAGE, "/plugin"+layout);
    }

    RequestDispatcher dispatcher = request.getRequestDispatcher("/main.jsp");

    if (dispatcher != null) {
      dispatcher.forward(request, response);
    } else {
      response.sendRedirect(config.getUrl());
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}
