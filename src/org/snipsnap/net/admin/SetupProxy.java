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

public class SetupProxy implements SetupHandler {
  public String getName() {
    return "proxy";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    String autodetect = request.getParameter(Configuration.APP_REAL_AUTODETECT) != null ? "true" : "false";
    config.setRealAutodetect(autodetect);
    if ("false".equals(autodetect)) {
      config.setRealHost(request.getParameter(Configuration.APP_REAL_HOST));
      String portStr = request.getParameter(Configuration.APP_REAL_PORT);
      config.setRealPort(request.getParameter(Configuration.APP_REAL_PORT));
      if (portStr != null && !"".equals(portStr)) {
        try {
          Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
          errors.put(Configuration.APP_REAL_PORT, Configuration.APP_REAL_PORT);
        }
      }
    }
    String realProtocol = request.getParameter(Configuration.APP_REAL_PROTOCOL);
    if (null != realProtocol && !"".equals(realProtocol)) {
      config.setRealPath(realProtocol.trim());
    }
    String realPath = request.getParameter(Configuration.APP_REAL_PATH);
    if (null != realPath && !"".equals(realPath)) {
      realPath = realPath.trim();
      config.setRealPath(realPath.startsWith("/") ? realPath : "/" + realPath);
    }

    return errors;
  }
}
