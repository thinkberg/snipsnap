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

import org.snipsnap.app.Application;
import org.snipsnap.snip.*;
import org.snipsnap.user.User;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Security;
import org.snipsnap.container.Components;
import org.snipsnap.config.Configuration;
import org.snipsnap.net.filter.MultipartWrapper;
import org.radeox.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet to store snips into the database after they have been edited.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipStoreServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.sendRedirect(Application.get().getConfiguration().getUrl());
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    Configuration config = Application.get().getConfiguration();
    // If this is not a multipart/form-data request continue
    String type = request.getHeader("Content-Type");
    if (type != null && type.startsWith("multipart/form-data")) {
      try {
        request = new MultipartWrapper(request, config.getEncoding() != null ? config.getEncoding() : "UTF-8");
      } catch (IllegalArgumentException e) {
        Logger.warn("SnipStoreServlet: multipart/form-data wrapper:" + e.getMessage());
      }
    }

    String name = request.getParameter("name");
    String parent = request.getParameter("parent");
    String content = request.getParameter("content");

    if (parent != null && !"".equals(parent)) {
      name = parent + "/" + name;
    }

    SnipSpace space = SnipSpaceFactory.getInstance();
    Snip snip = space.load(name);

    // cancel pressed, return to snip or referer
    if (request.getParameter("cancel") != null) {
      if (null == snip) {
        response.sendRedirect(request.getParameter("referer"));
      } else {
        response.sendRedirect(config.getUrl("/space/" + SnipLink.encode(name)));
      }
    }

    // if the name is empty show an error
    if (null == name || "".equals(name)) {
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/edit");
      request.setAttribute("error", "snip.name.empty");
      dispatcher.forward(request, response);
      return;
    }

    RequestDispatcher dispatcher;

    // handle preview requests
    if (request.getParameter("preview") != null) {
      request.setAttribute("preview", SnipFormatter.toXML(snip, content));
      dispatcher = request.getRequestDispatcher("/exec/edit");
      dispatcher.forward(request, response);
      return;
    }

    // handle template copy requests (done by the edit servlet!)
    if (request.getParameter("copy.template") != null) {
      dispatcher = request.getRequestDispatcher("/exec/edit");
      dispatcher.forward(request, response);
      return;
    }

    // handle requests that store using their own handler and forward (plugin)
    HttpSession session = request.getSession();
    if (session != null) {
      User user = Application.get().getUser();
      AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);
      String storeHandler = request.getParameter("store_handler");
      if (service.isAuthenticated(user) && (null == snip || Security.checkPermission(Permissions.EDIT_SNIP, user, snip))) {
        if (null != storeHandler) {
          dispatcher = request.getRequestDispatcher("/plugin/" + storeHandler);
          try {
            dispatcher.forward(request, response);
          } catch (Exception e) {
            Logger.warn("error while forwarding to store handler", e);
            request.setAttribute("error", "snip.store.handler.error");
            dispatcher = request.getRequestDispatcher("/exec/edit");
            dispatcher.forward(request, response);
          }
          return;
        } else {
          // default storage handling
          if (snip != null) {
            snip.setContent(content);
            space.store(snip);
          } else {
            snip = space.create(name, content);
          }
        }
      }
    }

    if (null == snip && !space.exists(name)) {
      response.sendRedirect(request.getParameter("referer"));
      return;
    }

    response.sendRedirect(config.getUrl("/space/" + SnipLink.encode(name)));
  }
}
