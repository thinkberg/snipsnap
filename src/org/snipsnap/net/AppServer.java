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
package com.neotis.net;

import com.neotis.app.Application;
import com.neotis.user.User;
import com.neotis.user.UserManager;
import org.mortbay.http.HttpListener;
import org.mortbay.http.HttpRequest;
import org.mortbay.http.HttpResponse;
import org.mortbay.http.SecurityConstraint;
import org.mortbay.http.UserPrincipal;
import org.mortbay.http.UserRealm;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.MultiException;
import org.mortbay.util.InetAddrPort;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Iterator;
import java.util.Map;

/**
 * Application Server
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AppServer {

  private static Server jettyServer;
  private static WebApplicationContext adminContext;
  private static Properties config;

  public static void main(String args[]) {
    System.out.println("SnipSnap $Revision$");
    System.out.println("Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel. "
                       + "All Rights Reserved.");
    System.out.println("See License Agreement for terms and conditions of use.");
    initServer();
    checkConfig();
  }

  /**
   * Initialize Server and load administrative web application.
   */
  private static void initServer() {
    try {
      jettyServer = new Server("./conf/server.conf");
      jettyServer.start();
      adminContext = addApplication("/admin", "./lib/SnipSnap-admin.war");
    } catch (IOException e) {
      System.err.println("AppServer: configuration not found: " + e);
    } catch (Exception e) {
      System.err.println("AppServer: unable to load servlet class: " + e);
    }
  }

  private static void checkConfig() {
    WebApplicationContext context = addApplication("/", "./app");
    System.out.println("HACK: added default application: "+context);

    config = new Properties();
    try {
      FileInputStream configFile = new FileInputStream("./conf/local.conf");
      config.load(configFile);
    } catch(IOException e) {
      System.out.println("INFO: Server is still unconfigured!");
      System.out.println("INFO: Point your browser to the following address:");
      HttpListener listener = jettyServer.getListeners()[0];
      String host = listener.getHost();
      if (InetAddrPort.__0_0_0_0.equals(host)) {
	host = "localhost";
      }
      System.out.println("INFO: http://"+host+":"+listener.getPort()+"/admin");
    }
  }

  private static WebApplicationContext addApplication(String root, String app) {
    WebApplicationContext context = null;
    try {
      context = jettyServer.addWebApplication(root, app);
      context.start();
    } catch (Exception e) {
      System.err.println("AppServer: configuration not found: " + e);
    }
    return context;
  }
}
