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

import org.snipsnap.interceptor.custom.*;
import org.snipsnap.snip.BlogImpl;
import org.snipsnap.snip.SnipImpl;
import org.snipsnap.snip.SnipSpaceImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class Aspects implements InvocationHandler {
  private Object target = null;
  private List interceptors;

  /** Neat trick, done with some thinking and help from Jon (Nanning) and
   *  Rickard. Thanks.
   */
  static ThreadLocal currentThis = new ThreadLocal();

  public static boolean hasTarget(Object proxy) {
    return ((Aspects) Proxy.getInvocationHandler(proxy)).hasTarget();
  }

  private boolean hasTarget() {
    return null != target;
  }

  public static Object getThis() {
    return currentThis.get();
  }

  public static Object newInstance(Object target) {
    return newInstance(target, target.getClass().getInterfaces());
  }

  public static Object newInstance(Object target, Class interfaceTarget) {
    return newInstance(target, new Class[]{interfaceTarget});
  }

  public static Object newInstance(Object target, Class[] interfaceTargets) {
    Class targetClass = target.getClass();
    Class interfaces[] = interfaceTargets;
    Aspects aspects = new Aspects(target);
    if (target.getClass().equals(SnipImpl.class)) {
      aspects.addInterceptor(new ACLInterceptor());
    } else if (target.getClass().equals(SnipSpaceImpl.class)) {
      aspects.addInterceptor(new MissingInterceptor());
      aspects.addInterceptor(new SnipSpaceACLInterceptor());
      aspects.addInterceptor(new StoreInterceptor());
    } else if (target.getClass().equals(BlogImpl.class)) {
      aspects.addInterceptor(new BlogACLInterceptor());
    }
    return Proxy.newProxyInstance(targetClass.getClassLoader(),
        interfaces, new Aspects(target));
  }

  public Aspects(Object target) {
    this.target = target;
    interceptors = new ArrayList();
  }

  public void addInterceptor(Interceptor interceptor) {
     interceptors.add(interceptor);
  }

  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//    return method.invoke(target,args);
    Object invocationResult = null;
    Object previousThis = currentThis.get();
    currentThis.set(proxy);
    Invocation i = new Invocation(target, method, args, interceptors);
    try {
      invocationResult = i.next();
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } finally {
      currentThis.set(previousThis);
    }

    return invocationResult;
  }
}
