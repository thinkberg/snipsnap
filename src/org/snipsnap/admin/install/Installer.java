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
package org.snipsnap.admin.install;

import org.snipsnap.config.ServerConfiguration;

import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Installer servlet that installs the application.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Installer extends HttpServlet {

  private final static String AUTHENTICATED = "installer.authenticated";

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    // check authentication and verify session
    HttpSession session = request.getSession(false);
    if (null == session || !"true".equals(session.getAttribute(AUTHENTICATED))) {
      Properties serverConfig = new Properties();
      serverConfig.load(new FileInputStream("conf/server.conf"));
      String serverPass = serverConfig.getProperty(ServerConfiguration.ADMIN_PASS);
      String installPass = request.getPathInfo();
      if(serverPass == null || !serverPass.equals(installPass)) {
        RequestDispatcher dispatcher = request.getRequestDispatcher("login.jsp");
        dispatcher.forward(request, response);
        return;
      } else {
        session = request.getSession(true);
        session.setAttribute(AUTHENTICATED, "true");
      }
    }

    
  }
}
