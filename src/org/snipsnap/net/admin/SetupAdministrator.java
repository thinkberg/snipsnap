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

public class SetupAdministrator implements SetupHandler {
  public String getName() {
    return "administrator";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String login = request.getParameter(Configuration.APP_ADMIN_LOGIN);
    config.setAdminLogin(login);
    if (null == login || "".equals(login)) {
      errors.put(Configuration.APP_ADMIN_LOGIN, Configuration.APP_ADMIN_LOGIN);
    }
    String password = request.getParameter(Configuration.APP_ADMIN_PASSWORD);
    String verify = request.getParameter(Configuration.APP_ADMIN_PASSWORD + ".vrfy");
    if ((password != null && password.length() > 0) || config.getAdminPassword() == null) {
      if (password == null || password.length() == 0) {
        errors.put(Configuration.APP_ADMIN_PASSWORD, Configuration.APP_ADMIN_PASSWORD);
      } else if (!password.equals(verify)) {
        errors.put(Configuration.APP_ADMIN_PASSWORD, Configuration.APP_ADMIN_PASSWORD + ".match");
      } else if (password.length() < 3) {
        errors.put(Configuration.APP_ADMIN_PASSWORD, Configuration.APP_ADMIN_PASSWORD + ".length");
      } else {
        config.setAdminPassword(password);
      }
    }
    config.setAdminEmail(request.getParameter(Configuration.APP_ADMIN_EMAIL));

    return errors;
  }
}
