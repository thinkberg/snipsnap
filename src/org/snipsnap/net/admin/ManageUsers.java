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
import org.snipsnap.user.UserManager;
import org.snipsnap.container.Components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ManageUsers implements SetupHandler {
  public String getName() {
    return "users";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    if (request.getParameter("remove") != null) {
      errors.put("fatal", "fatal");
    } else if (request.getParameter("edit") != null) {
      UserManager um = (UserManager) Components.getComponent(UserManager.class);
      request.setAttribute("user", um.load(request.getParameter("edit_login")));
    } else if (request.getParameter("save") != null) {
      String login = request.getParameter("config.users.login");
      String passwd = request.getParameter("config.users.password");
      String passwdVrfy = request.getParameter("config.users.password.vrfy");
      String email = request.getParameter("config.users.email");
      String[] roles = request.getParameterValues("config.users.roles");
      String status = request.getParameter("config.users.status");
    }

    return errors;
  }
}
