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
package com.neotis.admin;

import com.neotis.config.Configuration;
import com.neotis.snip.SnipLink;
import org.mortbay.http.HttpServer;
import org.mortbay.util.Code;
import org.mortbay.util.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Application configuration servlet.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Shutdown extends HttpServlet {

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    HttpSession session = request.getSession();
    if (session != null) {
      String user = request.getParameter("login");
      String pass = request.getParameter("password");

      Configuration config = new Configuration("./conf/local.conf");

      // don't do anything before user name and password are checked
      if (config.isConfigured() &&
        config.getUserName().equals(user) &&
        config.getPassword().equals(pass)) {
        // shut down server ...
        shutdown();
        response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Server has been shut down.");
        return;
      }
    }

    response.sendRedirect(SnipLink.absoluteLink(request, "/"));
  }

  /**
   * Shut down complete server ...
   */
  private void shutdown() {
    new Thread(new Runnable() {
      public void run() {
        try {
          Thread.sleep(1000);
        } catch (Exception e) {
          Code.ignore(e);
        }
        Log.event("Application: stopping all servers");
        System.out.println("INFO: Stopping all servers ...");
        Iterator s = HttpServer.getHttpServers().iterator();
        while (s.hasNext()) {
          HttpServer server = (HttpServer) s.next();
          try {
            System.out.println("INFO: stopping " + server);
            server.stop();
          } catch (Exception e) {
            Code.ignore(e);
          }
        }
        System.out.println("Shutting down Java VM (it ends here)!");
        Log.event("Application: exiting Java VM");
        System.exit(1);
      }
    }).start();
  }

  private void writeMessage(PrintWriter out, String message) {
    out.println(message);
    out.flush();
  }

}

