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
package org.snipsnap.config;

import org.snipsnap.app.Application;
import org.snipsnap.snip.HomePage;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.XMLSnipImport;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.storage.JDBCSnipStorage;
import org.snipsnap.snip.storage.JDBCUserStorage;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.container.Components;

import java.io.IOException;
import java.io.InputStream;

/**
 * Create initial database and example snips.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class CreateDB {

  public static void createDB(Configuration config) {
    // Make a connection with the database.  This will create the database
    // and log into the newly created database.

    Application.get().setConfiguration(config);
    JDBCSnipStorage.createStorage();
    JDBCUserStorage.createStorage();
  }

  public static void createAdmin(Configuration config) {
    System.out.println("CreateDB: Creating Admin Home Page");
    SnipSpaceFactory.removeInstance();
    UserManager.removeInstance();

    User admin = UserManager.getInstance().create(config.getAdminLogin(),
                                                  config.getAdminPassword(),
                                                  config.getAdminPassword());
    admin.getRoles().add(Roles.EDITOR);
    UserManager.getInstance().store(admin);

    Application app = Application.get();
    app.setUser(admin);

    System.out.println("Creating admin homepage.");
    HomePage.create(config.getAdminLogin());
  }

  public static void insertData(Configuration config, InputStream data) {
    System.out.println("CreateDB: Inserting Data");
    SnipSpaceFactory.removeInstance();
    UserManager.removeInstance();

    User admin = ((AuthenticationService) Components.getComponent(AuthenticationService.class)).authenticate(config.getAdminLogin(),
                                                        config.getAdminPassword());
    Application app = Application.get();
    app.setUser(admin);

    System.out.println("Importing default snips.");
    try {
      XMLSnipImport.load(data, XMLSnipImport.OVERWRITE | XMLSnipImport.IMPORT_USERS | XMLSnipImport.IMPORT_SNIPS);
    } catch (IOException e) {
      System.out.println("CreateDB: import failed!");
    }


    System.out.println("CreateDB: Complete");
  }

  public static void postFirst(Configuration config) {
    System.out.println("CreateDB: Posting first entry");
    SnipSpaceFactory.removeInstance();
    UserManager.removeInstance();

    User admin = ((AuthenticationService) Components.getComponent(AuthenticationService.class)).authenticate(config.getAdminLogin(),
                                                        config.getAdminPassword());
    Application app = Application.get();
    app.setUser(admin);

    String ping = config.get(Configuration.APP_PERM_WEBLOGSPING);
    String notify = config.get(Configuration.APP_PERM_NOTIFICATION);

    config.set(Configuration.APP_PERM_WEBLOGSPING, "deny");
    config.set(Configuration.APP_PERM_NOTIFICATION, "deny");

    ((SnipSpace) Components.getComponent(SnipSpace.class)).getBlog().post("Welcome to [SnipSnap]." +
                                                  " You can now login and add/edit your first post. There is a __post blog__ link in the menu bar. For help with formatting your post" +
                                                  " take a look at [snipsnap-help]. To create a link to a page on your site surround a word with \\[ and \\]." +
                                                  " Putting \\_\\_ around a phrase makes it __bold__ and putting \\~\\~ around it makes the" +
                                                  " phrase ~~italics~~. You can create links to the internet by just writing the url like " +
                                                  " http://snipsnap.org or by using \\{link:Name|url\\}. So \\{link:SnipSnap|\\http://snipsnap.org\\} produces " +
                                                  " {link:SnipSnap|http://snipsnap.org}. Have fun.\n\n" +
                                                  " Pinging weblogs.com may be turned on. The {link:FAQ|http://snipsnap.org/space/FAQ}" +
                                                  " explains how to turn this on or off.",
                                                  "Welcome to SnipSnap");

    config.set(Configuration.APP_PERM_WEBLOGSPING, ping);
    config.set(Configuration.APP_PERM_NOTIFICATION, notify);
  }
}
