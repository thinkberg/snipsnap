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
package org.snipsnap.user;

import org.snipsnap.container.Components;
import org.snipsnap.snip.storage.UserStorage;

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
  private List delayed;
  private UserStorage storage;
  public static final int MILISECS_PER_MINUTE = 60 * 1000;

  public DefaultUserManager(UserStorage storage) {
    this.storage = storage;

    delayed = new LinkedList();

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
    }, 5 * MILISECS_PER_MINUTE, 5 * MILISECS_PER_MINUTE);
  }

  public List getAll() {
    return storage.storageAll();
  }

  public int getUserCount() {
    return storage.storageUserCount();
  }

  public User create(String login, String passwd, String email) {
    User user = storage.storageCreate(login, passwd, email);
    return user;
  }

  public void store(User user) {
    user.setMTime(new Timestamp(new Date().getTime()));
    storage.storageStore(user);
    return;
  }

  public void delayedStore(User user) {
    synchronized (delayed) {
      if (!delayed.contains(user)) {
        delayed.add(user);
      }
    }
  }

  public void systemStore(User user) {
    storage.storageStore(user);
    return;
  }

  public void remove(User user) {
    storage.storageRemove(user);
    return;
  }

  public User load(String login) {
    return storage.storageLoad(login);
  }
}
