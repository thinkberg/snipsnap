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

package org.snipsnap.security;

import org.snipsnap.user.User;

import java.util.Set;
import java.util.Iterator;

import gabriel.acl.Acl;
import gabriel.acl.AclEntry;
import gabriel.Principal;
import gabriel.Group;
import gabriel.Permission;

/**
 * Check for access to resources and operations
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class DefaultAccessController implements AccessController {
  private Acl acl;
  private AclManager aclManager;
  private Principal owner;

  public DefaultAccessController() {
    this.aclManager = aclManager;

    owner = new Principal("AclOwner");
    acl = new Acl(owner, "SnipSnap");

    Group editors = new Group("Editor");

    AclEntry entry = new AclEntry(editors);
    Permission post = new Permission("POST_BLOG");
    entry.addPermission(post);
    acl.addEntry(owner, entry);
  }

  public boolean checkPermission(User user, Permission permission, AccessContext context) {
    // generate principal from user
    // probably take context into account
    // check if he has the permission to do things
    Set roles = user.getRoles().getRoleSet();

    Iterator iterator = roles.iterator();
    boolean hasPermission = false;
    while (iterator.hasNext()) {
      String role = (String) iterator.next();
      hasPermission = hasPermission || acl.checkPermission(new Principal(role), permission);
    }
    return hasPermission;
  }
}