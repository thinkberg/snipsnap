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
import org.snipsnap.snip.SnipSpaceFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Display information about the weblog for
 * really simple discovery,
 * http://archipelago.phrasewise.com/stories/storyReader$1330
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class RsdServlet extends HttpServlet {
  private AppConfiguration config;

  public void init(ServletConfig servletConfig) throws ServletException {
    config = AppConfiguration.getInstance();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String name = "start";
    Snip snip = SnipSpaceFactory.getInstance().load(name);

    request.setAttribute("snip", snip);
    request.setAttribute("space", SnipSpaceFactory.getInstance());
    request.setAttribute("config", config);

    request.setAttribute("url", config.getUrl("/space"));
    request.setAttribute("baseurl", config.getUrl());

    RequestDispatcher dispatcher = request.getRequestDispatcher("/rsd.jsp");
    dispatcher.forward(request, response);
  }
}