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
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

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

    UserManager um = UserManager.getInstance();
    User user = Application.get().getUser();
    if (um.isAuthenticated(user)) {
      user.lastAccess();
    }

    // handle the snip name
    String name = request.getPathInfo();
    if (null == name || "/".equals(name)) {
      name = Application.get().getConfiguration().getStartSnip();
    } else {
      name = name.substring(1);
    }
    name = name.replace('+', ' ');

    // handle sub snips and attachments (TODO: handle more than one level)
    String subname = null;
    int slashIndex = name.indexOf('/');
    if (slashIndex != -1) {
      subname = name.substring(slashIndex + 1);
      name = name.substring(0, slashIndex);
      Logger.log(Logger.DEBUG, name + ": attachment: " + subname);
    }

    // TODO: make load from snipspace work with name spaces
    // load snip and set attributes for request
    Snip snip = SnipSpaceFactory.getInstance().load(name);

    request.setAttribute("snip", snip);
    request.setAttribute("URI", request.getRequestURL().toString());

    if (subname != null && subname.length() > 0) {
      // TODO work with sub snips as well, not just attachments
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
      response.sendRedirect("/space/snipsnap-notfound?name="+name);
      return;
    }

    snip.handle(request);
    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/snip.jsp");
    dispatcher.forward(request, response);
  }
}