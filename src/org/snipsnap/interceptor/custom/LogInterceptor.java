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

public class LogInterceptor extends InterceptorSupport {
  public Object invoke(Invocation invocation) throws Throwable {
    // before
    System.out.print(name + ": before " + invocation.getMethod().getName() + " ");
    print(invocation.getArgs());
    Object result = invocation.next();
    // after
    System.out.print(name + ": after " + invocation.getMethod().getName());
    if (null == result) {
      System.out.println();
    } else {
      System.out.println(" = "+result.toString());
    }
    return result;
  }

  private void print(Object[] args) {
    System.out.print("[");
    if (null != args) {
      for (int i = 0; i < args.length; i++) {
        Object object = args[i];
        if (null == object) {
          System.out.print("null");
        } else {
          String className = object.getClass().getName();
          System.out.print(object.toString());
          System.out.print("(");
          System.out.print(className.substring(className.lastIndexOf(".") + 1) + ")");
        }
        if (i < args.length - 1) {
          System.out.print(", ");
        }

      }
    }
    System.out.println("]");
    return;
  }
}
