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

package org.snipsnap.snip.storage;

import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;
import snipsnap.api.user.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * Storage backend for User data. Users
 * are stored in files.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public abstract class FileUserStorage implements UserStorage {
  /**
   * Return name of the file to store
   * user into
   *
   * @param login User to get file name for
   * @return
   */
  protected abstract String getFileName(String login);

  /**
   * Store the user to the given directory
   *
   * @param user User to store
   * @param out Output stream where the user is stored
   */
  protected abstract void storeUser(User user, OutputStream out) throws IOException;

  /**
   * Load the user data to a map from the given
   * directory
   *
   * @param login Login of the user to load
   * @param in Input stream from which the user is loaded
   * @return
   */
  protected abstract Map loadUser(String login, InputStream in) throws IOException;

  /**
   * Return the base directory where all users are stored
   *
   * @return
   */
  public File getWorkingDir() {
    Application app = Application.get();
    return new File(new File(app.getConfiguration().getFileStore()), "users");
  }

  /**
   * Return the base directory where all users are stored
   *
   * @param applicationOid
   * @return
   */
  public File getWorkingDir(String applicationOid) {
    Application app = Application.get();
    return app.getConfiguration().getUserPath(applicationOid);
  }

  /**
   * Store an user in to the backend
   *
   * @param user User to store
   */
  public void storageStore(User user) {
    File userDir = getWorkingDir();

    if (!userDir.exists()) {
      userDir.mkdirs();
    }

    File userFile = new File(userDir, getFileName(user.getLogin()));
    if (userFile.exists()) {
      Logger.log("FileUserStorage: backing up " + userFile.getPath());
      File backup = new File(userFile.getPath() + ".bck");
      userFile.renameTo(backup);
    }

    try {
      storeUser(user, new FileOutputStream(userFile));
    } catch (IOException e) {
      Logger.log("FileUserStorage: Cannot store user "+user.getLogin(), e);
    }
  }

  /**
   * Create a new user in the backend
   *
   * @param login Login name of the user
   * @param passwd Credential of the user
   * @param email Email adress of the user
   * @return
   */
  public User storageCreate(String login, String passwd, String email) {
    File userDir = getWorkingDir();

    if (!userDir.exists()) {
      userDir.mkdirs();
    }
    File userFile = new File(userDir, getFileName(login));

    User user = new snipsnap.api.user.User(login, passwd, email);
    String applicationOid = (String) Application.get().getObject(Application.OID);
    Timestamp cTime = new Timestamp(new java.util.Date().getTime());
    user.setCTime(cTime);
    user.setMTime(cTime);
    user.setLastLogin(cTime);
    user.setLastAccess(cTime);
    user.setLastLogout(cTime);
    user.setApplication(applicationOid);
    try {
      storeUser(user, new FileOutputStream(userFile));
    } catch (Exception e) {
      Logger.log("FileUserStorage: Cannot create user "+login, e);
    }
    return user;
  }

  /**
   * Remove an user from the backend
   *
   * @param user User to remove
   */
  public void storageRemove(snipsnap.api.user.User user) {
    removeUser(user, getWorkingDir());
  }

  /**
   * Remove user files from directory
   *
   * @param user User to remove
   * @param userDir Directory which contains the user data
   */
  protected void removeUser(snipsnap.api.user.User user, File userDir) {
    File userFile = new File(userDir, getFileName(user.getLogin()));
    if (userFile.exists()) {
      File backup = new File(userFile.getPath() + ".removed");
      userFile.renameTo(backup);
    }
  };

  /**
   * Return the number of users in the backend which
   * are stored in the file system
   *
   * @return
   */
  public int storageUserCount() {
    return storageAll().size();
  }

  /**
   * Load a user from the backend. Users are
   * stored in the file system.
   *
   * @param login Login of the user to load
   * @return
   */
 public User storageLoad(String login) {
    File userFile = new File(getWorkingDir(), getFileName(login));

    Map map = null;
    try {
      map = loadUser(login, new FileInputStream(userFile));
      // create user from map
    } catch (Exception e) {
      Logger.log("FileUserStorage: Cannot load user "+login, e);
    }

    if (null == map) {
      return null;
    } else {
      return parseUser(map);
    }
  }

  /**
   * Return a list of all users from
   * the file system
   *
   * @return
   */
  public List storageAll() {
    // load all users from directory
    return null;
  }

  protected User parseUser(Map map) {
    return UserSerializer.getInstance().deserialize(map, new User());
  }
}
