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

import org.apache.xmlrpc.WebServer;
import org.mortbay.http.HttpListener;
import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.MultiException;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.user.Digest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
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
    // load server defaults configuration
    try {
      serverInfo.load(AppServer.class.getResourceAsStream("/conf/snipsnap.conf"));
    } catch (Exception e) {
      System.err.println("AppServer: warning: unable to load server defaults: " + e);
    }
    System.setProperty(ServerConfiguration.VERSION, serverInfo.getProperty(ServerConfiguration.VERSION, "<unknown version>"));

    printCopyright();

    // load additional local configuration
    try {
      serverInfo.load(new FileInputStream("conf/server.conf"));
    } catch (IOException e) {
      serverInfo.setProperty(ServerConfiguration.ADMIN_PASS,
                             Digest.getDigest("" + new Date()).substring(0, 5).toLowerCase());
      try {
        serverInfo.store(new FileOutputStream("conf/server.conf"), " SnipSnap Server Configuration");
      } catch (IOException e1) {
        System.err.println("AppServer: warning: unable to store local server configuration: " + e);
      }
    }
    serverInfo = parseArguments(args, serverInfo);

    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        Shutdown.shutdown();
      }
    });

    // set encoding of the JVM and make sure Jetty decodes URIs correctly
    String enc = serverInfo.getProperty(ServerConfiguration.ENCODING, "UTF-8");
    System.setProperty("file.encoding", enc);
    System.setProperty("org.mortbay.util.URI.charset", "iso-8859-1");

    // start jetty server and install web application
    try {
      File appDir = new File(serverInfo.getProperty(ServerConfiguration.WEBAPP_ROOT));
      if (!appDir.exists()) {
        appDir.mkdir();
      }
      jettyServer = new Server(getResource("/conf/jetty.conf", "./conf/jetty.conf"));
      jettyServer.start();
    } catch (IOException e) {
      System.err.println("AppServer: warning: admin server configuration not found: " + e);
    } catch (MultiException e) {
      Iterator exceptions = e.getExceptions().iterator();
      while (exceptions.hasNext()) {
        Exception ex = (Exception) exceptions.next();
        ex.printStackTrace();
        System.out.println("ERROR: can't start server: " + ex.getMessage());
      }
      System.exit(-1);
    }

    try {
      URL xmlRpcServerUrl = new URL(serverInfo.getProperty(ServerConfiguration.ADMIN_URL));
      WebServer xmlRpcServer = new WebServer(xmlRpcServerUrl.getPort());
      // xmlRpcServer.setParanoid(true);
      xmlRpcServer.addHandler("$default", new AdminXmlRpcHandler(serverInfo));
      xmlRpcServer.start();
    } catch (Exception e) {
      System.out.println("ERROR: can't start administrative server interface (XML-RPC): "+e.getMessage());
      e.printStackTrace();
    }

    // now, after loading all possible services we will look for applications and start them
    int errors = ApplicationLoader.loadApplications(serverInfo.getProperty(ServerConfiguration.WEBAPP_ROOT));
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
      System.out.println("ATTENTION: http://" + host + ":" + listener.getPort() + "/install/" + serverInfo.getProperty(ServerConfiguration.ADMIN_PASS));
      System.out.println("ATTENTION: To force setup of a specific host and port add '?expert=true'");
      System.out.println("ATTENTION: to the above URL.");
    } else {
      System.out.println(ApplicationLoader.getApplicationCount() + " applications loaded and running (" + errors + " errors).");
    }
  }

  private static void printCopyright() {
    System.out.println("SnipSnap "+ System.getProperty(ServerConfiguration.VERSION));

    // output version and copyright information
    try {
      BufferedReader copyrightReader = new BufferedReader(new InputStreamReader(AppServer.class.getResourceAsStream("/conf/copyright.txt")));
      String line = null;
      while ((line = copyrightReader.readLine()) != null) {
        System.out.println(line);
      }
    } catch (IOException e) {
      // ignore io exception here ...
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
          serverInfo.setProperty(ServerConfiguration.WEBAPP_ROOT, args[i++]);
        } else {
          usage("an argument is required for -root");
        }
      } else if("-port".equals(args[i])) {
        if (args.length >= i + 1 && !args[i + 1].startsWith("-")) {
          serverInfo.setProperty(ServerConfiguration.ADMIN_URL, args[i++]);
        } else {
          usage("an argument is required for -url");
        }
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
    System.out.println("  -url    URL, admin server URL (http://host:port/)");
    System.out.println("  -help   this help text");
    System.exit(0);
  }
}
