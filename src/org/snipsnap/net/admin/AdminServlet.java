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
package org.snipsnap.net.admin;

import org.snipsnap.app.Application;
import org.snipsnap.net.Layouter;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.UserManagerFactory;
import org.snipsnap.container.Components;
import org.snipsnap.container.SessionService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet used for interfacing to external user management. Sets the current user manager
 * into the session as usermanager:/contextPath.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AdminServlet extends HttpServlet {

  protected final static String ATT_USERMANAGER = "usermanager";
  protected final static String ATT_CONFIG = "config";
  protected final static String ATT_ADMIN = "admin";

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    // get user manager and store in session
    UserManager um = UserManagerFactory.getInstance();
    HttpSession session = request.getSession();
    Application app = Application.getInstance(session);
    User user = app.getUser();
    if (user == null) {
      SessionService service = (SessionService) Components.getComponent(SessionService.class);
      user = service.getUser(request, response);
    }

    AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);

    if (service.isAuthenticated(user) && user.isAdmin() && request.getPathInfo() != null) {
      session.setAttribute(ATT_USERMANAGER, um);
      session.setAttribute(ATT_CONFIG, app.getConfiguration());
      session.setAttribute(ATT_ADMIN, user);

      String layout = request.getPathInfo();
      if (null == layout || "/".equals(layout)) {
        layout = "/application.jsp";
      }
      request.setAttribute(Layouter.ATT_PAGE, "/admin" + layout);
      RequestDispatcher dispatcher = getServletContext().getNamedDispatcher("org.snipsnap.net.Layouter");
      dispatcher.forward(request, response);
      return;
    }
    response.sendRedirect(SnipLink.absoluteLink("/"));
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}