package org.snipsnap.interceptor.custom;


import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.Roles;
import org.snipsnap.user.Security;
import org.snipsnap.user.User;
import org.codehaus.nanning.config.P;
import org.codehaus.nanning.config.Pointcut;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.Invocation;

import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.util.Iterator;

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

public class SnipSpaceACLAspect implements Aspect {
  Pointcut removePc = P.methodName("remove.*");
  private Roles roles;

  public SnipSpaceACLAspect() {
    roles = new Roles();
    roles.add("Editor");
  }

  public void introduce(AspectInstance instance) {
  }

  public void advise(AspectInstance instance) {
    Class klass = instance.getClassIdentifier();
    System.out.println("class=" + klass);
    if (klass != null && klass.equals(SnipSpace.class)) {
      removePc.advise(instance, new MethodInterceptor() {
        public Object invoke(Invocation invocation) throws Throwable {
          User user = Application.get().getUser();
          if (!Security.hasRoles(user, null, roles)) {
            Logger.debug("SECURITY EXCEPTION");
            throw new GeneralSecurityException("Not allowed to remove.");
          }
          return null;
        }
      });
    }
  }
}
