/*            Compent
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

import org.snipsnap.snip.Ownable;
import snipsnap.api.snip.Snip;
import snipsnap.api.user.*;
import snipsnap.api.user.User;

import java.util.*;

/**
 * Manages security and checks if a role has a permission.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class DefaultPermissionManager implements PermissionManager {
  private AuthenticationService authenticationService;

  private Permission[] EDITOR_PERMISSIONS = {
    Permission.REMOVE_SNIP, Permission.EDIT_COMMENT, Permission.POST_TO_SNIP, Permission.LOCK_SNIP };
  private Permission[] USER_PERMISSIONS = {
    Permission.EDIT_SNIP, Permission.CREATE_SNIP, Permission.POST_COMMENT };
  private Permission[] OWNER_PERMISSIONS = {
    Permission.EDIT_COMMENT, Permission.LOCK_SNIP };
  private Permission[] GUEST_PERMISSIONS = {
    Permission.VIEW_SNIP };

  private Map rolesToPermissions;
  private Map permissionToRoles;

  public DefaultPermissionManager(AuthenticationService authenticationService) {
    this.authenticationService = authenticationService;

    rolesToPermissions = new HashMap();
    rolesToPermissions.put("Editor", EDITOR_PERMISSIONS);
    rolesToPermissions.put("User", USER_PERMISSIONS);
    rolesToPermissions.put("Owner", OWNER_PERMISSIONS);
    rolesToPermissions.put("Guest", GUEST_PERMISSIONS);

    permissionToRoles = new HashMap();
    Iterator iterator = rolesToPermissions.keySet().iterator();
    while (iterator.hasNext()) {
      String role = (String) iterator.next();
      Permission[] rolePermissions = (Permission[]) rolesToPermissions.get(role);
      for (int i = 0; i < rolePermissions.length; i++) {
        Permission permission = rolePermissions[i];
        Set permissions;
        if (permissionToRoles.containsKey(permission)) {
          permissions = (Set) permissionToRoles.get(permission);
        } else {
          permissions = new HashSet();
          permissionToRoles.put(permission, permissions);
        }
        permissions.add(role);
      }
    }
    //System.err.println("rolesToPermissions="+rolesToPermissions);
    //System.err.println("permissionToRoles="+permissionToRoles);
  }

  public boolean check(Permission permission, snipsnap.api.user.User user, snipsnap.api.snip.Snip snip) {
    // for all roles of the user
    //    check for all permission of the roles
    //       if permission is in
    Set roles = getRoles(user, snip).getRoleSet();
    if (! permissionToRoles.containsKey(permission)) {
      return false;
    } else {
      //System.err.println("Permission found.");
      Set rolesWithPermission = (Set) permissionToRoles.get(permission);
      //System.err.println("Roles="+roles);
      //System.err.println("rolesWithP="+rolesWithPermission);
      rolesWithPermission.retainAll(roles);
      return ! rolesWithPermission.isEmpty();
    }
  }

  /**
   * Return the roles for a user
   *
   * @param user
   * @return
   */
  private Roles getRoles(User user) {
    Roles userRoles = new Roles(user.getRoles());
    if (authenticationService.isAuthenticated(user)) {
      userRoles.add(Roles.AUTHENTICATED);
    }
    return userRoles;
  }

  /**
   * Return the roles for a user in a snip
   * context. This adds the owner role to roles
   * list
   *
   * @param user User to check
   * @param object Object with possible owner
   * @return List of roles for user and object
   */
  private Roles getRoles(User user, snipsnap.api.snip.Snip object) {
    Roles roles = getRoles(user);
//    if (object instanceof Ownable) {
//      Ownable o = object;
//      if (o.isOwner(user)) {
//        roles.add(Roles.OWNER);
//      }
//    }
    return roles;
  }
}

