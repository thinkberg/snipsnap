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

import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.user.UserManager;
import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Main AdminServlet
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AdminServlet extends HttpServlet {

  List free = Arrays.asList(new String[]{"/login.jsp", "/install.jsp", "/finished.jsp"});

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    // get or create session and application object
    HttpSession session = request.getSession(true);

    UserManager um = UserManager.getInstance();
    Collection servers = HttpServer.getHttpServers();
    AppConfiguration config = (AppConfiguration) session.getAttribute("config");
    if (null == config) {
      config = new AppConfiguration(new File("./conf/local.conf"));
    }

    session.setAttribute("servers", servers);
    session.setAttribute("config", config);

    String command = request.getPathInfo();
    if (null == command || "/".equals(command)) {
      if (config != null) {
        if (session.getAttribute("admin") != null) {
          response.sendRedirect(SnipLink.absoluteLink(request, "/exec/welcome.jsp"));
        } else {
          response.sendRedirect(SnipLink.absoluteLink(request, "/exec/login.jsp"));
        }
      } else {
        response.sendRedirect(SnipLink.absoluteLink(request, "/exec/install.jsp"));
      }
      return;
    }

    if (!free.contains(command) && command.endsWith(".jsp") && session.getAttribute("admin") == null) {
      response.sendRedirect(SnipLink.absoluteLink(request, "/exec/login.jsp"));
      return;
    }

    request.setAttribute("page", command);
    RequestDispatcher dispatcher = null;
    if (command.endsWith(".jsp")) {
      prepareUserManager(servers, request, response);
      dispatcher = request.getRequestDispatcher("/main.jsp");
    } else {
      dispatcher = request.getRequestDispatcher(command);
    }

    if (dispatcher != null) {
      response.addHeader("Pragma", "no-cache");
      response.addHeader("Cache-Control", "no-cache, no-store");
      dispatcher.forward(request, response);
    } else {
      Map errors = new HashMap();
      errors.put(command, "Function not implemented!");
      session.setAttribute("errors", errors);
      response.sendRedirect(SnipLink.absoluteLink(request, "/exec"));
    }
  }

  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doGet(request, response);
  }

  private void prepareUserManager(Collection servers, HttpServletRequest request, HttpServletResponse response) {
    Iterator it = servers.iterator();
    HttpSession session = request.getSession(false);
    request.setAttribute("prepare", "true");
    while (it.hasNext()) {
      HttpServer server = (HttpServer) it.next();
      HttpContext context[] = server.getContexts();
      Map usermanagers = new HashMap();
      for (int i = 0; i < context.length; i++) {
        String contextPath = context[i].getContextPath();
        try {
          RequestDispatcher appUserManagerDispatcher =
            getServletContext().getContext(contextPath).getNamedDispatcher("org.snipsnap.net.UserManagerServlet");
          appUserManagerDispatcher.forward(request, response);
          usermanagers.put(contextPath, request.getAttribute("usermanager"));
        } catch (Exception e) {
          System.err.println("AdminServlet: no user manager servlet available on: " + contextPath);
        }
      }
      session.setAttribute("usermanagers", usermanagers);
    }
    request.removeAttribute("prepare");
  }
}
