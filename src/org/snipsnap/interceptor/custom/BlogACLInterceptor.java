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

import org.snipsnap.app.Application;

import org.snipsnap.user.User;
import org.snipsnap.container.Components;
import org.snipsnap.security.AccessController;

import java.security.GeneralSecurityException;

import dynaop.Interceptor;
import dynaop.Invocation;
import gabriel.components.context.OwnerAccessContext;
import gabriel.Permission;

public class BlogACLInterceptor implements Interceptor {
  private AccessController access;

  public BlogACLInterceptor() {
    access = (AccessController) Components.getComponent(AccessController.class);
  }

  public Object intercept(Invocation invocation) throws Throwable {
    if (invocation.getMethod().getName().startsWith("post")) {
      User user = Application.get().getUser();
      if (! access.checkPermission(user, new Permission("POST_BLOG"), new OwnerAccessContext(null))) {
        throw new GeneralSecurityException("Not allowed to post.");
      }
    }
    return invocation.proceed();
  }
}
