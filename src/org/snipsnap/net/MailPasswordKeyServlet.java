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
import snipsnap.api.config.Configuration;
import snipsnap.api.snip.SnipLink;
import snipsnap.api.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.PasswordService;
import org.snipsnap.user.UserManagerFactory;
import org.snipsnap.util.mail.Mail;
import snipsnap.api.container.Components;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Generates a password key to change the password. The key
 * is mailed to the user.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class MailPasswordKeyServlet extends HttpServlet {
  private final static String ERR_PASSWORD = "User name and password do not match!";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String login = request.getParameter("login");

    if (request.getParameter("cancel") == null) {
      UserManager um = UserManagerFactory.getInstance();
      snipsnap.api.user.User user = um.load(login);

      if (user == null) {
        request.setAttribute("error", "User name '" + login + "' does not exist!");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/forgot.jsp");
        dispatcher.forward(request, response);
        return;
      }

      PasswordService passwordService = (PasswordService) Components.getComponent(PasswordService.class);
      String key = passwordService.getPassWordKey(user);
      Configuration configuration = Application.get().getConfiguration();
      String receiver = user.getEmail();
      if (receiver != null && receiver.length() > 0) {
        String subject = "Forgotten password";
        String url = configuration.getUrl("/exec/changepass.jsp?key=" + key);
        String content = "To change your password go to <a href=\"" + url +
          "\">" + url + "</a>";

        Mail.getInstance().sendMail(receiver, subject, content);
      } else {
        request.setAttribute("error", "No email, please contact the SnipSnap administrator!");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/forgot.jsp");
        dispatcher.forward(request, response);
        return;
      }
    } else {
      snipsnap.api.config.Configuration config = snipsnap.api.app.Application.get().getConfiguration();
      response.sendRedirect(config.getUrl("/space/"+config.getStartSnip()));
      return;
    }


    request.setAttribute("error", "Check your inbox. You should receive an email with"
                                  + " instructions soon if you registered with an Email.");
    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/forgot.jsp");
    dispatcher.forward(request, response);
  }
}
