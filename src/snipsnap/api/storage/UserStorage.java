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

package snipsnap.api.storage;

import snipsnap.api.user.User;

import java.util.List;

/**
 * Storage backend for User data
 *
 * @author Stephan J. Schmidt
 * @version $Id: UserStorage.java 1816 2005-04-06 17:56:22Z stephan $
 */

public interface UserStorage  {
  /**
   * Store an user in to the backend
   *
   * @param user User to store
   */
  public void storageStore(User user);

  /**
   * Create a new user in the backend
   *
   * @param login Login name of the user
   * @param passwd Credential of the user
   * @param email Email adress of the user
   * @return
   */
  public User storageCreate(String login, String passwd, String email);

  /**
   * Remove an user from the backend
   *
   * @param user User to remove
   */
  public void storageRemove(User user);

  /**
   * Return the number of users in the backend
   *
   * @return
   */
  public int storageUserCount();

  /**
   * Load a user from the backend
   *
   * @param login Login of the user to load
   * @return
   */

  public User storageLoad(String login);

  /**
   * Return a list of all users
   *
   * @return
   */
  public List storageAll();
}
