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

    String name = request.getParameter("snip_name");
    String parent = request.getParameter("parent");

    // handle requests that store using their own handler and forward (plugin)
    String storeHandler = request.getParameter("store_handler");
    if (null != storeHandler) {
      RequestDispatcher dispatcher = request.getRequestDispatcher("/plugin/" + storeHandler);
      try {
        dispatcher.forward(request, response);
      } catch (Exception e) {
        Logger.warn("error while forwarding to store handler", e);
        request.setAttribute("error", "snip.store.handler.error");
        if (parent != null) {
          // We have been called from new
          dispatcher = request.getRequestDispatcher("/exec/new");
        } else {
          dispatcher = request.getRequestDispatcher("/exec/edit");
        }
        dispatcher.forward(request, response);
      }
      return;
    }

    if (parent != null && ! "".equals(parent)) {
      name = parent + "/" + name;
    }
    SnipSpace space = SnipSpaceFactory.getInstance();
    Snip snip = space.load(name);

    String content = request.getParameter("content");
    // if the name is empty show an error
    if (null == name || "".equals(name)) {
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/new");
      request.setAttribute("error", "snip.name.empty");
      dispatcher.forward(request, response);
      return;
    }

    if (request.getParameter("preview") != null) {
      request.setAttribute("preview", SnipFormatter.toXML(snip, content));
      RequestDispatcher dispatcher;
      if (parent != null) {
        // We have been called from new
        dispatcher = request.getRequestDispatcher("/exec/new");
      } else {
        dispatcher = request.getRequestDispatcher("/exec/edit");
      }
      dispatcher.forward(request, response);
      return;
    } else if (request.getParameter("copy.template") != null) {
      RequestDispatcher dispatcher;
      dispatcher = request.getRequestDispatcher("/exec/new");
      dispatcher.forward(request, response);
      return;
    } else if (request.getParameter("cancel") == null) {
      HttpSession session = request.getSession();
      if (session != null) {
        User user = Application.get().getUser();
        AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);

        if (service.isAuthenticated(user) && (null == snip || Security.checkPermission(Permissions.EDIT_SNIP, user, snip))) {
          if (snip != null) {
            snip.setContent(content);
            space.store(snip);
          } else {
            snip = space.create(name, content);
          }
        } else {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
      }
    } else if (snip == null) {
      // return to referrer if the snip cannot be found
      response.sendRedirect(request.getParameter("referer"));
      return;
    }
    response.sendRedirect(config.getUrl("/space/" + SnipLink.encode(name)));
  }
}
