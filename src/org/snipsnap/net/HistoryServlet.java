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
import org.snipsnap.container.Components;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipSpace;
import org.snipsnap.versioning.VersionManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Show the version history of a snip
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class HistoryServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    final String name = request.getParameter("name");
    if (null == name) {
      Configuration config = Application.get().getConfiguration();
      response.sendRedirect(config.getUrl("/space/"+config.getStartSnip()));
      return;
    }

    snipsnap.api.snip.SnipSpace space = (snipsnap.api.snip.SnipSpace)Components.getComponent(SnipSpace.class);
    snipsnap.api.snip.Snip snip = space.load(name);

    VersionManager versionManager = (VersionManager) Components.getComponent(VersionManager.class);
    List history = versionManager.getHistory(snip);

    request.setAttribute("snip", snip);
    request.setAttribute("history", history);
    request.setAttribute("URI", request.getRequestURL().toString());

    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/history.jsp");
    dispatcher.forward(request, response);
  }
}