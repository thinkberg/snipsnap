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

import org.snipsnap.interceptor.InterceptorSupport;
import org.snipsnap.interceptor.Invocation;

import java.util.HashMap;
import java.util.Map;

/**
 * MissingLSnip interceptor caches method call result
 * from SnipSpace (exists, create).
 * When a missing snip is detected the
 * snip name is stored. When the snip is created
 * the snip is removed from the missing list.
 *
 * @author stephan
 * @version $Id$
 */

public class MissingSnipInterceptor extends InterceptorSupport {
  private Map missing;

  public MissingSnipInterceptor() {
    super();
    missing = new HashMap();
  }

  public Object invoke(Invocation invocation) throws Throwable {
    String methodName = invocation.getMethod().getName();

    if ("exists".equals(methodName)) {
      String name = ((String) invocation.getArgs()[0]).toUpperCase();
      // Snip is in the missing list
      if (missing.containsKey(name)) {
        return new Boolean(false);
      }
      Boolean result = (Boolean) invocation.next();
      // The snip does not exist so put it in the missing list
      if (result.equals(Boolean.FALSE)) {
        missing.put(name, new Integer(0));
      }
      return result;

    } else if ("create".equals(methodName)) {
      String name = ((String) invocation.getArgs()[0]).toUpperCase();

      Object result = invocation.next();

      if (missing.containsKey(name)) {
        missing.remove(name);
      }
      return result;
    } else {
      // Other method
      return invocation.next();
    }
  }
}
