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

import org.snipsnap.config.ServerConfiguration;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class AdminClient {

  public static void main(String args[]) {
    Properties config = new Properties();
    try {
      config.load(AdminClient.class.getResourceAsStream("/conf/snipsnap.conf"));
    } catch (Exception e) {
      System.err.println("AdminClient: unable to load config defaults: " + e);
    }
    try {
      config.load(new FileInputStream("conf/server.conf"));
    } catch (IOException e) {
      System.err.println("AdminClient: unable to load conf/server.conf: "+e);
    }

    List commands = parseOptions(args, config);
    if(commands.size() > 0) {
      execute(commands, config);
      System.exit(0);
    }
    System.exit(1);
  }

  private static void execute(List commands, Properties config) {
    try {
      AdminXmlRpcClient client = new AdminXmlRpcClient(config.getProperty(ServerConfiguration.ADMIN_HOST),
                                                       Integer.parseInt(config.getProperty(ServerConfiguration.ADMIN_PORT)),
                                                       config.getProperty(ServerConfiguration.ADMIN_PASS));
      String method = (String)commands.get(0);
      Vector args = new Vector();
      for(int i = 1; i < commands.size(); i++) {
        args.addElement(commands.get(i));
      }
      client.execute(method, args);
    } catch (Exception e) {
      System.err.println("AdminClient: error executing command: " + e);
      e.printStackTrace();
    }
  }


  private static List parseOptions(String args[], Properties config) {
    int argNo;
    List commands = new ArrayList();
    for(argNo = 0; argNo < args.length; argNo++) {
      if("-host".equals(args[argNo]) && args.length > argNo + 1) {
        config.setProperty("admin.host", args[argNo + 1]);
      } else if("-port".equals(args[argNo]) && args.length > argNo + 1) {
        config.setProperty("admin.port", args[argNo + 1]);
      } else if("-config".equals(args[argNo]) && args.length > argNo + 1) {
        try {
          config.load(new FileInputStream(args[argNo + 1]));
        } catch (IOException e) {
          System.err.println("AdminClient: unable to load configuration: "+e);
        }
      } else {
        commands.add(args[argNo]);
      }
    }
    return commands;
  }
}
