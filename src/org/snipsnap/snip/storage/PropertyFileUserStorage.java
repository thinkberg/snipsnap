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
import org.snipsnap.user.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Storage backend for User data. Users
 * are stored in properties files.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class PropertyFileUserStorage extends FileUserStorage {

  /**
   * Return name of the file to store
   * user into
   *
   * @param login User to get file name for
   * @return
   */
  protected String getFileName(String login) {
    return login + ".properties";
  }

  /**
   * Store the user to the given directory
   *
   * @param user User to store
   * @param out OutputStream where the user is stored
   */
  protected void storeUser(User user, OutputStream out) throws IOException {
    Properties props = new Properties();
    props.putAll(UserSerializer.getInstance().createUserMap(user));
    props.store(out, "");
  }

  /**
   * Return a list of all users from
   * the file system
   *
   * @return
   */
  public List storageAll() {
    List users = new ArrayList();
    File userDir = getWorkingDir();

    String[] files = userDir.list(new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.endsWith(".properties");
      }
    });

//    System.out.println("User files="+Arrays.asList(files));
    for (int i = 0; i < files.length; i++) {
      String fileName = files[i];
      String login = fileName.substring(0, fileName.lastIndexOf("."));
      try {
        User user = parseUser(loadUser(login, new FileInputStream(new File(userDir, fileName))));
        users.add(user);
      } catch (Exception e) {
        Logger.log("PropertyFileUserStorage: cannot load user "+fileName, e);
      }
    }
    return users;
  }

  /**
   * Load the user data to a map from the given
   * directory
   *
   * @param login Login of the user to load
   * @param in Input stream from which to load the data
   * @return
   */
  protected Map loadUser(String login, InputStream in) throws IOException {
    Properties userData = new Properties();
    userData.load(in);
    return userData;
  }
}
