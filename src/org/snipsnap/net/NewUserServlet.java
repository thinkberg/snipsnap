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
import com.neotis.snip.HomePage;
import com.neotis.user.User;
import com.neotis.user.UserManager;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet to register a new user.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class NewUserServlet extends HttpServlet {
  private final static String ERR_EXISTS = "User exists, please user another login name!";
  private final static String ERR_PASSWORD = "Password does not match!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String login = request.getParameter("login");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String password2 = request.getParameter("password2");

    if (request.getParameter("cancel") == null) {
      UserManager um = UserManager.getInstance();
      User user = um.load(login);
      // check whether user exists or not
      if (user != null) {
        response.sendRedirect("/exec/register?login=" + login + "&email=" + email + "&message=" + ERR_EXISTS);
        return;
      }
      // check whether the password is correctly typed
      if (!password.equals(password2)) {
        response.sendRedirect("/exec/register?login=" + login + "&email=" + email + "&message=" + ERR_PASSWORD);
        return;
      }
      user = um.create(login, password);

      HttpSession session = request.getSession(true);
      Application app = (Application) session.getAttribute("app");
      app.setUser(user);
      HomePage.create(login, app);
      // store user name and app in cookie and session
      response.addCookie(new Cookie("userName", user.getLogin()));
      session.setAttribute("app", app);
      response.sendRedirect("/space/" + login);
      return;
    }

    String referer = request.getParameter("referer");
    response.sendRedirect(referer != null ? referer : "/space/about");
  }
}
