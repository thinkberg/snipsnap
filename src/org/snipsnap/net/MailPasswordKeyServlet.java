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
import org.snipsnap.util.mail.Mail;
import org.snipsnap.config.AppConfiguration;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

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
      UserManager um = UserManager.getInstance();
      User user = um.load(login);

      if (user == null) {
        request.setAttribute("error", "User name '"+login+"' does not exist!");
        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/forgot.jsp");
        dispatcher.forward(request, response);
        return;
      }

      String key = um.getPassWordKey(user);
      AppConfiguration configuration = Application.get().getConfiguration();
      String sender = configuration.getUrl();
      try {
        sender = new URL(sender).getHost();
      } catch (MalformedURLException e) {
        sender = "this-is-a-bug.org";
      }
      sender = "do-not-reply@" + sender;
      String receiver = user.getEmail();
      String subject = "Forgotten password";
      String url = configuration.getUrl("/exec/changepass.jsp?key="+key);
      String content = "To change your password go to <a href=\"" + url +
          "\">"+ url + "</a>";

      Mail.getInstance().sendMail(sender, receiver, subject, content);
    } else {
      response.sendRedirect(SnipLink.absoluteLink(request, "/space/start"));
      return;
    }


    request.setAttribute("error", "Check your inbox. You should receive an email with instructions soon.");
    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/forgot.jsp");
    dispatcher.forward(request, response);
  }
}
