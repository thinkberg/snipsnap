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

import java.util.List;

/**
 * User manager handles all register, creation and authentication of users.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public interface UserManager {
  /**
   * Get all users in the system
   *
   * @return
   */
  public List getAll();

  /**
   * Get the number of users in the system
   * @return
   */
  public int getUserCount();

  /**
   * Create a new user
   *
   * @param login Login for the user
   * @param passwd Password for the user
   * @param email Email for the user
   * @return
   */
  public User create(String login, String passwd, String email);

  /**
   * Store user
   *
   * @param user User to store
   */
  public void store(User user);

  /**
   * Store user delayed. Some unimportant
   * changes to users are done every login etc.
   * This does not to be persistet every time,
   * only after some time the user is stored
   *
   * @param user User to store
   */
  public void delayedStore(User user);

  /**
   * Stores the user but does not change
   * user data (like last modified time)
   *
   * @param user User to store
   */
  public void systemStore(User user);

  /**
   * Remove user from system
   *
   * @param user User to remove
   */
  public void remove(User user);

  /**
   * Load user from backend
   *
   * @param login Login of the user to load
   * @return
   */
  public User load(String login);

  /**
   * Test if an user in the system exists
   *
   * @param login Login of the user to test
   * @return
   */
  public boolean exists(String login);
}
