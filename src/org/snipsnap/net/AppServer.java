package com.neotis.net;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.ServletHttpContext;
import org.mortbay.util.MultiException;

import java.io.IOException;

public class AppServer {

  private static Server jettyServer;

  public static void main(String args[]) {
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

        ServletHttpContext context = (ServletHttpContext) jettyServer.getContext(null, "/");
        context.addServlet("SnipServlet", "/", "com.neotis.net.SnipServlet");

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
