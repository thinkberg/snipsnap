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

package com.neotis.user;

import com.neotis.snip.Snip;
import com.neotis.snip.Ownable;

/**
 * Security manager for checking permission, roles etc.
 *
 * @author stephan
 * @version $Id$
 */

public class Security {
  public final static String AUTHENTICATED = "Authenticated";
  public final static String OWNER = "Owner";

  /**
   * Adds authenticated role to user roles
   *
   * @param user User to check
   * @return List of roles
   */
  public static Roles getRoles(User user) {
    Roles userRoles = user.getRoles();
    if (UserManager.getInstance().isAuthenticated(user)) {
      userRoles.add(AUTHENTICATED);
    }
    return userRoles;
  }

  /**
   * Adds owner role to roles list
   *
   * @param user User to check
   * @param object Object with possible owner
   * @return List of roles for user and object
   */
  public static Roles getRoles(User user, Snip object) {
    Roles roles = getRoles(user);
    if (object instanceof Ownable) {
      Ownable o = (Ownable) object;
      if (o.isOwner(user)) {
        roles.add(OWNER);
      }
    }
    return roles;
  }

  public static boolean hasRoles(User user, Roles roles) {
    Roles userRoles = getRoles(user);
    return userRoles.containsAny(roles);
  }

  public static boolean hasRoles(User user, Snip object, Roles roles) {
    Roles userRoles = getRoles(user, object);
    return userRoles.containsAny(roles);
  }

  /**
   * Check if the user has the permission on the object
   *
   * @param permission the permission to check, e.g. "Edit"
   * @param user the user to check permissions for, e.g. "funzel"
   * @param object the object that should be manipulated
   * @return
   */
  public static boolean checkPermission(String permission, User user, Snip object) {
    Permissions permissions = object.getPermissions();
    return permissions.check(permission, getRoles(user, object));
  }
}
