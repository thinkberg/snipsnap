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

public class SetupMail implements SetupHandler {
  public String getName() {
    return "mail";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String mailHost = request.getParameter(Configuration.APP_MAIL_HOST);
    config.setMailHost(mailHost);
    if (null != mailHost && !"".equals(mailHost)) {
      try {
        // check host name/address
        final InetAddress address = InetAddress.getByName(mailHost);
        Socket socket = new Socket();
        try {
          socket.connect(new InetSocketAddress(address, 25), 5 * 1000);
          socket.close();
        } catch (IOException e) {
          errors.put(Configuration.APP_MAIL_HOST, Configuration.APP_MAIL_HOST + ".connect");
        }
      } catch (UnknownHostException e) {
        errors.put(Configuration.APP_MAIL_HOST, Configuration.APP_MAIL_HOST + ".unknown");
      }
    }

    String mailDomain = request.getParameter(Configuration.APP_MAIL_DOMAIN);
    config.setMailDomain(mailDomain);
    if (config.getMailHost() != null && !"".equals(config.getMailHost())) {
      if (mailDomain == null || "".equals(mailDomain) || mailDomain.indexOf('@') != -1) {
        errors.put(Configuration.APP_MAIL_DOMAIN, Configuration.APP_MAIL_DOMAIN);
      }
    }

    return errors;
  }
}
