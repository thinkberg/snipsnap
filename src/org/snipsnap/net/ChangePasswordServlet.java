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

import snipsnap.api.app.Application;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.PasswordService;
import org.snipsnap.user.UserManagerFactory;
import snipsnap.api.container.Components;
import org.snipsnap.container.SessionService;
import snipsnap.api.config.Configuration;

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
    Configuration config = Application.get().getConfiguration();

    String password1 = request.getParameter("password");
    String password2 = request.getParameter("password2");
    String key = request.getParameter("key");

    if (request.getParameter("cancel") == null) {
      snipsnap.api.user.User user;
      if (null != password1 && password1.equals(password2)) {
        PasswordService passwordService = (PasswordService) snipsnap.api.container.Components.getComponent(PasswordService.class);
        user = passwordService.changePassWord(key, password1);
      } else {
        request.setAttribute("error", "user.password.error.nomatch");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/changepass.jsp");
        dispatcher.forward(request, response);
        return;
      }

      if (null != user) {
        if (snipsnap.api.app.Application.getCurrentUsers().contains(user)) {
          snipsnap.api.app.Application.getCurrentUsers().remove(user);
        }
        HttpSession session = request.getSession();
        snipsnap.api.app.Application.get().setUser(user, session);

        SessionService service = (SessionService) Components.getComponent(SessionService.class);
        service.setUser(request, response, user);
        response.sendRedirect(config.getUrl("/space/"+snipsnap.api.snip.SnipLink.encode(user.getLogin())));
      } else {
        request.setAttribute("error", "user.password.error.keymismatch");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/forgot.jsp");
        dispatcher.forward(request, response);
      }
    } else {
      response.sendRedirect(config.getUrl("/space/"+Application.get().getConfiguration().getStartSnip()));
    }
  }
}
