package org.snipsnap.user;

import org.snipsnap.snip.storage.UserStorage;

import java.util.Random;
import java.util.Map;
import java.util.HashMap;

import snipsnap.api.user.*;
import snipsnap.api.user.User;

/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002-2003 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free SoftSware Foundation; either version 2
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

public class PasswordService {
  private UserStorage storage;
  private Map authKeys;

  public PasswordService(UserStorage storage) {
    this.storage = storage;
    authKeys = new HashMap();
  }

  public String getPassWordKey(User user) {
    String key = Digest.getDigest(Integer.toString((new Random()).nextInt()));
    authKeys.put(key, user);
    return key;
  }

  public User changePassWord(String key, String passwd) {
    User user = (snipsnap.api.user.User) authKeys.get(key);
    if (null != user) {
      user.setPasswd(passwd);
      storage.storageStore(user);
      authKeys.remove(key);
    }
    return user;
  }
}
