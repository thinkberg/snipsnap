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

import org.snipsnap.config.AppConfiguration;
import org.snipsnap.config.Configuration;
import org.radeox.util.logging.LogHandler;
import org.radeox.util.logging.Logger;

import javax.servlet.*;
import java.io.IOException;

/**
 * Only used to initialize configuration.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class InitServlet extends GenericServlet {
  public void init(ServletConfig servletConfig) throws ServletException {
    String configFile = (String) servletConfig.getServletContext().getAttribute(Configuration.INIT_PARAM);
    if (null == configFile) {
      configFile = servletConfig.getServletContext().getRealPath("WEB-INF/application.conf");
    }
    try {
      AppConfiguration config = AppConfiguration.getInstance(configFile);
      try {
        Logger.setHandler((LogHandler) Class.forName(config.getLogger()).newInstance());
      } catch (Exception e) {
        System.err.println("InitServlet: LogHandler not found: " + config.getLogger());
      }
    } catch (IOException e) {
      e.printStackTrace();
      System.err.println("InitServlet: Unable to load configuration for this application: " + e);
    }
  }

  public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
    // do nothing
  }
}
