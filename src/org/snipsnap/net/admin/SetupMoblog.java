/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002,2003 Fraunhofer Gesellschaft
 * Fraunhofer Institut for Computer Architecture and Software Technology
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
package org.snipsnap.net.admin;

import org.snipsnap.config.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.net.InetAddress;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.io.IOException;

public class SetupMoblog implements SetupHandler {
  public String getName() {
    return "moblog";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String pop3Host = request.getParameter(Configuration.APP_MAIL_POP3_HOST);
    config.setMailPop3Host(pop3Host);
    if (null != pop3Host && !"".equals(pop3Host)) {
      try {
        // check host name/address
        final InetAddress address = InetAddress.getByName(pop3Host);
        Socket socket = new Socket();
        try {
          socket.connect(new InetSocketAddress(address, 110), 5 * 1000);
          socket.close();
        } catch (IOException e) {
          errors.put(Configuration.APP_MAIL_POP3_HOST, Configuration.APP_MAIL_POP3_HOST + ".connect");
        }
      } catch (UnknownHostException e) {
        errors.put(Configuration.APP_MAIL_POP3_HOST, Configuration.APP_MAIL_POP3_HOST + ".unknown");
      }
    }

    String pop3User = request.getParameter(Configuration.APP_MAIL_POP3_USER);
    config.setMailPop3User(pop3User);
    String pop3Pass = request.getParameter(Configuration.APP_MAIL_POP3_PASSWORD);
    config.setMailPop3Password(pop3Pass);
    String blogPass = request.getParameter(Configuration.APP_MAIL_BLOG_PASSWORD);
    config.setMailBlogPassword(blogPass);
    String pop3Interval = request.getParameter(Configuration.APP_MAIL_POP3_INTERVAL);
    config.setMailPop3Interval(pop3Interval);

    if (config.getMailPop3Host() != null && !"".equals(config.getMailPop3Host())) {
      if (pop3User == null || "".equals(pop3User)) {
        errors.put(Configuration.APP_MAIL_POP3_USER, Configuration.APP_MAIL_POP3_USER);
      }

      if (blogPass == null || "".equals(blogPass) || blogPass.length() < 3) {
        errors.put(Configuration.APP_MAIL_BLOG_PASSWORD, Configuration.APP_MAIL_BLOG_PASSWORD);
      }

      try {
        int interval = Integer.parseInt(pop3Interval);
        if (interval < 5) {
          errors.put(Configuration.APP_MAIL_POP3_INTERVAL, Configuration.APP_MAIL_POP3_INTERVAL);
        }
      } catch (NumberFormatException e) {
        errors.put(Configuration.APP_MAIL_POP3_INTERVAL, Configuration.APP_MAIL_POP3_INTERVAL + ".format");
      }
    }

    return errors;
  }
}
