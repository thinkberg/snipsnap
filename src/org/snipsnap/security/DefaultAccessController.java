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

import gabriel.Permission;
import gabriel.Subject;
import gabriel.Principal;
import gabriel.acl.Acl;
import gabriel.components.AccessManager;
import gabriel.components.AccessManagerImpl;
import gabriel.components.AclStore;
import gabriel.components.context.AccessContext;
import gabriel.components.context.OwnerAccessContext;
import gabriel.components.io.FileAclStore;
import gabriel.components.parser.AclParser;
import org.snipsnap.user.User;
import org.snipsnap.snip.Snip;

/**
 * Check for access to resources and operations
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class DefaultAccessController implements AccessController  {
  private AccessManager manager;

  public DefaultAccessController() {
    AclStore store = new FileAclStore(new AclParser());
    manager = new AccessManagerImpl(store);
  }

  public boolean checkPermission(User user, String permission, Snip snip) {
    return checkPermission(user, new Permission(permission), new OwnerAccessContext(snip));
  }

  public boolean checkPermission(User user, Permission permission, AccessContext context) {
    Subject subject = user.getSubject();

    System.err.println("Check user="+user.getLogin()+":"+subject.getName() +" permission="+permission+" principals="+subject.getPrincipals());
    boolean hasPermission = manager.checkPermission(subject.getPrincipals(), permission);
    System.err.println("  hasPermission="+hasPermission);
    return hasPermission;
  }
}