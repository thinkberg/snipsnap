package com.neotis.net;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.WebApplicationContext;
import org.mortbay.util.MultiException;

import java.io.IOException;

/**
 * Application Server
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AppServer {

  private static Server jettyServer;

  public static void main(String args[]) {
    System.out.println("SnipSnap $Revision$");
    System.out.println("Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel."
	              +"All Rights Reserved.");
    System.out.println("See License Agreement for terms and conditions of use.");
    startServer();
  }

  private static void startServer() {
    if (jettyServer != null) {
      // gracefully stop server
      try {
        jettyServer.stop(true);
      } catch (InterruptedException e) {
        System.err.println("AppServer: unable to stop jetty server gracefully");
      }
    } else {
      try {
        jettyServer = new Server("./conf/server.conf");
        WebApplicationContext context = jettyServer.addWebApplication("/", "./app");
        jettyServer.start();
      } catch (MultiException e) {
        System.err.println("AppServer: unable to start server: " + e);
        System.exit(-1);
      } catch (IOException e) {
        System.err.println("AppServer: configuration not found: " + e);
      } catch (Exception e) {
        System.err.println("AppServer: unable to load servlet class: " + e);
      }
    }
  }
}
