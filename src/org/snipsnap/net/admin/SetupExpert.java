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

import org.radeox.util.logging.LogHandler;
import org.radeox.util.logging.Logger;
import snipsnap.api.config.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class SetupExpert implements SetupHandler {
  public String getName() {
    return "expert";
  }

  public Map setup(HttpServletRequest request, HttpServletResponse response, Configuration config, Map errors) {
    config.setAuth(request.getParameter(Configuration.APP_AUTH));
    String startSnip = request.getParameter(Configuration.APP_START_SNIP);
    config.setStartSnip(null == startSnip || "".equals(startSnip) ? "start" : startSnip);
    config.setPermCreateSnip(allowDeny(request.getParameter(Configuration.APP_PERM_CREATESNIP)));
    String logger = request.getParameter(Configuration.APP_LOGGER);
    config.setLogger(logger != null && logger.length() > 0 ? logger : "org.radeox.util.logging.NullLogger");
    // initalize logger before starting to load configurations
    try {
      Logger.setHandler((LogHandler) Class.forName(config.getLogger()).newInstance());
    } catch (Exception e) {
      System.err.println("InitFilter: LogHandler not found: " + logger);
    }
    String cache = request.getParameter(Configuration.APP_CACHE);
    config.setCache(cache != null && cache.length() > 0 ? cache : "full");
    String encoding = request.getParameter(Configuration.APP_ENCODING);
    config.setEncoding(encoding != null && encoding.length() > 0 ? encoding : "UTF-8");

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
