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

import org.snipsnap.util.ConnectionManager;
import org.snipsnap.util.log.Logger;
import org.snipsnap.cache.Cache;
import org.snipsnap.jdbc.Loader;
import org.snipsnap.jdbc.FinderFactory;
import org.snipsnap.jdbc.Finder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Password handler for encrypting passwords.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Password {
  private static MessageDigest digest;

  static {
    try {
      digest =  MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      System.err.println("UserManager: unable to load digest algorithm: "+e);
      digest = null;
    }
  }

  /**
   * Get a hexadecimal cookie digest from a user.
   */
  public static String getCookieDigest(User user) {
    if (digest != null) {
      String tmp = user.getLogin() + user.getPasswd() + user.getLastLogin().toString();
      return digestToHexString(digest.digest(tmp.getBytes()));
    }
    return "";
  }

  /**
   * Compare a password with an encryped password.
   */
  public static boolean authenticate(String password, String encrypted) {
    return encrypted.equals(digest.digest(password.getBytes()));
  }

  /**
   * Make a hexadecimal character string out of a byte array digest
   */
  private static String digestToHexString(byte[] digest) {
    StringBuffer hexString = new StringBuffer();
    hexString.setLength(0);
    for (int i = 0; i < digest.length; i++) {
      hexString.append(Integer.toHexString(digest[i]).toUpperCase());
    }
    return hexString.toString();
  }
}
