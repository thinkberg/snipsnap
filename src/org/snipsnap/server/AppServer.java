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

import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import java.util.Locale;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Application Server
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AppServer {
  protected static Configuration snipsnapConfig;
  protected static Configuration serverConfig;
  protected static Server jettyServer;

  public static void main(String args[]) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        System.out.println("Shutting down server ...");
        Shutdown.shutdown();
      }
    });

    try {
      snipsnapConfig = new Configuration("conf/snipsnap.conf");
      System.setProperty("snipsnap."+Configuration.SERVER_VERSION, snipsnapConfig.getVersion());
    } catch (IOException e) {
      e.printStackTrace();
      System.out.println("ERROR: unable to read snipsnap default configuration, aborting");
      System.exit(-1);
    }

    System.out.println("SnipSnap " + snipsnapConfig.getVersion());
    System.out.println("Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel. "
                       + "All Rights Reserved.");
    System.out.println("See License Agreement for terms and conditions of use.");

    // if not server.conf exists create one
    try {
      serverConfig = new Configuration("conf/server.conf");
    } catch (IOException e) {
      try {
        serverConfig = new Configuration("conf/snipsnap.conf");
        serverConfig.store(new File("conf/server.conf"));
      } catch (IOException e1) {
        System.err.println("ERROR: unable to store server.conf, aborting");
        System.exit(-1);
      }
    }

    String enc = serverConfig.getProperty(Configuration.SERVER_ENCODING);
    if(enc != null && enc.length() > 0) {
      System.setProperty("file.encoding", enc);
    } else {
      System.setProperty("file.encoding", "iso-8859-1");
    }

    // start jetty server and install web application
    try {
      jettyServer = new Server("./conf/jetty.conf");
      jettyServer.start();
      String hostname = InetAddress.getLocalHost().getHostName();
      System.out.println("Administrative interface started at http://"+hostname+":8668/install");
    } catch (IOException e) {
      System.err.println("AppServer: admin server configuration not found: " + e);
    } catch (MultiException e) {
      Iterator exceptions = e.getExceptions().iterator();
      while (exceptions.hasNext()) {
        Exception ex = (Exception) exceptions.next();
        ex.printStackTrace();
        System.out.println("ERROR: can't start server: " + ex.getMessage());
      }
      System.exit(-1);
    }

    // start the administrative network interface
    try {
      new AdminServer(Integer.parseInt(serverConfig.getProperty(Configuration.SERVER_ADMIN_PORT).trim()));
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: unable to start administration server: " + e);
    }

    // now, after loading all possible services we will look for applications and start them
    int errors = ApplicationLoader.loadApplications(serverConfig.getProperty(Configuration.SERVER_WEBAPP_ROOT));
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
      System.out.println(ApplicationLoader.getApplicationCount() + " applications loaded and running ("+errors+" errors).");
    }

  }

  /**
   * Parse argument from command line and exit in case of erroneous options.
   * The server will immedialy exit if there is a problem and display a usage message
   * @param args the command line arguments
   * @param adminConfig the configuration properties of the server
   * @return the possibly changed configuration properties
   */
  private static Configuration parseArguments(String args[], Configuration adminConfig) {
    for (int i = 0; i < args.length; i++) {
      // the applications root directory
      if ("-appsroot".equals(args[i])) {
        if (args.length >= i + 1 && !args[i + 1].startsWith("-")) {
          adminConfig.setProperty(Configuration.SERVER_WEBAPP_ROOT, args[i++]);
        } else {
          usage("an argument is required for -appsroot");
        }
        // the port where the server listens for administrative commands
      } else if ("-adminport".equals(args[i]) && !args[i + 1].startsWith("-")) {
        if (args.length >= i + 1) {
          adminConfig.setProperty(Configuration.SERVER_ADMIN_PORT, args[i++].trim());
        } else {
          usage("a number argument is required for -adminport");
        }
        // an administrative command to be sent to the server
      } else if ("-admin".equals(args[i])) {
        if (args.length >= i + 1) {
          try {
            if (!AdminServer.execute(Integer.parseInt(adminConfig.getProperty(Configuration.SERVER_ADMIN_PORT).trim()), args[1])) {
              System.exit(-1);
            }
          } catch (NumberFormatException e) {
            System.out.println("ERROR: admin port '" + adminConfig.getProperty(Configuration.SERVER_ADMIN_PORT) + "' is not a number, aborting");
            System.exit(-1);
          }
        } else {
          usage("an argument is required for -admin");
        }
        System.exit(0);
      }
    }
    return adminConfig;
  }

  /**
   * Display a a message in addition to a usage message.
   * @param message an additional informational text
   */
  private static void usage(String message) {
    System.out.println(message);
    System.out.println("usage: " + AppServer.class.getName() + " [-appsroot <dir>] [-adminport <port>] [-admin <command>]");
    System.out.println("  -appsroot   directory, where to find the applications for this server");
    System.out.println("  -adminport  port number, where the server listens for administrative commands");
    System.out.println("  -admin      send command to server, allowed commands include:");
    System.out.println("              " + AdminServer.COMMANDS);
    System.exit(-1);
  }
}
