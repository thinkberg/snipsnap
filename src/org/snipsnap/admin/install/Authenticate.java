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
package org.snipsnap.admin.install;

import org.snipsnap.admin.util.CommandHandler;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.user.User;

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
  public final static String ATT_CHECK_USER = "auth.login.check";

  private final static String ERR_PASSWORD = "User name and password do not match!";
  private final static String ERR_CREATE = "Passwords are not equal, try again!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String login = request.getParameter("login");
    String password = request.getParameter("password");

    HttpSession session = request.getSession(false);
    ServerConfiguration config = (ServerConfiguration) session.getAttribute(CommandHandler.ATT_CONFIG);
    User checkUser = (User) session.getAttribute(ATT_CHECK_USER);

    // request to create a new user, but make sure there is no password set already
    if (request.getParameter("create") != null && config != null &&
        checkUser.getLogin() == null && checkUser.getPasswd() == null) {
      String password2 = request.getParameter("password2");
      String email = request.getParameter("email");
      if (password != null && password.equals(password2)) {
        config.setAdminLogin(login);
        config.setAdminPassword(password);
        config.setAdminEmail(email);
        config.store();
        // put admin user in session
        checkUser = new User(login, password, email);
        session.setAttribute(CommandHandler.ATT_ADMIN, checkUser);
      } else {
        session.setAttribute("error", ERR_CREATE);
      }
    } else if (request.getParameter("cancel") == null) {
      User user = authenticate(checkUser, login, password);
      if (user == null) {
        session.setAttribute("error", ERR_PASSWORD);
      } else {
        session.setAttribute(CommandHandler.ATT_ADMIN, user);
      }
    }

    response.sendRedirect(request.getContextPath() + "/");
  }

  /**
   * Authenticate user against local administrator.
   * @param user the user to check against
   * @param login passed user name
   * @param password pass password
   * @return the authenticated user
   */
  private User authenticate(User user, String login, String password) {
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

    response.sendRedirect(request.getContextPath() + "/");
  }
}
