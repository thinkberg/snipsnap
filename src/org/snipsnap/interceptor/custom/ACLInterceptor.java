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

import org.snipsnap.snip.Snip;
import org.snipsnap.user.Roles;
import org.snipsnap.user.User;
import org.snipsnap.security.AccessController;
import org.snipsnap.container.Components;

import java.security.GeneralSecurityException;

import dynaop.Interceptor;
import dynaop.Invocation;


/**
 * Access Control Interceptor for checking permissions of set operations on objects.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class ACLInterceptor implements Interceptor {
  private Roles roles;
  private AccessController controller;

  public ACLInterceptor() {

    super();
    roles = new Roles();
    roles.add("Editor");
    controller = (AccessController) Components.getComponent(AccessController.class);
  }

  public Object intercept(Invocation invocation) throws Throwable {
    // hack should a.) also check other methods b.) declare security for every method
    String name = invocation.getMethod().getName();
    User user = Application.get().getUser();
    Snip snip = (Snip) invocation.getProxy().getProxyContext().unwrap();
    if (invocation.getMethod().getName().startsWith("set")) {
       // if (controller.checkPermission(user, new Permission("CHANGE_SNIP"), new OwnerAccessContext(snip))) {
      //Logger.debug("ACLInterceptor: Method="+invocation.getMethod().getName());
      //Logger.debug("ACLInterceptor: User = "+user);
      //Logger.debug("ACLInterceptor: Snip = "+snip);
      if (user != null && !user.isAdmin()) {// TODO: checking for the admin is a hack
        if (!( controller.checkPermission(user, AccessController.EDIT_SNIP, snip))) {
          throw new GeneralSecurityException(snip.getName() + ": " + user + " is not allowed to modify object");
        }
      }
    } else if ("getContent".equals(name) || "getXMLContent".equals(name)) {
      String snipName = snip.getName();
      if (user != null && ("SnipSnap/config".equals(snipName) || snipName.startsWith("SnipSnap/blacklist")) && !user.isAdmin()) {
        return "content protected";
      }
    }
    return invocation.proceed();
  }
}
