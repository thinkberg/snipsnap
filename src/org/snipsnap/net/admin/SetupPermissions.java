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

import snipsnap.api.config.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class SetupPermissions implements SetupHandler {
  public String getName() {
    return "permissions";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    config.setPermRegister(allowDeny(request.getParameter(Configuration.APP_PERM_REGISTER)));
    config.setPermWeblogsPing(allowDeny(request.getParameter(Configuration.APP_PERM_WEBLOGSPING)));
    config.setPermNotification(allowDeny(request.getParameter(snipsnap.api.config.Configuration.APP_PERM_NOTIFICATION)));
    config.setPermExternalImages(allowDeny(request.getParameter(snipsnap.api.config.Configuration.APP_PERM_EXTERNALIMAGES)));
    config.setPermMultiplePosts(allowDeny(request.getParameter(Configuration.APP_PERM_MULTIPLEPOSTS)));
    return errors;
  }

  private String allowDeny(String value) {
    if ("allow".equals(value)) {
      return value;
    } else {
      return "deny";
    }
  }
}
