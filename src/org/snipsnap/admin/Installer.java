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
package com.neotis.admin;

import com.neotis.config.CreateDB;
import com.neotis.user.UserManager;
import com.neotis.util.JarExtractor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.zip.ZipFile;
import java.util.jar.JarFile;
import java.net.UnknownHostException;

import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;
import org.mortbay.jetty.servlet.WebApplicationContext;

/**
 * Installs an unpacks the default application
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Installer extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    // get or create session and application object
    HttpSession session = request.getSession(false);
    if (null == session) {
      response.sendRedirect("/admin/");
      return;
    }
    Map errors = new HashMap();
    session.removeAttribute("errors");

    UserManager um = UserManager.getInstance();
    Configuration config = (Configuration) session.getAttribute("config");

    config.setUserName(request.getParameter("username"));
    if (null == config.getUserName() || config.getUserName().length() == 0) {
      errors.put("username", "You must enter a user name!");
    }
    config.setEmail(request.getParameter("email"));

    String password = request.getParameter("password");
    String password2 = request.getParameter("password2");
    if (null == password || password.length() == 0 || !password.equals(password2)) {
      errors.put("password", "Passwords do not match!");
    }

    config.setHost(request.getParameter("host"));
    try {
      config.setPort(Integer.parseInt(request.getParameter("port")));
    } catch (NumberFormatException e) {
      errors.put("port", "The port '" + request.getParameter("port") + "' is not a valid number!");
    }
    config.setContextPath(request.getParameter("context"));

    if (errors.size() != 0) {
      sendError(session, errors, response);
      return;
    }

    InetAddrPort addrPort = new InetAddrPort();
    try {
      String host = config.getHost();
      if(host != null && host.length() > 0) {
        addrPort.setHost(host);
      } else {
        addrPort.setHost(InetAddrPort.__0_0_0_0);
      }
      addrPort.setPort(config.getPort());
    } catch (UnknownHostException e) {
      errors.put("host", "The host you entered is unknown, leave blank to bind server to the default host name.");
      sendError(session, errors, response);
      return;
    }

    Collection servers = Server.getHttpServers();
    Iterator it = servers.iterator();
    Server server = null;
    if(it.hasNext()) {
      server = (Server)it.next();
      System.out.println("servers host map: "+server.getHostMap());

      try {
        server.addListener(new SocketListener(addrPort));
      } catch (Exception e) {
        System.err.println("Installer: error configuring socket listener: "+e);
        errors.put("host", "Unable to configure web server. Either you cannot run the web server on port "
                           +config.getPort()+" or there is something wrong with your host name.");
        sendError(session, errors, response);
        return;
      }
    }

    try {
      JarExtractor.extract(new JarFile("./lib/SnipSnap-template.war", true), new File("./app/"));
    } catch (IOException e) {
      errors.put("fatal", "Unable to extract default application, please see server.log for details!");
      sendError(session, errors, response);
      return;
    }



    CreateDB.createDB(config.getUserName(), password, config.getEmail());
    config.save("./conf/local.conf");
    try {
      WebApplicationContext context =
        server.addWebApplication(config.getContextPath(), "./app/");
      context.start();
    } catch(Exception e) {
      System.err.println("Installer: unable to start application: "+e);
      errors.put("fatal", "Cannot start application: "+e);
      sendError(session, errors, response);
      return;
    }

    response.sendRedirect(config.getContextPath());
  }

  private void sendError(HttpSession session, Map errors, HttpServletResponse response) throws IOException {
    session.setAttribute("errors", errors);
    response.sendRedirect("/admin/exec/install.jsp");
  }

}
