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
import org.snipsnap.snip.HomePage;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Servlet to register a new user.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class NewUserServlet extends HttpServlet {
  private final static String ERR_EXISTS = "User exists, please choose another login name!";
  private final static String ERR_TOO_SHORT = "User name too short (min. 3 characters)!";
  private final static String ERR_ILLEGAL = "Illegal user name! Should only contain letters, numbers, underscore and a dot.";
  private final static String ERR_PASSWORD = "Password does not match!";
  private final static String ERR_PASSWORD_TOO_SHORT = "Password must be at least 3 characters long!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String login = request.getParameter("login");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    String password2 = request.getParameter("password2");

    login = login != null ? login : "";
    email = email != null ? email : "";

    HttpSession session = request.getSession(true);
    session.removeAttribute("errors");
    Map errors = new HashMap();

    if (request.getParameter("cancel") == null) {
      UserManager um = UserManager.getInstance();
      User user = um.load(login);
      // check whether user exists or not
      if (user != null) {
        errors.put("login", ERR_EXISTS);
        sendError(session, errors, request, response);
        return;
      }

      if (login.length() < 3) {
        errors.put("login", ERR_TOO_SHORT);
        sendError(session, errors, request, response);
        return;
      }

      // TODO 1.4 if(!login.matches("[A-Za-z0-9._ ]+")) {
      StringTokenizer tok = new StringTokenizer(login, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789._ ");
      if (login.startsWith(" ") || tok.hasMoreTokens()) {
        errors.put("login", ERR_ILLEGAL + ": " + (tok.hasMoreTokens() ? tok.nextToken() : ""));
        sendError(session, errors, request, response);
        return;
      }

      // check whether the password is correctly typed
      if (!password.equals(password2)) {
        errors.put("password", ERR_PASSWORD);
        sendError(session, errors, request, response);
        return;
      }

      if (password.length() < 3) {
        errors.put("password", ERR_PASSWORD_TOO_SHORT);
        sendError(session, errors, request, response);
        return;
      }

      // create user ...
      Application app = Application.getInstance(session);
      user = um.create(login, password, email);
      app.setUser(user, session);
      HomePage.create(login);

      // store user name and app in cookie and session
      Cookie cookie = new Cookie("userName", user.getLogin());
      cookie.setMaxAge(43200000);
      cookie.setPath(request.getContextPath());
      response.addCookie(cookie);
      session.setAttribute("app", app);
      response.sendRedirect(SnipLink.absoluteLink(request, "/space/" + SnipLink.encode(login)));
      return;
    }

    String referer = request.getParameter("referer");
    response.sendRedirect(referer != null ? referer : SnipLink.absoluteLink(request, "/space/start"));
  }

  private void sendError(HttpSession session, Map errors, HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    session.setAttribute("errors", errors);
    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/register.jsp");
    dispatcher.forward(request, response);
  }
}
