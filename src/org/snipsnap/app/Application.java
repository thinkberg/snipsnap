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

import org.radeox.util.logging.Logger;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.notification.NotificationService;
import org.snipsnap.snip.Snip;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * The application object contains information about current users and other
 * session specific information.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class Application {
  private static Map currentUsers = new HashMap();
  private static Map globalStore = new HashMap();

  private User user;
  private Configuration config;
  private List log = new ArrayList();
  private NotificationService notification;
  private Map params;

  private static ThreadLocal instance = new ThreadLocal() {
    protected synchronized Object initValue() {
      Logger.debug("Reading init value.");
      return new Application();
    }
  };

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
    //Logger.debug("Application - notify() "+type);
    if (notification == null &&
        config != null && config.allow(Configuration.APP_PERM_NOTIFICATION)) {
      notification = NotificationService.getInstance();
    }
    if (notification != null) {
      notification.notify(type, snip);
    }
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
    return params;
  }

  public void setParameters(Map parameters) {
    this.params = parameters;
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
    currentUsers.put(session, user);
  }

  public static List getCurrentUsers() {
    List users = new ArrayList();
    Iterator iterator = currentUsers.values().iterator();
    while (iterator.hasNext()) {
      User user = (User) iterator.next();
      if (!(user.isGuest() || user.isNonUser() || users.contains(user))) {
        users.add(user);
      }
    }
    return users;
  }

  public static List getCurrentNonUsers() {
    List users = new ArrayList();
    Iterator iterator = currentUsers.values().iterator();
    while (iterator.hasNext()) {
      User user = (User) iterator.next();
      if (user.isNonUser() && !users.contains(user) && !"IGNORE".equals(user.getEmail())) {
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
      if (user.isGuest() && !user.isNonUser()) {
        count++;
      }
    }
    return count;
  }

  public static void removeCurrentUser(HttpSession session) {
    if (null == currentUsers) { return; }

    if (currentUsers.containsKey(session)) {
      UserManager um = UserManager.getInstance();
      User user = (User) currentUsers.get(session);
      if (um.isAuthenticated(user)) {
        Logger.debug("Removing user from session: " + user.getLogin());
        user.setLastLogout(user.getLastAccess());
        UserManager.getInstance().systemStore(user);
      }
      currentUsers.remove(session);
    }
  }

  // Global memory
  public void storeObject(String key, Object value) {
    Application.globalStore.put(key, value);
  }

  public Object getObject(String key) {
    return Application.globalStore.get(key);
  }

  public void setConfiguration(Configuration config) {
    this.config = config;
  }

  public Configuration getConfiguration() {
    if (null == config) {
      config = ConfigurationProxy.getInstance();
    }
    return config;
  }
}
