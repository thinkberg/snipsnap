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
package com.neotis.server;

import com.neotis.config.Configuration;
import org.mortbay.http.HttpListener;
import org.mortbay.http.SocketListener;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.InetAddrPort;
import org.mortbay.util.MultiException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;

/**
 * Application Server
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AppServer {

  public final static int ADMIN_PORT = 9765;
  private static Server jettyServer;
  private static WebApplicationContext adminContext;

  public static void main(String args[]) {
    System.out.println("SnipSnap v0.1-alpha");
    System.out.println("Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel. "
                       + "All Rights Reserved.");
    System.out.println("See License Agreement for terms and conditions of use.");
    if (args.length > 1) {
      if ("-admin".equals(args[0])) {
        try {
          Socket s = new Socket(InetAddress.getLocalHost(), ADMIN_PORT);
          BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
          writer.write(args[1]);
          writer.newLine();
          writer.close();
          s.close();
        } catch (IOException e) {
          System.err.println("AppServer: cannot send administrative command");
          System.exit(-1);
        }
        System.exit(0);
      }
    } else {
      Configuration config = new Configuration("./conf/local.conf");
      initServer(config);
      checkConfig(config);
    }
  }

  /**
   * Initialize Server and load administrative web application.
   */
  private static void initServer(Configuration config) {
    try {
      jettyServer = new Server("./conf/server.conf");
      jettyServer.start();
    } catch (IOException e) {
      System.err.println("AppServer: configuration not found: " + e);
    } catch (MultiException e) {
      Iterator exceptions = e.getExceptions().iterator();
      while (exceptions.hasNext()) {
        Exception ex = (Exception) exceptions.next();
        if (ex instanceof BindException) {
          System.out.println("ERROR: can't start server, address already in use: " + ex.getMessage());
        }
      }
    }

    try {
      adminContext = addApplication("/admin", "./lib/snipsnap-admin.war");
    } catch (Exception e) {
      System.err.println("AppServer: unable to load servlet class: " + e);
    }

    new Thread(new Runnable() {
      public void run() {
        ServerSocket ss = null;
        try {
          ss = new ServerSocket(ADMIN_PORT, 1, InetAddress.getLocalHost());
        } catch (IOException e) {
          System.err.println("AppServer: unable to bind administration socket on port ");
        }
        while(ss != null) {
          try {
            Socket s = ss.accept();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            if (s.getInetAddress().equals(InetAddress.getLocalHost())) {
              String command = reader.readLine();
              if ("shutdown".equals(command)) {
                Shutdown.shutdown();
              }
            } else {
              writer.write("I cut you out, don't try that again! Snip Snap!");
              writer.newLine();
              writer.flush();
            }
            reader.close();
            writer.close();
            s.close();
          } catch (IOException e) {
            System.err.println("AppServer: exception while handling administrative command on socket");
            e.printStackTrace();
          }
        } while (true);
      }
    }).start();
  }

  private static void checkConfig(Configuration config) {
    if (config.isConfigured()) {
      try {
        jettyServer.addListener(new SocketListener(new InetAddrPort(config.getPort())));
      } catch (IllegalArgumentException e) {
        System.err.println("AppServer: illegal number for port: " + config.getPort());
      } catch (IOException e) {
        System.err.println("AppServer: unable to configure server on port " + config.getPort());
        System.err.println("AppServer: error caused by: " + e);
      }
      try {
        WebApplicationContext context = addApplication(config.getContextPath(), "./app" + config.getContextPath());
        System.out.println("INFO: starting application " + context);
        context.start();
      } catch (Exception e) {
        System.out.println("WARNING: unable to start your application: " + e);
        e.printStackTrace(System.err);
      }
    } else {
      System.out.println("INFO: Server is still unconfigured!");
      System.out.println("INFO: Point your browser to the following address:");
      HttpListener listener = jettyServer.getListeners()[0];
      String host = listener.getHost();
      if (InetAddrPort.__0_0_0_0.equals(host)) {
        host = "localhost";
      }
      System.out.println("INFO: http://" + host + ":" + listener.getPort() + "/admin");
    }
  }

  private static WebApplicationContext addApplication(String root, String app) {
    WebApplicationContext context = null;
    try {
      context = jettyServer.addWebApplication(root, app);
      context.start();
    } catch (Exception e) {
      System.err.println("AppServer: default configuration not found: " + e);
    }
    return context;
  }

  public static Server getServer() {
    return jettyServer;
  }
}
