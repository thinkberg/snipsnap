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

import org.snipsnap.config.Configuration;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Server for administrative commands that are sent via local host.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AdminServer implements Runnable {

  public final static List COMMANDS = Arrays.asList(
      new String[]{
        "shutdown",
        "start <appname>",
        "stop <appname>",
        "reload <appname>"
      });

  /**
   * Execute an administrative command by sending it to the server on the specified port.
   * @param port the admin port to send to
   * @param command the actual command
   * @return true if the command was successful
   */
  public static boolean execute(int port, String command, String args) {
    try {
      Socket s = new Socket(InetAddress.getLocalHost(), port);
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
      BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
      System.out.println("Executing command: "+ command + " " + (args != null ? args : ""));
      writer.write(command + " " + args);
      writer.newLine();
      writer.flush();
      String output = null;
      while ((output = reader.readLine()) != null) {
        System.out.println(output);
      }
      reader.close();
      writer.close();
      s.close();
    } catch (IOException e) {
      System.out.println("AdminServer: cannot execute administrative command");
      e.printStackTrace(System.out);
      return false;
    }
    return true;
  }

  private Properties config = null;
  private ServerSocket serverSocket = null;
  private Thread serverThread = null;

  /**
   * Create a new administrative server interface.
   * @param config the configuration
   * @throws IOException
   * @throws UnknownHostException
   */
  public AdminServer(Properties config) throws NumberFormatException, IOException, UnknownHostException {
    int port = Integer.parseInt(config.getProperty(Configuration.ADMIN_PORT).trim());
    this.config = config;
    serverSocket = new ServerSocket(port, 1, InetAddress.getLocalHost());
    serverThread = new Thread(this);
    serverThread.start();
  }

  /**
   * Wait for connections and handle commands.
   */
  public void run() {
    while (serverThread != null && serverSocket != null) {
      try {
        Socket s = serverSocket.accept();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        if (s.getInetAddress().equals(InetAddress.getLocalHost())) {
          String line = reader.readLine();
          String command = null, args = null;
          int idx = line.indexOf(' ');
          if (idx != -1) {
            command = line.substring(0, idx);
            args = line.substring(idx + 1);
          }
          System.out.println("AdminServer: got command: '" + command + "' '" + args + "'");
          if ("shutdown".equals(command)) {
            System.exit(0);
          } else if ("start".equals(command) && args != null) {
            try {
              ApplicationLoader.loadApplication(config.getProperty(Configuration.WEBAPP_ROOT), args);
            } catch (Exception e) {
              e.printStackTrace(new PrintWriter(writer));
              writer.newLine();
            }
          } else if ("stop".equals(command) && args != null) {
            try {
              ApplicationLoader.unloadApplication(config.getProperty(Configuration.WEBAPP_ROOT), args);
            } catch (Exception e) {
              e.printStackTrace(new PrintWriter(writer));
              writer.newLine();
            }
          } else if ("reload".equals(command) && args != null) {
            try {
              ApplicationLoader.reloadApplication(config.getProperty(Configuration.WEBAPP_ROOT), args);
            } catch (Exception e) {
              e.printStackTrace(new PrintWriter(writer));
              writer.newLine();
            }
          } else {
            writer.write("Unknown command '"+command+"', aborting ...");
          }
        } else {
          writer.write("I cut you out, don't try that again! Snip Snap!");
          writer.newLine();
        }
        writer.flush();
        reader.close();
        writer.close();
        s.close();
      } catch (IOException e) {
        System.err.println("AdminServer: exception while handling administrative command on socket: " + e);
        e.printStackTrace();
      }
    }
  }
}
