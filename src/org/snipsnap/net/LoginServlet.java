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
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.snip.SnipLink;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet to login a user by checking user name and password.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class LoginServlet extends HttpServlet {
  private final static String ERR_PASSWORD = "User name and password do not match!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String login = request.getParameter("login");
    String password = request.getParameter("password");
    String referer = request.getParameter("referer");

    if (request.getParameter("cancel") == null) {
      UserManager um = UserManager.getInstance();
      User user = um.authenticate(login, password);
      if(Application.getCurrentUsers().contains(user)) {
        Application.getCurrentUsers().remove(user);
      }
      HttpSession session = request.getSession(true);
      if (user == null) {
        session.setAttribute("login.login", login);
        session.setAttribute("referrer", referer);
        session.setAttribute("error", ERR_PASSWORD);
        response.sendRedirect(SnipLink.absoluteLink(request, "/exec/login.jsp"));
        return;
      }

      session.removeAttribute("referrer");
      Application app = Application.getInstance(session);
      app.setUser(user, session);

      um.setCookie(request, response, user);
      session.setAttribute("app", app);
    }

    response.sendRedirect(referer);
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    String referer = request.getHeader("REFERER");
    if(referer == null || referer.length() == 0) {
      referer = Application.get().getConfiguration().getSnipUrl("start");
    }

    if("true".equals(request.getParameter("logoff"))) {
      HttpSession session = request.getSession(true);
      UserManager.getInstance().removeCookie(request, response);
      session.invalidate();
    } else if("true".equals(request.getParameter("timeout"))) {
      HttpSession session = request.getSession(true);
      Application.removeCurrentUser(session);
      session.invalidate();
    }

    response.sendRedirect(referer);
  }
}
