/*            Compent
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
package org.snipsnap.user;

import org.snipsnap.container.Components;
import org.snipsnap.snip.storage.UserStorage;
import org.snipsnap.util.ApplicationAwareMap;
import org.snipsnap.util.ApplicationAwareIntegerMap;
import org.snipsnap.jdbc.IntHolder;

import java.sql.Timestamp;
import java.util.*;

/**
 * User manager handles all register, creation and authentication of users.
 * Default user manager uses a UserStorage component for storage
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class DefaultUserManager implements UserManager {
  // Number of users
  private ApplicationAwareIntegerMap countCache;
  private ApplicationAwareMap missingCache;

  // List of users that should be stored later
  private List delayed;
  // Storage from where to load users
  private UserStorage storage;
  public static final int MILLISECS_PER_MINUTE = 60 * 1000;

  public DefaultUserManager(UserStorage storage) {
    this.storage = storage;

    delayed = new LinkedList();

    countCache = new ApplicationAwareIntegerMap(HashMap.class);
    missingCache = new ApplicationAwareMap(HashMap.class, ArrayList.class);

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      public void run() {
        synchronized (delayed) {
          ListIterator iterator = delayed.listIterator();
          while (iterator.hasNext()) {
            User user = (User) iterator.next();
            systemStore(user);
            iterator.remove();
          }
        }
      }
      // execute after 5 minutes and then
      // every 5 minutes
    }, 5 * MILLISECS_PER_MINUTE, 5 * MILLISECS_PER_MINUTE);
  }

  /**
   * Get all users in the system
   *
   * @return
   */
  public List getAll() {
    return storage.storageAll();
  }

  /**
   * Get the number of users in the system
   * @return
   */
  public int getUserCount() {
    IntHolder holder = countCache.getIntHolder();
    int count = holder.getValue();
    if (-1 == count) {
      holder.setValue(count);
    }
    return count;
  }

  /**
   * Create a new user
   *
   * @param login Login for the user
   * @param passwd Password for the user
   * @param email Email for the user
   * @return
   */
  public User create(String login, String passwd, String email) {
    User user = storage.storageCreate(login, passwd, email);
    IntHolder holder = countCache.getIntHolder();
    holder.inc();
    // Remove from missing list
    return user;
  }

  /**
   * Store user
   *
   * @param user User to store
   */
  public void store(User user) {
    user.setMTime(new Timestamp(new Date().getTime()));
    storage.storageStore(user);
    return;
  }

  /**
   * Store user delayed. Some unimportant
   * changes to users are done every login etc.
   * This does not to be persistet every time,
   * only after some time the user is stored
   *
   * @param user User to store
   */
  public void delayedStore(User user) {
    synchronized (delayed) {
      if (!delayed.contains(user)) {
        delayed.add(user);
      }
    }
  }

  /**
   * Stores the user but does not change
   * user data (like last modified time)
   *
   * @param user User to store
   */
  public void systemStore(User user) {
    storage.storageStore(user);
    return;
  }

  /**
   * Remove user from system
   *
   * @param user User to remove
   */
  public void remove(User user) {
    // Add to missing list
    storage.storageRemove(user);
    IntHolder holder = countCache.getIntHolder();
    holder.dec();
    return;
  }

  /**
   * Test if an user in the system exists
   *
   * @param login Login of the user to test
   * @return
   */
  public boolean exists(String login) {
    // Cache this with an interceptor
    // Read from missing list
    return (null != storage.storageLoad(login));
  }

  /**
   * Load user from backend
   *
   * @param login Login of the user to load
   * @return
   */
  public User load(String login) {
    return storage.storageLoad(login);
  }
}
