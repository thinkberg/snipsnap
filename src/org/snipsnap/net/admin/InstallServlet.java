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
package org.snipsnap.net.admin;

import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.snip.SnipLink;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class InstallServlet extends HttpServlet {

  protected final static String ATT_APPLICATION = "app";
  protected final static String ATT_CONFIG = "config";

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    HttpSession session = request.getSession();
    Application app = Application.getInstance(session);
    Configuration config = app.getConfiguration();

    if(!config.isInstalled()) {
      session.setAttribute(ATT_APPLICATION, app);
      session.setAttribute(ATT_CONFIG, config);

      if(request.getParameter("install") == null) {
        if(!request.getContextPath().equals(config.getPath())) {
          config.set(Configuration.APP_PATH, request.getContextPath());
        }
        RequestDispatcher dispatcher = request.getRequestDispatcher("/install/install.jsp");
        dispatcher.forward(request, response);
        return;
      } else {
        
        System.out.println("INSTALLING: "+request);
      }
    }
    response.sendRedirect(SnipLink.absoluteLink("/"));
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }
}