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
package org.snipsnap.util;

import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.server.Shutdown;
import org.snipsnap.snip.XMLSnipImport;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.UserManagerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class DBImport {

  public static void main(String args[]) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        Shutdown.shutdown();
      }
    });

    if (args.length < 3) {
      System.out.println("usage: DBImport <application> <user> <xml-file> [overwrite]");
      System.exit(-1);
    }

    ServerConfiguration serverConfig = null;
    try {
      serverConfig = new ServerConfiguration("./conf/server.conf");
    } catch (IOException e) {
      System.out.println("Unable to load server config: " + e);
      System.exit(-1);
    }

    Application app = Application.get();
    Configuration config = null;
    try {
      config = ConfigurationProxy.newInstance();
      config.load(new FileInputStream(serverConfig.getProperty(ServerConfiguration.WEBAPP_ROOT) + "/" + args[0] + "/WEB-INF/application.conf"));
    } catch (IOException e) {
      System.out.println("Unable to load application config: " + e);
      System.exit(-1);
    }
    app.setConfiguration(config);

    UserManager um = UserManagerFactory.getInstance();
    User user = um.load(args[1]);
    app.setUser(user);

    if (user == null) {
      System.err.println("User not found: " + args[1]);
      System.exit(-1);
    }

    int importMask = XMLSnipImport.IMPORT_SNIPS + XMLSnipImport.IMPORT_USERS;
    importMask += (args.length == 4 && "overwrite".equals(args[3]) ? XMLSnipImport.OVERWRITE : 0);

    System.err.println("Disabling weblogs ping and jabber notification ...");
    config.set(Configuration.APP_PERM_WEBLOGSPING, "deny");
    config.set(Configuration.APP_PERM_NOTIFICATION, "deny");

    try {
      XMLSnipImport.load(new FileInputStream(args[2]), importMask);
    } catch (IOException e) {
      System.out.println("Unable to load import file: "+e);
      System.exit(-1);
    }
    System.exit(0);
  }


}
