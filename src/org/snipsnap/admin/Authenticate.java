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
package com.neotis.admin;

import com.neotis.config.Configuration;
import com.neotis.snip.SnipLink;
import com.neotis.user.User;

import javax.servlet.ServletException;
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
public class Authenticate extends HttpServlet {
  private final static String ERR_PASSWORD = "User name and password do not match!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String login = request.getParameter("login");
    String password = request.getParameter("password");

    Configuration config = new Configuration("./conf/local.conf");
    if (config.isConfigured() && request.getParameter("cancel") == null) {
      HttpSession session = request.getSession(true);
      User user = authenticate(config, login, password);
      if (user == null) {
        session.setAttribute("error", ERR_PASSWORD);
        response.sendRedirect(SnipLink.absoluteLink(request, "/exec/login.jsp"));
        return;
      }
      session.setAttribute("admin", user);
    }

    response.sendRedirect(SnipLink.absoluteLink(request, "/"));
  }

  /**
   * Authenticate user against local administrator.
   * @param config the configuration object
   * @param login passed user name
   * @param password pass password
   * @return the authenticated user
   */
  private User authenticate(Configuration config, String login, String password) {
    User user = new User(config.getUserName(), config.getPassword(), config.getEmail());
    if (user.getLogin().equals(login) && user.getPasswd().equals(password)) {
      return user;
    }
    return null;
  }

  protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    if ("true".equals(request.getParameter("logoff"))) {
      HttpSession session = request.getSession();
      if (session != null) {
        session.invalidate();
      }
    }

    response.sendRedirect(SnipLink.absoluteLink(request, "/"));
  }
}
