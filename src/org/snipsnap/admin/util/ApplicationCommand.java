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
package com.neotis.admin.util;

import org.mortbay.http.HttpContext;
import org.mortbay.http.HttpServer;
import org.mortbay.jetty.Server;

import java.io.IOException;
import java.util.Iterator;

/**
 * Start, stop or remove a web application from the server.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class ApplicationCommand {
  public final static String CMD_APPLICATION_START = "start";
  public final static String CMD_APPLICATION_STOP = "stop";
  public final static String CMD_APPLICATION_REMOVE = "remove";
  public final static String CMD_APPLICATION_ADD = "add";

  public static void execute(String srv, String ctx, String command) {
    System.err.println("srv=" + srv + " ctx=" + ctx);
    Iterator it = Server.getHttpServers().iterator();
    while (it.hasNext()) {
      HttpServer server = (HttpServer) it.next();
      if (server.toString().equals(srv)) {
        if (CMD_APPLICATION_ADD.equals(command)) {
          try {
            ((Server) server).addWebApplication(ctx, "./app" + ctx);
            try {
              Thread.sleep(5000);
            } catch (InterruptedException e) {
              System.err.println("Updater: interrupted while waiting for application to sync ..." + e);
            }
          } catch (IOException e) {
            System.err.println("Application: unable to add context: " + srv + ":" + ctx);
          }
          return;
        } else {
          HttpContext contexts[] = server.getContexts();
          for (int c = 0; c < contexts.length; c++) {
            HttpContext context = contexts[c];
            if (context.getContextPath().equals(ctx)) {
              try {
                if (CMD_APPLICATION_START.equals(command)) {
                  context.start();
                } else if (CMD_APPLICATION_STOP.equals(command)) {
                  context.stop();
                } else if (CMD_APPLICATION_REMOVE.equals(command)) {
                  server.removeContext(context);
                  try {
                    Thread.sleep(5000);
                  } catch (InterruptedException e) {
                    System.err.println("Application: interrupted while waiting for application to sync ..." + e);
                  }
                } else {
                  System.err.println("Application: unknown or illegal command: " + command);
                }
              } catch (Exception e) {
                System.err.println("Application: unable to " + command + " server=" + server + ", context=" + context);
              }
              return;
            }
          }

        }
      }
    }
  }
}
