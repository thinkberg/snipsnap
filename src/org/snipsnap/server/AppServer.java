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
import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.MultiException;
import org.snipsnap.config.Configuration;

import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Properties;

/**
 * Application Server
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AppServer {
  protected static Properties serverInfo = new Properties();


  protected static Server jettyServer;

  /**
   * Start application server.
   */
  public static void main(String args[]) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        Shutdown.shutdown();
      }
    });

    // load server defaults configuration
    try {
      serverInfo.load(AppServer.class.getResourceAsStream("/conf/snipsnap.conf"));
    } catch (IOException e) {
      // ignore
    }
    System.setProperty(Configuration.VERSION, serverInfo.getProperty(Configuration.VERSION));

    // output version and copyright information
    System.out.println("SnipSnap " + serverInfo.getProperty(Configuration.VERSION));
    BufferedReader copyrightReader = new BufferedReader(new InputStreamReader(AppServer.class.getResourceAsStream("/conf/copyright.txt")));
    String line = null;
    try {
      while ((line = copyrightReader.readLine()) != null) {
        System.out.println(line);
      }
    } catch (IOException e) {
      // ignore io exception here ...
    }

    // load additional local configuration
    try {
      serverInfo.load(new FileInputStream("conf/server.conf"));
    } catch (IOException e) {
      // ignore local server configuration not found
    }
    serverInfo = parseArguments(args, serverInfo);

    // set encoding of the JVM
    String enc = serverInfo.getProperty(Configuration.ENCODING, "UTF-8");
    System.setProperty("file.encoding", enc);

    // start jetty server and install web application
    try {
      jettyServer = new Server(getResource("/conf/jetty.conf", "./conf/jetty.conf"));
      jettyServer.start();
      new AdminServer(serverInfo);
    } catch (IOException e) {
      System.err.println("WARNING: admin server configuration not found: " + e);
    } catch (MultiException e) {
      Iterator exceptions = e.getExceptions().iterator();
      while (exceptions.hasNext()) {
        Exception ex = (Exception) exceptions.next();
        ex.printStackTrace();
        System.out.println("ERROR: can't start server: " + ex.getMessage());
      }
      System.exit(-1);
    }

    // now, after loading all possible services we will look for applications and start them
    int errors = ApplicationLoader.loadApplications(serverInfo.getProperty(Configuration.WEBAPP_ROOT));
    if (errors == 0 && ApplicationLoader.getApplicationCount() == 0) {
      System.out.println("ATTENTION: Server is still unconfigured!");
      System.out.println("ATTENTION: Point your browser to the following address:");
      HttpListener listener = jettyServer.getListeners()[0];
      String host = listener.getHost();
      if (InetAddrPort.__0_0_0_0.equals(host)) {
        try {
          host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
          host = System.getProperty("host", "localhost");
        }
      }
      System.out.println("ATTENTION: http://" + host + ":" + listener.getPort() + "/install");
    } else {
      System.out.println(ApplicationLoader.getApplicationCount() + " applications loaded and running (" + errors + " errors).");
    }

  }

  /**
   * Parse argument from command line and exit in case of erroneous options.
   * The server will immedialy exit if there is a problem and display a usage message
   * @param args the command line arguments
   * @param serverInfo the configuration properties of the server
   * @return the possibly changed configuration properties
   */
  private static Properties parseArguments(String args[], Properties serverInfo) {
    for (int i = 0; i < args.length; i++) {
      if ("-help".equals(args[i])) {
        usage("");
        System.exit(0);
      }

      // the applications root directory
      if ("-root".equals(args[i])) {
        if (args.length >= i + 1 && !args[i + 1].startsWith("-")) {
          serverInfo.setProperty(Configuration.WEBAPP_ROOT, args[i++]);
        } else {
          usage("an argument is required for -root");
        }
      } else if ("-admin".equals(args[i])) {
        if (args.length > i + 1) {
          String command = args[i + 1];
          String argument = null;
          if (args.length > i + 2) {
            argument = args[i + 2];
          }
          if (!AdminServer.execute(Integer.parseInt(serverInfo.getProperty(Configuration.ADMIN_PORT)), command, argument)) {
            System.out.println("Cannot execute administrative command: '" + command + "'");
            System.exit(-1);
          }
        } else {
          usage("an argument is required for -admin");
        }
        System.exit(0);
      }
    }
    return serverInfo;
  }

  /**
   * Get a resource from file if the file exists, else just return the jar resource.
   * @param jarResource the jar file resource (fallback)
   * @param fileResource the file resource
   */
  private static URL getResource(String jarResource, String fileResource) {
    File file = new File(fileResource);
    URL url = null;
    if (file.exists()) {
      try {
        url = file.toURL();
      } catch (MalformedURLException e) {
        System.err.println("Warning: unable to load '" + file + "': " + e.getMessage());
      }
    }

    if (null == url) {
      url = AppServer.class.getResource(jarResource);
    }

    return url;
  }

  /**
   * Display a a message in addition to a usage message.
   * @param message an additional informational text
   */
  private static void usage(String message) {
    System.out.println(message);
    System.out.println("usage: " + AppServer.class.getName() + " [-root <dir>]");
    System.out.println("  -root   directory, where to find the applications for this server");
    Iterator it = AdminServer.COMMANDS.iterator();
    while (it.hasNext()) {
      System.out.println("  -admin " + it.next());
    }
    System.exit(-1);
  }
}
