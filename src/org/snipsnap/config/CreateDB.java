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
import org.snipsnap.snip.storage.JDBCSnipStorage;
import org.snipsnap.snip.storage.JDBCUserStorage;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import java.io.IOException;
import java.io.InputStream;

/**
 * Create initial database and example snips.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class CreateDB {

  public static void main(String[] args) {
    System.err.println("NOT IMPLEMENTED.");
//    createDB("funzel", "funzel", "stephan@mud.de");
//    insertData("funzel", "funzel", "stephan@mud.de");
  }

  public static void createDB(AppConfiguration config) {
    // Make a connection with the database.  This will create the database
    // and log into the newly created database.

    JDBCSnipStorage.createStorage();
    JDBCUserStorage.createStorage();
  }

  public static void createAdmin(AppConfiguration config) {
    System.out.println("CreateDB: Creating Admin Home Page");
    SnipSpaceFactory.removeInstance();
    UserManager.removeInstance();

    User admin = UserManager.getInstance().create(config.getAdminLogin(), config.getAdminPassword(), config.getAdminEmail());
    admin.getRoles().add(Roles.EDITOR);
    UserManager.getInstance().store(admin);

    Application app = Application.get();
    app.setUser(admin);

    System.out.println("Creating admin homepage.");
    HomePage.create(config.getAdminLogin());
  }

  public static void insertData(AppConfiguration config, InputStream data) {
    System.out.println("CreateDB: Inserting Data");
    SnipSpaceFactory.removeInstance();
    UserManager.removeInstance();

    User admin = UserManager.getInstance().authenticate(config.getAdminLogin(), config.getAdminPassword());
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

  public static void postFirst(AppConfiguration config) {
    System.out.println("CreateDB: Posting first entry");
    SnipSpaceFactory.removeInstance();
    UserManager.removeInstance();

    User admin = UserManager.getInstance().authenticate(config.getAdminLogin(), config.getAdminPassword());
    Application app = Application.get();
    app.setUser(admin);

    String ping = config.getProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_WEBLOGS_PING);
    String notify = config.getProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_NOTIFICATION);

    config.setProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_WEBLOGS_PING, "deny");
    config.setProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_NOTIFICATION, "deny");

    SnipSpaceFactory.getInstance().post("Welcome to [SnipSnap]." +
           " You can now login and add/edit your first post. There is a __post blog__ link in the menu bar. For help with formatting your post" +
           " take a look at [snipsnap-help]. To create a link to a page on your site surround a word with \\[ and \\]." +
           " Putting \\_\\_ around a phrase makes it __bold__ and putting \\~\\~ around it makes the" +
           " phrase ~~italics~~. You can create links to the internet by just writing the url like "+
           " http://snipsnap.org or by using \\{link:Name|url\\}. So \\{link:SnipSnap|\\http://snipsnap.org\\} produces " +
           " {link:SnipSnap|http://snipsnap.org}. Have fun.\n\n" +
           " Pinging weblogs.com may be turned on. The {link:FAQ|http://snipsnap.org/space/FAQ}" +
           " explains how to turn this on or off.",
           "Welcome to SnipSnap");

    config.setProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_WEBLOGS_PING, ping);
    config.setProperty(AppConfiguration.APP_PERM + "." + AppConfiguration.PERM_NOTIFICATION, notify);
  }
}
