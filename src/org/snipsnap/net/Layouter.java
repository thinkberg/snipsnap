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
package com.neotis.net;

import com.neotis.app.Application;
import com.neotis.user.UserManager;
import com.neotis.user.User;
import com.neotis.snip.SnipSpace;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Layouter and main handler for web sites.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Layouter extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    // get or create session and application object
    HttpSession session = request.getSession(true);
    Application app = (Application)session.getAttribute("app");
    if(app == null) {
      app = new Application();
    }

    User user = app.getUser();
    if(user == null) {
      user = UserManager.getInstance().getUser(request);
    }

    // store user name and app in cookie and session
    response.addCookie(new Cookie("userName", user.getLogin()));
    app.setUser(user);
    session.setAttribute("app", app);
    session.setAttribute("space", SnipSpace.getInstance());

    String layout = request.getPathInfo();
    if(null == layout || "/".equals(layout)) {
      response.sendRedirect("/");
      return;
    }

    request.setAttribute("page", layout);
    RequestDispatcher dispatcher = request.getRequestDispatcher("/main.jsp");
    dispatcher.forward(request, response);
  }

}
