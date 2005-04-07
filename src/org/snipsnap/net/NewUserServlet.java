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
import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import snipsnap.api.container.Components;
import org.snipsnap.container.SessionService;
import org.snipsnap.net.filter.MultipartWrapper;
import org.snipsnap.snip.HomePage;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.UserManagerFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Servlet to register a new user.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class NewUserServlet extends HttpServlet {
  private final static String ERR_EXISTS = "login.register.error.user.exists";
  private final static String ERR_TOO_SHORT = "login.register.error.user.short";
  private final static String ERR_ILLEGAL = "login.register.error.user.illegal";
  private final static String ERR_PASSWORD = "login.register.error.passwords";
  private final static String ERR_PASSWORD_TOO_SHORT = "login.register.error.password.short";
  private final static String ERR_NOT_ALLOWED = "login.register.error.not.allowed";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    snipsnap.api.config.Configuration config = snipsnap.api.app.Application.get().getConfiguration();

    // If this is not a multipart/form-data request continue
    String type = request.getHeader("Content-Type");
    if (type != null && type.startsWith("multipart/form-data")) {
      try {
        request = new MultipartWrapper(request, config.getEncoding() != null ? config.getEncoding() : "UTF-8");
      } catch (IllegalArgumentException e) {
        Logger.warn("FileUploadServlet: multipart/form-data wrapper:" + e.getMessage());
      }
    }

    HttpSession session = request.getSession();
    session.removeAttribute("errors");
    Map errors = new HashMap();

    if (!config.deny(snipsnap.api.config.Configuration.APP_PERM_REGISTER)) {
      String login = request.getParameter("login");
      String email = request.getParameter("email");
      String password = request.getParameter("password");
      String password2 = request.getParameter("password2");

      login = login != null ? login : "";
      email = email != null ? email : "";


      if (request.getParameter("cancel") == null) {
        UserManager um = UserManagerFactory.getInstance();
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

        login = login.trim();

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
        user = um.create(login, password, email);
        Application.get().setUser(user, session);
        HomePage.create(login);

        // store user name and app in cookie and session
        SessionService sessionService = (SessionService) Components.getComponent(SessionService.class);
        sessionService.setCookie(request, response, user);

        response.sendRedirect(config.getUrl("/space/" + SnipLink.encode(login)));
        return;
      }

      String referer = sanitize(request.getParameter("referer"));
      response.sendRedirect(referer != null ? referer : config.getUrl("/space/" + config.getStartSnip()));
    } else {
      errors.put("Fatal", ERR_NOT_ALLOWED);
      sendError(session, errors, request, response);
    }
  }

  private void sendError(HttpSession session, Map errors, HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    session.setAttribute("errors", errors);
    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/register.jsp");
    dispatcher.forward(request, response);
  }

  private String sanitize(String parameter) {
    return parameter.split("[\r\n]")[0];
  }
}
