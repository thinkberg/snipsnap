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

package org.snipsnap.interceptor;

import org.snipsnap.interceptor.custom.LogInterceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Invocation {
  private Iterator chain;
  private Object target;
  private Method method;
  private Object[] args;

  public Invocation(Object target, Method method, Object[] args) {
    this.target = target;
    this.method = method;
    this.args = args;
    List interceptors = new ArrayList();
    //Interceptor log = new LogInterceptor();
    //log.setName("Logging");
    //interceptors.add(log);
    chain = interceptors.iterator();
  }

  public Object[] getArgs() {
    return args;
  }

  public Method getMethod() {
    return method;
  }

  public Object next() throws Throwable {
    if (chain.hasNext()) {
      Interceptor i = (Interceptor) chain.next();
      //String filter = i.getFilter();
      // if (method.getName().match(filter) call, else do not use
      // interceptor
      return i.invoke(this);
    }
    //System.err.println("Calling target proxy.");
    return method.invoke(target, args);
  }
}
