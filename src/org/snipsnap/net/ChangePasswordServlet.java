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
import org.snipsnap.snip.SnipLink;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Generates a password key to change the password. The key
 * is mailed to the user.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class ChangePasswordServlet extends HttpServlet {
  private final static String ERR_PASSWORD = "User name and password do not match!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    String password1 = request.getParameter("password");
    String password2 = request.getParameter("password2");
    String key = request.getParameter("key");

    if (request.getParameter("cancel") == null) {
      UserManager um = UserManager.getInstance();
      User user;
      if (null != password1 && password1.equals(password2)) {
        user = um.changePassWord(key, password1);
      } else {
        request.setAttribute("error", "Passwords do not match.");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/changepass.jsp");
        dispatcher.forward(request, response);
        return;
      }

      if (null != user) {
        if (Application.getCurrentUsers().contains(user)) {
          Application.getCurrentUsers().remove(user);
        }
        HttpSession session = request.getSession(true);
        Application app = Application.getInstance(session);
        app.setUser(user, session);
        session.setAttribute("app", app);

        um.setCookie(request, response, user);
        response.sendRedirect(SnipLink.absoluteLink("/space/" + SnipLink.encode(user.getLogin())));
      } else {
        request.setAttribute("error", "Your reset key has been manipulated. Try again, please.");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/forgot.jsp");
        dispatcher.forward(request, response);
      }
    } else {
      response.sendRedirect(SnipLink.absoluteLink("/space/start"));
    }
  }
}
