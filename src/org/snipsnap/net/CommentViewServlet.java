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

import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.snip.SnipSpaceFactory;
import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Load a snip to view.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class CommentViewServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    String name = request.getPathInfo();
    if (null == name) {
      snipsnap.api.config.Configuration config = snipsnap.api.app.Application.get().getConfiguration();

      response.sendRedirect(config.getUrl("/space/"+snipsnap.api.app.Application.get().getConfiguration().getStartSnip()));
      return;
    } else {
      name = name.substring(1);
    }

    Snip snip = snipsnap.api.snip.SnipSpaceFactory.getInstance().load(name.replace('+', ' '));
    // Snip does not exist
    if (null == snip) {
      System.err.println("Snip does not exist: name=" + name);
      snip = snipsnap.api.snip.SnipSpaceFactory.getInstance().load("snipsnap-notfound");
    }

    snipsnap.api.app.Application app = snipsnap.api.app.Application.get();
    Map params = app.getParameters();
    params.put("viewed", snip);
    params.put("RSS", params.get("RSS") + "?snip=" + snip.getNameEncoded());

    request.setAttribute("snip", snip);
    request.setAttribute("URI", request.getRequestURI());
    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/comment.jsp");
    dispatcher.forward(request, response);
  }

}
