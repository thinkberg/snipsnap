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

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpListener;
import org.mortbay.http.HttpServer;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.MultiException;
import org.snipsnap.config.Globals;
import org.snipsnap.config.ServerConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Application Server handler that loads, starts, stops and unloads applications.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class ApplicationLoader {
  private final static String WEBINF = "__WEBINF";
  protected static Map applications = new HashMap();
  protected static int errors = 0;

  /**
   * Create a new application load that uses the given jetty server and searches for applications in
   * the root directory specified.
   *
   * @param root the root directory of a number of applications
   */
  public static int loadApplications(String root) {
    File rootDir = new File(root);
    if (rootDir.exists() && rootDir.isDirectory()) {
      File files[] = rootDir.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          try {
            Properties config = getGlobals(root, files[i].getName());
            if (config != null) {
              loadApplication(config);
            }
          } catch (Exception e) {
            errors++;
            e.printStackTrace();
            System.out.println("WARNING: unable to load application '" + files[i].getName() + "': " + e.getMessage());
          } catch (Error err) {
            errors++;
            err.printStackTrace();
            System.out.println("FATAL: unable to load application: '" + files[i].getName() + "': " + err.getMessage());
          }
        }
      }
    }
    return errors;
  }

  private static String getName(Properties config) {
    String host = config.getProperty(Globals.APP_HOST) == null ? "" : config.getProperty(Globals.APP_HOST);
    String port = config.getProperty(Globals.APP_PORT) == null ? "8668" : config.getProperty(Globals.APP_PORT);
    String contextPath = config.getProperty(Globals.APP_PATH);
    if (contextPath == null || contextPath.length() == 0) {
      contextPath = "";
    }
    return normalize(host + port + contextPath).replace('/', '_');
  }

  private static String normalize(String name) {
    return name.replace(' ', '_');
  }

  private static Properties getGlobals(String root, String name) throws IOException {
    File rootDir = new File(root);
    if (rootDir.exists() && rootDir.isDirectory()) {
      File appDir = new File(rootDir, normalize(name));
      if (appDir.isDirectory()) {
        File configFile = new File(appDir, "WEB-INF/application.conf");
        if (!configFile.exists()) {
          configFile = new File(appDir, "webapp/WEB-INF/application.conf");
        }

        if (configFile.exists()) {
          Properties config = new Properties();
          config.load(new FileInputStream(configFile));
          config.setProperty(WEBINF, configFile.getParentFile().getCanonicalPath());
          return config;
        }
      }
    }
    return null;
  }

  public static Properties reloadApplication(String root, String name) throws Exception {
    Properties config = getGlobals(root, name);
    if (config != null) {
      unloadApplication(config);
      loadApplication(config);
      return config;
    }
    return null;
  }

  public static Properties loadApplication(String root, String name) throws Exception {
    Properties config = getGlobals(root, name);
    if (config != null) {
      loadApplication(config);
      return config;
    }
    return null;
  }

  public static void unloadApplication(String root, String name) throws Exception {
    Properties config = getGlobals(root, name);
    if (config != null) {
      unloadApplication(config);
    }
  }


  public static int getApplicationErrorCount() {
    return errors;
  }

  public static int getApplicationCount() {
    return applications.size();
  }

  private static WebApplicationContext loadApplication(Properties config) throws Exception {
    String appName = getName(config);
    if (applications.get(appName) != null) {
      WebApplicationContext context = (WebApplicationContext) applications.get(appName);
      if (context.isStarted()) {
        throw new Exception("ApplicationLoader: '" + appName + "' already started");
      }
    }

    String host = config.getProperty(Globals.APP_HOST) == null ? "" : config.getProperty(Globals.APP_HOST);
    String port = config.getProperty(Globals.APP_PORT) == null ? "8668" : config.getProperty(Globals.APP_PORT);
    String contextPath = config.getProperty(Globals.APP_PATH);
    if (contextPath == null || contextPath.length() == 0) {
      contextPath = "/";
    }

    File appRoot = new File(config.getProperty(WEBINF)).getParentFile();
    boolean extract = appRoot.getName().equals("webapp");
    if (extract) {
      appRoot = appRoot.getParentFile();
    }

    String snipsnapWar = "lib/snipsnap.war";
    if (!(new File(snipsnapWar).exists())) {
      URL warFile = ApplicationLoader.class.getResource("/snipsnap.war");
      if (warFile != null) {
        snipsnapWar = warFile.toString();
      }
    }
    Server server = findOrCreateServer(host, port, contextPath);
    WebApplicationContext context =
            server.addWebApplication(null, contextPath,
                                     extract ? snipsnapWar : appRoot.getCanonicalPath());

    if (extract) {
      context.setTempDirectory(appRoot.getCanonicalFile());
      context.setExtractWAR(true);
    }
    context.setAttribute(ServerConfiguration.INIT_PARAM, new File(config.getProperty(WEBINF), "application.conf").getCanonicalPath());
    context.start();

    applications.put(appName, context);
    System.out.println("Started '" + appName + "' at " + getUrl(config));

    return context;
  }

  private static void unloadApplication(Properties config) {
    String appName = getName(config);
    try {
      WebApplicationContext context = (WebApplicationContext) applications.get(appName);
      context.stop();
      context.destroy();
    } catch (Exception e) {
      System.out.println("Unable to stop '" + appName + "': " + e);
      e.printStackTrace();
    }
    System.out.println("Stopped application '" + appName + "'");
  }

  private static Server findOrCreateServer(String host, String port, String path) throws IOException {
    //System.out.print("Scanning for " + host + ":" + port + path);
    Collection httpServers = Server.getHttpServers();

    // try exact match first (host AND port)
    HttpServer httpServer = findHost(httpServers, host, port);
    //System.out.print(httpServer != null ? "<!>" : "<?>");
    if (null == httpServer) {
      httpServer = new Server();
      ((Server) httpServer).setRootWebApp("/");
      try {
        httpServer.start();
      } catch (MultiException e) {
        e.printStackTrace();
        throw new IOException("unable to start HTTP Server: " + e.getMessage());
      }
      //System.out.print("(new server)");

      try {
        httpServer.addListener(port).start();
      } catch (Exception e) {
        e.printStackTrace();
        throw new IOException("Unable to create port listener: " + e.getMessage());
      }
      //System.out.print("(port " + port + ")");
    } else {
      if (getContext(httpServer, host, path) != null) {
        throw new IOException("Conflicting HTTP Server configuration found: '" + host + ":" + port + path + "/'");
      }
    }
    //System.out.println();
    return (Server) httpServer;
  }

  private static HttpServer findHost(Collection servers, String host, String port) {
    host = (host == null ? "" : host);

    //System.out.println("{" + host + ":" + port + "}");

    Iterator it = servers.iterator();
    while (it.hasNext()) {
      HttpServer server = (HttpServer) it.next();
      HttpListener listener[] = server.getListeners();
      for (int i = 0; i < listener.length; i++) {
        String listenerHost = listener[i].getHost();
        listenerHost = (listenerHost == null || listenerHost.equals(InetAddrPort.__0_0_0_0) ? "" : listenerHost);
        String listenerPort = "" + listener[i].getPort();
        if (port != null) {
          //System.out.print("[" + listenerHost + ":" + listenerPort);
          if (listenerHost.equals(host) && listenerPort.equals(port)) {
            //System.out.println("!]");
            return server;
          }
        } else {
          //System.out.print("[" + listenerHost);
          if (listenerHost.equals(host)) {
            System.out.print("!]");
            return server;
          }
        }
        //System.out.print("]");
      }
    }
    return null;
  }

  private static HttpContext getContext(HttpServer httpServer, String host, String path) {
    HttpContext contexts[] = httpServer.getContexts();
    for (int i = 0; i < contexts.length; i++) {
      HttpContext context = contexts[i];
      String contextPath = context.getContextPath();
      //System.out.print("{" + contextPath + "}");
      if (contextPath.equals(path) || contextPath.equals(path + "/*")) {
        String[] vhosts = context.getVirtualHosts();
        for (int j = 0; j < vhosts.length; j++) {
          if (vhosts[j].equals(host)) {
            return context;
          }
        }
      }
    }
    return null;
  }

  /**
   * Return base url to Snipsnap instance
   */
  public static String getUrl(Properties config) {
    String host = config.getProperty(Globals.APP_HOST);
    String port = config.getProperty(Globals.APP_PORT);
    String path = config.getProperty(Globals.APP_PATH);

    StringBuffer tmp = new StringBuffer();
    tmp.append("http://");
    try {
      tmp.append(host == null || host.length() == 0 || "localhost".equals(host) ? InetAddress.getLocalHost().getHostName() : host);
    } catch (UnknownHostException e) {
      tmp.append(System.getProperty("host", "localhost"));
    }
    if (port != null && !"80".equals(port)) {
      tmp.append(":");
      tmp.append(port);
    }
    tmp.append(path != null ? path : "");
    return tmp.toString();
  }

}
