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

import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.user.User;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.config.Configuration;
import org.snipsnap.container.Components;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Load a snip to view.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipViewServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    Configuration config = Application.get().getConfiguration();
    User user = Application.get().getUser();
    AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);

    if (service.isAuthenticated(user)) {
      user.lastAccess();
    }

    // handle the snip name
    String name = request.getPathInfo();
    if (null == name || "/".equals(name)) {
      name = config.getStartSnip();
    } else {
      name = name.substring(1);
    }
    name = name.replace('+', ' ');

    // load snip and set attributes for request
    Snip snip = SnipSpaceFactory.getInstance().load(name);

    String subname = null;
    if (null == snip) {
      // handle attachments
      int slashIndex = name.indexOf('/');
      if (slashIndex != -1) {
        subname = name.substring(slashIndex + 1);
        name = name.substring(0, slashIndex);
        Logger.log(Logger.DEBUG, name + ": attachment: " + subname);
      }
      snip = SnipSpaceFactory.getInstance().load(name);
    }


    request.setAttribute("snip", snip);
    request.setAttribute("URI", request.getRequestURL().toString());

    if (subname != null && subname.length() > 0) {
      try {
        request.setAttribute(FileDownloadServlet.FILENAME, subname);
        RequestDispatcher dispatcher =
            getServletContext().getNamedDispatcher("org.snipsnap.net.FileDownloadServlet");
        dispatcher.forward(request, response);
        return;
      } catch (ServletException e) {
        // jump to the not found page
        snip = null;
      }
    }

    // Snip does not exist
    if (null == snip) {
      if (config.allow(Configuration.APP_PERM_CREATESNIP)) {
        response.sendRedirect("/exec/edit?name=" + name);
      } else {
        if ("snipsnap-notfound".equals(name)) {
          response.sendError(HttpServletResponse.SC_NOT_FOUND,
              "Internal Error: could not find snipsnap-notfound page<br>"
              + "This may indicate that either the installation has failed or the Database is corrupted.");
          return;
        }
        response.sendRedirect(SnipLink.absoluteLink("/space/snipsnap-notfound?name=" + name));
      }
      return;
    }

    snip.handle(request);
    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/snip.jsp");
    dispatcher.forward(request, response);
  }
}