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
import org.jdesktop.jdic.desktop.Desktop;
import org.mortbay.http.HttpListener;
import org.mortbay.jetty.Server;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.MultiException;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.user.Digest;

import java.io.*;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Application Server
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AppServer {
  protected static Properties serverPrefsDefaults;
  protected static Preferences serverPrefs;

  protected static Server jettyServer;

  /**
   * Start application server.
   */
  public static void main(String args[]) {
    // load server preferences (and possible convert old configs)
    serverPrefs = getServerPrefs();
    System.setProperty(ServerConfiguration.VERSION, serverPrefs.get(ServerConfiguration.VERSION, "<unknown version>"));

    printCopyright();
    parseArguments(args);

    // make sure we shutdown the server nicely when the JVM is stopped
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        Shutdown.shutdown();
      }
    });

    // set encoding of the JVM and make sure Jetty decodes URIs correctly
    String enc = serverPrefs.get(ServerConfiguration.ENCODING, "UTF-8");
    System.setProperty("file.encoding", enc);
    System.setProperty("org.mortbay.util.URI.charset", enc);

    // start jetty server and install web application
    try {
      jettyServer = new Server(getResource("/conf/jetty.conf", "./conf/jetty.conf"));
      jettyServer.addWebApplication("/install", AppServer.class.getResource("/snipsnap-installer.war").toString());
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
      URL xmlRpcServerUrl = new URL(serverPrefs.get(ServerConfiguration.ADMIN_URL,
                                                    serverPrefsDefaults.getProperty(ServerConfiguration.ADMIN_URL)));
      WebServer xmlRpcServer = new WebServer(xmlRpcServerUrl.getPort());
      // xmlRpcServer.setParanoid(true);
      xmlRpcServer.addHandler("$default", new AdminXmlRpcHandler());
      xmlRpcServer.start();
    } catch (Exception e) {
      System.out.println("ERROR: can't start administrative server interface (XML-RPC): " + e.getMessage());
      e.printStackTrace();
    }

    String webAppRoot = serverPrefs.get(ServerConfiguration.WEBAPP_ROOT,
                                        serverPrefsDefaults.getProperty((ServerConfiguration.WEBAPP_ROOT)));
    System.out.println(">> Applications: " + new File(webAppRoot).getAbsolutePath());
    // now, after loading all possible services we will look for applications and start them
    int errors = ApplicationLoader.loadApplications(serverPrefs.get(ServerConfiguration.WEBAPP_ROOT,
                                                                    serverPrefsDefaults.getProperty((ServerConfiguration.WEBAPP_ROOT))));
    if (errors == 0 && ApplicationLoader.getApplicationCount() == 0) {
      System.out.println("ATTENTION: Server is still unconfigured!");
      System.out.println("ATTENTION: Point your browser to the following address:");
      HttpListener listener = jettyServer.getListeners()[0];
      String host = listener.getHost();
      if (InetAddrPort.__0_0_0_0.equals(host)) {
        try {
          host = InetAddress.getLocalHost().getCanonicalHostName();
        } catch (UnknownHostException e) {
          host = System.getProperty("host", "localhost");
        }
      }
      String url = "http://" + host + ":" + listener.getPort() + "/install/" + serverPrefs.get(ServerConfiguration.ADMIN_PASS, "");
      System.out.println("ATTENTION: " + url);
      System.out.println("ATTENTION: To force setup of a specific host and port add '?expert=true'");
      System.out.println("ATTENTION: to the above URL.");

      // on an OS X System run the web browser to start configuration
      String system = System.getProperty("os.name");
      if (system.startsWith("Mac OS X")) {
        try {
          Runtime.getRuntime().exec("/usr/bin/open " + url);
        } catch (IOException e) {
          System.err.println("AppServer: unable to execute open web browser on MacOS X");
        }
      } else {
        try {
          Desktop.browse(new URL(url));
        } catch (Exception e) {
          System.err.println("AppServer: unable to open web browser on " + system);
        } catch (Error err) {
          System.err.println("AppServer: unable to open web browser on " + system);
        }
      }
    } else {
      System.out.println(ApplicationLoader.getApplicationCount() + " applications loaded and running (" + errors + " errors).");
    }
  }

  /**
   * Load server configuration from the preferences API. This method converts
   * old config files into the preferences as well.
   *
   * @return the preferences object
   */
  private static Preferences getServerPrefs() {
    serverPrefs = Preferences.userNodeForPackage(ServerConfiguration.class);
    int keyCount = 0;
    try {
      keyCount = serverPrefs.keys().length;
    } catch (BackingStoreException e) {
      System.err.println("AppServer: WARNING: no preferences backing store available! Using defaults.");
    }

    serverPrefsDefaults = new Properties();
    // load defaults
    try {
      serverPrefsDefaults.load(AppServer.class.getResourceAsStream("/conf/snipsnap.conf"));
    } catch (IOException e) {
      System.err.println("AppServer: Unable to read server defaults: " + e.getMessage());
      e.printStackTrace();
    }

    // check whether the node has any keys, empty means it is not yet initialized
    if (keyCount == 0) {
      System.err.println("AppServer: unconfigured server, loading preferences");

      // set application key pass (may be overidden by old config, see below)
      serverPrefs.put(ServerConfiguration.ADMIN_PASS,
                      Digest.getDigest("" + new Date()).substring(0, 5).toLowerCase());

      // put defaults into the properties
      Properties serverInfoProperties = new Properties();
      Iterator defaultsIt = serverPrefsDefaults.keySet().iterator();
      while (defaultsIt.hasNext()) {
        String key = (String) defaultsIt.next();
        System.err.println("AppServer: default preference: " + key + "=" + serverPrefsDefaults.get(key));
        serverInfoProperties.put(key, serverPrefsDefaults.get(key));
      }

      // load defaults
      try {
        serverInfoProperties.load(AppServer.class.getResourceAsStream("/conf/snipsnap.conf"));
      } catch (IOException e) {
        System.err.println("AppServer: Unable to read server defaults: " + e.getMessage());
        e.printStackTrace();
      }

      // now convert old config settings if necessary
      try {
        File propFile = new File("conf/server.conf");
        serverInfoProperties.load(new FileInputStream(propFile));
        System.err.println("AppServer: converted old server configuration, please remove '" + propFile + "'");
      } catch (IOException e) {
        // safely ignore file not found ...
      }

      // transfer defaults or existing old values into the preferences
      Iterator oldConfigIt = serverInfoProperties.keySet().iterator();
      while (oldConfigIt.hasNext()) {
        String key = (String) oldConfigIt.next();
        System.err.println("AppServer: converted old preference: " + key + "=" + serverInfoProperties.get(key));
        serverPrefs.put(key, (String) serverInfoProperties.get(key));
      }

      File webappRoot = new File(serverInfoProperties.getProperty(ServerConfiguration.WEBAPP_ROOT));
      System.err.println("webappRoot=" + webappRoot.getAbsolutePath());
      if (!webappRoot.exists() || !webappRoot.canWrite()) {
        webappRoot = new File(System.getProperty("user.home"), "applications");
      }
      serverPrefs.put(ServerConfiguration.WEBAPP_ROOT, webappRoot.getAbsolutePath());

      try {
        serverPrefs.flush();
      } catch (BackingStoreException e) {
        System.err.println("AppServer: unable to store server configuration: " + e.getMessage());
        e.printStackTrace();
      }

      File confDir = new File("conf");
      if (!confDir.exists() && !confDir.canWrite()) {
        confDir = webappRoot;
      }
      try {
        OutputStream configSave = new BufferedOutputStream(new FileOutputStream(new File(confDir, "snipsnap.xml")));
        serverPrefs.exportSubtree(configSave);
      } catch (Exception e) {
        System.err.println("AppServer: unable to store server configuation backup: " + e.getMessage());
        e.printStackTrace();
      }
    }

    String jarVersion = serverPrefsDefaults.getProperty(ServerConfiguration.VERSION);
    String version = serverPrefs.get(ServerConfiguration.VERSION, null);
    if (null == version || !(jarVersion != null && jarVersion.equals(version))) {
      serverPrefs.put(ServerConfiguration.VERSION, jarVersion);
      try {
        serverPrefs.flush();
      } catch (BackingStoreException e) {
        System.err.println("AppServer: unable to store server configuration " + e.getMessage());
        e.printStackTrace();
      }
    }

    return serverPrefs;
  }

  private static void printCopyright() {
    System.out.println("SnipSnap " + System.getProperty(ServerConfiguration.VERSION));

    // output version and copyright information
    InputStream in = null;
    try {
      in = AppServer.class.getResourceAsStream("/conf/copyright.txt");
      BufferedReader copyrightReader = new BufferedReader(new InputStreamReader(in));
      String line = null;
      while ((line = copyrightReader.readLine()) != null) {
        System.out.println(line);
      }
    } catch (IOException e) {
      // ignore io exception here ...
    } finally {
      try { in.close(); } catch (Throwable ignore) { };
    }
  }

  /**
   * Parse argument from command line and exit in case of erroneous options.
   * The server will immedialy exit if there is a problem and display a usage message
   *
   * @param args the command line arguments
   * @return the possibly changed configuration properties
   */
  private static Preferences parseArguments(String args[]) {
    for (int i = 0; i < args.length; i++) {
      if ("-help".equals(args[i])) {
        usage("");
        System.exit(0);
      }

      // the applications root directory
      if ("-root".equals(args[i])) {
        if (args.length >= i + 1 && !args[i + 1].startsWith("-")) {
          serverPrefs.put(ServerConfiguration.WEBAPP_ROOT, args[++i]);
        } else {
          usage("an argument is required for -root");
        }
      } else if ("-url".equals(args[i])) {
        if (args.length >= i + 1 && !args[i + 1].startsWith("-")) {
          serverPrefs.put(ServerConfiguration.ADMIN_URL, args[++i]);
        } else {
          usage("an argument is required for -url");
        }
      } else if ("-killconfig".equals(args[i])) {
        try {
          serverPrefs.removeNode();
          serverPrefs.flush();
        } catch (BackingStoreException e) {
          System.out.println("Error: cannot remove configuration ...");
          e.printStackTrace();
        }
        System.exit(0);
      } else if ("-showconfig".equals(args[i])) {
        try {
          serverPrefs.sync();
          serverPrefs.exportSubtree(System.out);
        } catch (Exception e) {
          System.out.println("Error: cannot display configuration ...");
          e.printStackTrace();
        }
        System.exit(0);
      }
    }
    return serverPrefs;
  }

  /**
   * Get a resource from file if the file exists, else just return the jar resource.
   *
   * @param jarResource  the jar file resource (fallback)
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
   *
   * @param message an additional informational text
   */
  private static void usage(String message) {
    System.out.println(message);
    System.out.println("usage: " + AppServer.class.getName() + " [-root <dir>] [-url <url>]");
    System.out.println("  -root       directory, where to find the applications for this server");
    System.out.println("  -url        URL, admin server URL (http://host:port/)");
    System.out.println("  -killconfig remove server configuration from system");
    System.out.println("  -showconfig show server configuration, dump it");
    System.out.println("  -help       this help text");
    System.exit(0);
  }
}
