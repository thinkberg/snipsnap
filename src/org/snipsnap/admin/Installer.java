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
package org.snipsnap.admin;

import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.server.AdminServer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Installer Servlet handling the initial setup of a new web application.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Installer extends HttpServlet {

  private final static String COMMAND = "installer.command";
  private final static String ERROR = "installer.error";
  private final static String OVERWRITE = "installer.overwrite";

  private final static String ERROR_EXISTS = "installer.error.exists";
  private final static String ERROR_LOAD = "installer.error.load";

  private File webAppDir = null;
  
  public void init(ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
    webAppDir = new File(getServletConfig().getInitParameter(ServerConfiguration.WEBAPP_ROOT));
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    HttpSession session = request.getSession();
    Properties serverConfig = (Properties)session.getAttribute(AdminInitFilter.ATT_CONFIG);

    String command = request.getParameter(COMMAND);
    if("install".equals(command)) {
      String name = request.getParameter(Configuration.APP_NAME);
      String host = request.getParameter(Configuration.APP_HOST);
      String port = request.getParameter(Configuration.APP_PORT);
      String path = request.getParameter(Configuration.APP_PATH);
      boolean overwrite = "true".equals(request.getParameter(OVERWRITE));

      File webInfDir = new File(webAppDir, name+"/webapp/WEB-INF");
      File appConf = new File(webInfDir, "application.conf");
      if(!overwrite && webInfDir.exists() && appConf.exists()) {
        request.setAttribute(ERROR, ERROR_EXISTS);
      } else {
        webInfDir.mkdirs();
        Configuration config = ConfigurationProxy.newInstance();

        config.set(Configuration.APP_NAME, name);
        config.set(Configuration.APP_HOST, host);
        config.set(Configuration.APP_PORT, port);
        config.set(Configuration.APP_PATH, path);
        config.store(new FileOutputStream(appConf));

        int adminPort = Integer.parseInt(serverConfig.getProperty(ServerConfiguration.ADMIN_PORT));
        if(AdminServer.execute(adminPort, "start", name)) {
          response.sendRedirect(config.getUrl());
          return;
        } else {
          request.setAttribute(ERROR, ERROR_LOAD);
        }
      }
    }
    RequestDispatcher dispatcher = request.getRequestDispatcher("install.jsp");
    dispatcher.forward(request, response);
  }
}
