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

package org.snipsnap.interceptor.custom;

import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.interceptor.InterceptorSupport;
import org.snipsnap.interceptor.Invocation;
import org.snipsnap.user.Roles;
import org.snipsnap.user.Security;
import org.snipsnap.user.User;

import java.security.GeneralSecurityException;

public class SnipSpaceACLInterceptor extends InterceptorSupport {
  private Roles roles;

  public SnipSpaceACLInterceptor() {
    super();
    roles = new Roles();
    //roles.add("Editor");
    roles.add("Admin");
  }

  public Object invoke(Invocation invocation) throws Throwable {
    String method = invocation.getMethod().getName();
    if(method.equals("remove")) {
      User user = Application.get().getUser();
      if (!Security.hasRoles(user, null, roles)) {
        Logger.debug("SECURITY EXCEPTION");
        throw new GeneralSecurityException("Not allowed to remove.");
      }

    }
    return invocation.next();
  }
}
