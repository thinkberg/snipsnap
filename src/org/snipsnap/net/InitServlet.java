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

import org.radeox.util.logging.LogHandler;
import org.radeox.util.logging.Logger;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.Snip;
import org.snipsnap.container.Components;

import javax.servlet.*;
import java.io.File;
import java.io.IOException;

/**
 * Only used to initialize configuration. The configuration file may either be set using
 * a servlet context variable or as a servlet parameter "config".
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class InitServlet extends GenericServlet {
  public void init(ServletConfig servletConfig) throws ServletException {
    ServletContext context = servletConfig.getServletContext();

    // check servlet context and then local servlet parameter or assume WEB-INF
    String configParam = (String) context.getAttribute(ServerConfiguration.INIT_PARAM);
    if (null == configParam) {
      configParam = context.getRealPath(context.getInitParameter(ServerConfiguration.INIT_PARAM));
    }
    if (null == configParam) {
      configParam = context.getRealPath("/WEB-INF/application.conf");
    }
    File configFile = new File(configParam);

    // create new configuration instance
    try {
      Configuration config = ConfigurationProxy.newInstance();
      if (configFile.exists()) {
        config.load(configFile);
      } else {
        config.setFile(configFile);
        if(!config.isInstalled()) {
          System.out.println("Unconfigured SnipSnap. Please visit the web site");
          System.out.println("and finish the installation procedure.");
        } else {
          SnipSpace space = (SnipSpace)Components.getComponent(SnipSpace.class);
          if(space.exists(Configuration.SNIPSNAP_CONFIG)) {
            Snip configSnip = space.load(Configuration.SNIPSNAP_CONFIG);
            String configContent = configSnip.getContent();
          }

        }
      }
      try {
        Logger.setHandler((LogHandler) Class.forName(config.getLogger()).newInstance());
      } catch (Exception e) {
        System.err.println("InitServlet: LogHandler not found: " + config.getLogger());
      }

      if (config.allow(Configuration.APP_PERM_WEBLOGSPING)) {
        System.out.println("WARNING: " + config.getName() + ": Weblogs ping is enabled.\n" +
                           "This means that SnipSnap sends notifications to hosts on the internet\n" +
                           "when your weblog changes. To turn this off take a look at the FAQ at\n" +
                           ">> http://snipsnap.org/space/faq <<\n");
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
