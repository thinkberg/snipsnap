package org.snipsnap.user;

import snipsnap.api.storage.UserStorage;
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

public class DefaultAuthenticationService implements AuthenticationService {
  private UserStorage storage;

  public DefaultAuthenticationService(UserStorage storage) {
    this.storage = storage;
  }

  public snipsnap.api.user.User authenticate(String login, String passwd) {
    return authenticate(login, passwd, !ENCRYPTED);
  }

  public User authenticate(String login, String passwd, boolean encrypted) {
    User user = storage.storageLoad(login);

//     System.out.println("user: "+user);
//     System.out.println("check: unencrypted: "+user.getPasswd().equals(passwd));
//     System.out.println(passwd+"-"+Digest.getDigest(passwd)+"-"+user.getPasswd());
//     System.out.println("check: encrypted: "+Digest.authenticate(passwd, user.getPasswd()));

    //@TODO split authenticate and lastLogin
    if (null != user &&
      (encrypted ? user.getPasswd().equals(passwd) : Digest.authenticate(passwd, user.getPasswd()))) {
      user.lastLogin();
      storage.storageStore(user);
      return user;
    } else {
      return null;
    }
  }

  /** Used for password-less login, i.e. X509Certificate
  */
  public User authenticate(String login) {
    User user = storage.storageLoad(login);

    //@TODO split authenticate and lastLogin
    if (null != user) {
      user.lastLogin();
      storage.storageStore(user);
      return user;
    } else {
      return null;
    }
  }

  public boolean isAuthenticated(User user) {
    return user != null && !(user.isGuest() || user.isNonUser());
  }
}
