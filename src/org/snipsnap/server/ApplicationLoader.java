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

import org.mortbay.http.HttpListener;
import org.mortbay.http.HttpServer;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.InetAddrPort;
import org.snipsnap.config.AppConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
            File configFile = getConfigFile(root, files[i].getName());
            if (configFile.exists()) {
              try {
                loadApplication(new AppConfiguration(configFile));
              } catch (Exception e) {
                errors++;
                e.printStackTrace();
                System.out.println("WARNING: unable to load application '" + files[i].getName() + "': " + e.getMessage());
              } catch(Error err) {
                errors++;
                err.printStackTrace();
                System.out.println("FATAL: unable to load application: '" + files[i].getName() + "': " + err.getMessage());
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

  private static File getConfigFile(String root, String name) {
    File rootDir = new File(root);
    if (rootDir.exists()) {
      if (rootDir.isDirectory()) {
        File appDir = new File(rootDir, normalize(name));
        if (appDir.isDirectory()) {
          // TODO: handle autoextracted web application
          return new File(appDir, /*"webapp/"+*/"WEB-INF/application.conf");
        }
      }
    }
    return null;
  }

  public static void reloadApplication(String root, String name) throws Exception {
    File configFile = getConfigFile(root, normalize(name));
    if (configFile != null) {
      AppConfiguration config = new AppConfiguration(configFile);
      unloadApplication(config);
      loadApplication(config);
    }
  }

  public static void loadApplication(String root, String name) throws Exception {
    File configFile = getConfigFile(root, normalize(name));
    if (configFile != null) {
      AppConfiguration config = new AppConfiguration(configFile);
      loadApplication(config);
    }
  }

  public static void unloadApplication(String root, String name) throws Exception {
    File configFile = getConfigFile(root, normalize(name));
    if (configFile != null) {
      AppConfiguration config = new AppConfiguration(configFile);
      unloadApplication(config);
    }
  }

  public static int getApplicationErrorCount() {
    return errors;
  }

  public static int getApplicationCount() {
    return applications.size();
  }

  public static WebApplicationContext loadApplication(AppConfiguration config) throws Exception {
    if (applications.get(config.getName()) != null) {
      WebApplicationContext context = (WebApplicationContext) applications.get(config.getName());
      if (context.isStarted()) {
        throw new Exception("ApplicationLoader: '" + config.getName() + "' already started");
      }
    }

    File webInf = config.getFile().getParentFile();
    String host = config.getHost();
    if (host == null || host.length() == 0) {
      host = InetAddrPort.__0_0_0_0;
    }
    int port = config.getPort();

    Server installServer = null;
    HttpListener listener = null;

    Iterator it = Server.getHttpServers().iterator();
    while (listener == null && it.hasNext()) {
      HttpServer server = (HttpServer) it.next();
      HttpListener listeners[] = server.getListeners();
      for (int i = 0; i < listeners.length; i++) {
        if (server instanceof Server && listeners[i].getHost().equals(host) && listeners[i].getPort() == port) {
          //System.out.println("-> "+listeners[i].getHost()+":"+listeners[i].getPort());
          System.err.println("ApplicationLoader: found existing server: " + server);
          installServer = (Server) server;
          listener = listeners[i];
          break;
        }
      }
    }

    if (null == listener) {
      installServer = new Server();
      listener = new SocketListener(new InetAddrPort(config.getHost(), config.getPort()));
      installServer.addListener(listener);
      installServer.start();
    }

    if (listener instanceof SocketListener) {
      System.err.println("ApplicationLoader: limiting threads per server to 30");
      ((SocketListener) listener).setMaxThreads(30);
    }


    // start web application context
    File appRoot = webInf.getParentFile()/*.getParentFile()*/.getAbsoluteFile();
    String contextPath = config.getContextPath();
    if (contextPath == null || contextPath.length() == 0) {
      contextPath = "/";
    }

    // TODO: handle autoextracted war
    WebApplicationContext context =
      installServer.addWebApplication(null, contextPath, appRoot.getCanonicalPath()/*"lib/snipsnap-template.war"*/);
    context.setAttribute(AppConfiguration.INIT_PARAM, config.getFile().getCanonicalPath());
//    context.setTempDirectory(appRoot);
//    context.setExtractWAR(true);
    context.start();

    applications.put(config.getName(), context);
    System.out.println("Started application '" + config.getName() + "' " + config.getUrl());

    return context;
  }

  public static void unloadApplication(AppConfiguration config) {
    try {
      WebApplicationContext context = (WebApplicationContext) applications.get(config.getName());
      context.stop();
      context.destroy();
    } catch (Exception e) {
      System.out.println("Unable to stop '" + config.getName() + "': " + e);
      e.printStackTrace();
    }
    System.out.println("Stopped application '" + config.getName() + "'");
  }

  private static String normalize(String name) {
    return name.replace(' ', '_');
  }
}