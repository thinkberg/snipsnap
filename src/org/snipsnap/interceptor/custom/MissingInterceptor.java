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

import java.util.HashSet;
import java.util.Set;

/**
 * Interceptor for caching SnipSpace misses. SnipSnap
 * often calls exists() to detect if a snip exists.
 * This operation can be costly so we keep the results
 * to exists() cached.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class MissingInterceptor extends InterceptorSupport {
  // @TODO we use map instead of set to increment the
  // missing counter each time a snip could not be found
  private Set missing;

  public MissingInterceptor() {
    super();
    missing = new HashSet();
  }

  public Set getMissing() {
    return missing;
  }

  public Object invoke(Invocation invocation) throws Throwable {
    String method = invocation.getMethod().getName();

    // exists(): return cache if possible
    if ("exists".equals(method)) {
      String name = ((String) invocation.getArgs()[0]).toUpperCase();
      if (missing.contains(name)) {
        return Boolean.FALSE;
      }
      Object result = invocation.next();
      if (Boolean.FALSE.equals(result)) {
        missing.add(name);
      }
      return result;
      // create() remove from missing list
    } else if ("create".equals(method)) {
      String name = ((String) invocation.getArgs()[0]).toUpperCase();
      if (missing.contains(name.toUpperCase())) {
        missing.remove(name);
      }
    }
    Object result = invocation.next();
    return result;
  }
}
