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
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.config.ServerConfiguration;

import java.io.File;
import java.util.HashMap;
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
    if (rootDir.exists() && rootDir.isDirectory()) {
      File files[] = rootDir.listFiles();
      for (int i = 0; i < files.length; i++) {
        if (files[i].isDirectory()) {
          File configFile = getConfigFile(root, files[i].getName());
          if (configFile.exists()) {
            try {
              loadApplication(ConfigurationProxy.newInstance(configFile));
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
    } else {
      throw new ExceptionInInitializerError("application root '" + root + "' is not a directory");
    }
    return errors;
  }

  private static File getConfigFile(String root, String name) {
    File rootDir = new File(root);
    if (rootDir.exists() && rootDir.isDirectory()) {
      File appDir = new File(rootDir, normalize(name));
      if (appDir.isDirectory()) {
        File configFile = new File(appDir, "WEB-INF/application.conf");
        if (!configFile.exists()) {
          configFile = new File(appDir, "webapp/WEB-INF/application.conf");
        }
        return configFile;
      }
    }
    return null;
  }

  public static void reloadApplication(String root, String name) throws Exception {
    File configFile = getConfigFile(root, normalize(name));
    if (configFile != null) {
      Configuration config = ConfigurationProxy.newInstance(configFile);
      unloadApplication(config);
      loadApplication(config);
    }
  }

  public static void loadApplication(String root, String name) throws Exception {
    File configFile = getConfigFile(root, normalize(name));
    if (configFile != null) {
      Configuration config = ConfigurationProxy.newInstance(configFile);
      loadApplication(config);
    }
  }

  public static void unloadApplication(String root, String name) throws Exception {
    File configFile = getConfigFile(root, normalize(name));
    if (configFile != null) {
      Configuration config = ConfigurationProxy.newInstance(configFile);
      unloadApplication(config);
    }
  }

  public static int getApplicationErrorCount() {
    return errors;
  }

  public static int getApplicationCount() {
    return applications.size();
  }

  public static WebApplicationContext loadApplication(Configuration config) throws Exception {
    String appName = config.getName();
    if (applications.get(appName) != null) {
      WebApplicationContext context = (WebApplicationContext) applications.get(appName);
      if (context.isStarted()) {
        throw new Exception("ApplicationLoader: '" + appName + "' already started");
      }
    }

    String contextPath = config.getPath();
    if (contextPath == null || contextPath.length() == 0) {
      contextPath = "/";
    }


    HttpListener listener[] = AppServer.jettyServer.getListeners();
    HttpListener existingListener = null;
    for (int i = 0; i < listener.length; i++) {
      int port = Integer.parseInt(config.getPort());
      if (listener[i].getPort() == port) {
        existingListener = listener[i];
      }
    }

    if (existingListener == null) {
      HttpListener newListener = AppServer.jettyServer.addListener(":"+config.getPort());
      newListener.start();
      System.err.println("ApplicationLoader: added new listener: " + newListener);
    }

    File appRoot = config.getFile().getParentFile().getParentFile();
    boolean extract = appRoot.getName().equals("webapp");
    if (extract) {
      appRoot = appRoot.getParentFile();
    }

    WebApplicationContext context =
      AppServer.jettyServer.addWebApplication(config.getHost(),
                                              contextPath,
                                              extract ? "lib/snipsnap-template.war" : appRoot.getCanonicalPath());

    if (extract) {
      context.setTempDirectory(appRoot.getCanonicalFile());
      context.setExtractWAR(true);
    }
    context.setAttribute(ServerConfiguration.INIT_PARAM, config.getFile().getCanonicalPath());
    context.start();

    applications.put(appName, context);
    System.out.println("Started application '" + appName + "' " + config.getUrl());

    return context;
  }

  public static void unloadApplication(Configuration config) {
    String appName = config.getName();
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

  private static String normalize(String name) {
    return name.replace(' ', '_');
  }
}