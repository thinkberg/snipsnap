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
import org.snipsnap.container.Components;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManagerFactory;
import org.snipsnap.util.ApplicationAwareMap;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * The application object contains information about current users and other
 * session specific information.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class Application {
  public final static String OID = "_applicationOid";
  public final static String URL = "_url";

  private static ApplicationAwareMap currentUsers =
    new ApplicationAwareMap(HashMap.class, HashMap.class);

  private static ThreadLocal instance = new ThreadLocal() {
    protected synchronized Object initialValue() {
//      System.out.println("creating new initial application value");
      return new Application();
    }
  };

  // TODO make this an application-aware map to get old functionality
  private Map objectStore = new HashMap();
  private User user;
  private Configuration config;
  private List log = new ArrayList();
  // TODO use private NotificationService notification;
  private Map params;

  public static Application get() {
    return (Application) instance.get();
  }

  public static void set(Application application) {
    instance.set(application);
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

  public long start() {
    return System.currentTimeMillis();
  }

  public void stop(long start, String output) {
    Logger.log(Logger.PERF, output + " - " + (System.currentTimeMillis() - start));
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

  public static synchronized void addCurrentUser(User user, HttpSession session) {
    currentUsers.getMap().put(session, user);
  }

  public static List getCurrentUsers() {
    List users = new ArrayList();
    Iterator iterator = currentUsers.getMap().values().iterator();
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
    Iterator iterator = currentUsers.getMap().values().iterator();
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
    Iterator iterator = currentUsers.getMap().values().iterator();
    while (iterator.hasNext()) {
      User user = (User) iterator.next();
      if (user.isGuest() && !user.isNonUser()) {
        count++;
      }
    }
    return count;
  }

  public static synchronized void removeCurrentUser(HttpSession session) {
    if (null == currentUsers || null == session) {
      return;
    }

    Map currentUsersMap = currentUsers.getMap();
    if(null == currentUsersMap) {
      currentUsersMap = currentUsers.findMap(session);
    }

    if (null != currentUsersMap && currentUsersMap.containsKey(session)) {
      User user = (User) currentUsersMap.get(session);
      AuthenticationService service = (AuthenticationService) Components.getComponent(AuthenticationService.class);

      if (service.isAuthenticated(user)) {
        Logger.debug("Removing authenticated user from session: " + user);
        user.setLastLogout(user.getLastAccess());
        // we ensure we remove the correct user by setting the OID from the user object
        Application.get().storeObject(Application.OID, user.getApplication());
        UserManagerFactory.getInstance().systemStore(user);
      } else {
        Logger.debug("Removing unauthenticated user from session: " + user);
      }
      currentUsersMap.remove(session);
    } else {
      Logger.warn("Unable to remove current user from session '"+session+"'");
    }
  }

  public void storeObject(String key, Object value) {
    objectStore.put(key, value);
  }

  public Object getObject(String key) {
    return objectStore.get(key);
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
