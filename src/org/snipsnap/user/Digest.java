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

import org.radeox.util.logging.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Digest handler for encrypting passwords.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Digest {
  private static MessageDigest digest;

  static {
    try {
      digest = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      Logger.warn("UserManager: unable to load digest algorithm", e);
      digest = null;
    }
  }

  public static String getDigest(String s) {
    if (digest != null && s != null) {
      return digestToHexString(digest.digest(s.getBytes()));
    }
    return "";
  }

  /**
   * Get a hexadecimal cookie digest from a user.
   */
  public static String getCookieDigest(User user) {
    String tmp = user.getLogin() + user.getPasswd() + user.getLastLogin().toString();
    return getDigest(tmp);
  }

  /**
   * Compare a password with an encryped password.
   */
  public static boolean authenticate(String password, String encrypted) {
    return encrypted.equals(getDigest(password));
  }

  /**
   * Make a hexadecimal character string out of a byte array digest
   */
  public static String digestToHexString(byte[] digest) {
    byte b = 0;
    StringBuffer buffer = new StringBuffer();

    for (int i = 0; i < digest.length; ++i) {
      b = digest[i];
      int value = (b & 0x7F) + (b < 0 ? 128 : 0);
      buffer.append(value < 16 ? "0" : "");
      buffer.append(Integer.toHexString(value).toUpperCase());
    }
    return buffer.toString();
  }
}
