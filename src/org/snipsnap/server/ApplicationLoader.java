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
package org.snipsnap.server;

import org.mortbay.http.SocketListener;
import org.mortbay.http.HttpServer;
import org.mortbay.http.HttpListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.Resource;
import org.snipsnap.config.AppConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.net.URLClassLoader;

/**
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class ApplicationLoader {
  protected static Map applications = new HashMap();
  protected static int errors = 0;

  /**
   * Create a new application load that uses the given jetty server and searches for applications in
   * the root directory specified.
   * @param root the root directory of a number of applications
   */
  public static int loadApplications(String root) {
    File rootDir = new File(root);
    if (rootDir.exists()) {
      if (rootDir.isDirectory()) {
        File files[] = rootDir.listFiles();
        for (int i = 0; i < files.length; i++) {
          if (files[i].isDirectory()) {
            File configFile = new File(files[i], "application.conf");
            if (configFile.exists()) {
              try {
                AppConfiguration config = new AppConfiguration(configFile);
                WebApplicationContext context = loadApplication(config);
                applications.put(files[i], context);
                System.out.println("Started application '" + files[i].getName() + "'");
              } catch (Exception e) {
                errors++;
                e.printStackTrace();
                System.out.println("WARNING: unable to load application '" + files[i].getName() + "': " + e.getMessage());
              }
            }
          }
        }
      } else {
        throw new ExceptionInInitializerError("application root '" + root + "' is not a directory");
      }
    }
    return errors;
  }

  public static int getApplicationErrorCount() {
    return errors;
  }

  public static int getApplicationCount() {
    return applications.size();
  }

  public static WebApplicationContext loadApplication(AppConfiguration config) throws Exception {
    File root = config.getFile().getParentFile();

    String host = config.getHost();
    if(host == null || host.length() == 0) {
      host = InetAddrPort.__0_0_0_0;
    }
    int port = config.getPort();

    Server installServer = null;
    HttpListener listener = null;

    Iterator it = Server.getHttpServers().iterator();
    while(listener == null && it.hasNext()) {
      HttpServer server = (HttpServer)it.next();
      HttpListener listeners[] = server.getListeners();
      for(int i = 0; i < listeners.length; i++) {
        if(server instanceof Server && listeners[i].getHost().equals(host) && listeners[i].getPort() == port) {
          System.err.println("ApplicationLoader: found existing server: "+server);
          installServer = (Server)server;
          listener = listeners[i];
        }
      }
    }


    if(null == listener) {
      installServer = new Server();
      listener = new SocketListener(new InetAddrPort(config.getHost(), config.getPort()));
      installServer.addListener(listener);
      installServer.start();
    }

    if(listener instanceof SocketListener) {
      System.err.println("ApplicationLoader: limiting threads per server to 30");
      ((SocketListener)listener).setMaxThreads(30);
    }

    // start web application context
    WebApplicationContext context = installServer.addWebApplication(config.getContextPath(), root.getAbsolutePath() + "/app");
    context.setAttribute(AppConfiguration.INIT_PARAM, config.getFile().getAbsolutePath());
    context.start();

    installServer.start();
    return context;
  }
}