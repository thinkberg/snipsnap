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
package org.snipsnap.app;

import org.snipsnap.config.AppConfiguration;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.util.log.Logger;
import org.snipsnap.snip.Snip;
import org.snipsnap.notification.NotificationService;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

/**
 * The application object contains information about current users and other
 * session specific information.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class Application {
  private static Map currentUsers;

  private User user;
  private AppConfiguration config;
  private Map parameters;
  private List log = new ArrayList();
  private NotificationService notification;

  private static ThreadLocal instance = new ThreadLocal() {
    protected synchronized Object initValue() {
      System.out.println("Reading init value.");
      return new Application();
    }
  };

  public Application() {
    notification = new NotificationService();
  }

  public void clearLog() {
    log = new ArrayList();
  }

  public List getLog() {
    return log;
  }

  public void log(String output) {
    log.add(output);
  }

  public void notify(int type, Snip snip) {
    System.err.println("Application - notify() "+type);
    notification.notify(type, snip);
  }

  public long start() {
    return System.currentTimeMillis();
  }

  public void stop(long start, String output) {
    Logger.log(Logger.PERF, output + " - " + (System.currentTimeMillis() - start));
  }

  public static Application get() {
    Application app = (Application) instance.get();
    // Workaround, because initValue doesn't work
    if (null == app) {
      app = new Application();
      instance.set(app);
    }
    return app;
  }

  public static void set(Application application) {
    instance.set(application);
  }

  public static Application getInstance(HttpSession session) {
    if (session != null) {
      Application application = (Application) session.getAttribute("app");
      if (null == application) {
        application = new Application();
      }
      instance.set(application);
      return application;
    }
    return null;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Map getParameters() {
    return parameters;
  }

  public void setParameters(Map parameters) {
    this.parameters = parameters;
  }

  public void setUser(User user, HttpSession session) {
    if (this.user == user) {
      return;
    }

    if (this.user != null) {
      Application.removeCurrentUser(session);
    }
    setUser(user);
    Application.addCurrentUser(user, session);
    return;
  }

  public static void addCurrentUser(User user, HttpSession session) {
    if (null == currentUsers) {
      currentUsers = new HashMap();
    }
    currentUsers.put(session, user);
  }

  public static List getCurrentUsers() {
    List users = new ArrayList();
    Iterator iterator = currentUsers.values().iterator();
    while (iterator.hasNext()) {
      User user  = (User) iterator.next();
      if (! (User.UNKNOWN.equals(user.getName()) || users.contains(user))) {
        users.add(user);
      }
    }
    return users;
  }

  public static int getGuestCount() {
    int count = 0;
    Iterator iterator = currentUsers.values().iterator();
    while (iterator.hasNext()) {
      User user = (User) iterator.next();
      if (User.UNKNOWN.equals(user.getLogin())) {
        count++;
      }
    }
    return count;
  }

  public static void removeCurrentUser(HttpSession session) {
    if (null == currentUsers) return;

    if (currentUsers.containsKey(session)) {
      UserManager um = UserManager.getInstance();
      User user = (User) currentUsers.get(session);
      if (um.isAuthenticated(user)) {
        System.err.println("Removing user: "+user.getLogin());
        user.setLastLogout(user.getLastAccess());
        UserManager.getInstance().systemStore(user);
      }
      currentUsers.remove(session);
    }
  }

  public void setConfiguration(AppConfiguration config) {
    this.config = config;
  }

  public AppConfiguration getConfiguration() {
    if (config == null) {
      config = AppConfiguration.getInstance();
    }
    return config;
  }
}
