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

import dynaop.Pointcuts;
import dynaop.ProxyFactory;
import dynaop.ProxyAware;
import dynaop.Proxy;
import org.snipsnap.interceptor.custom.ACLInterceptor;
import org.snipsnap.interceptor.custom.BlogACLInterceptor;
import org.snipsnap.snip.BlogImpl;
import org.snipsnap.snip.SnipImpl;

public class Aspects {
  /** Neat trick, done with some thinking and help from Jon (Nanning) and
   *  Rickard. Thanks.
      static ThreadLocal currentThis = new ThreadLocal();

      Neat it was indeed :-)
   */

  public static dynaop.Aspects aspects;
  public static ProxyFactory proxyFactory;

  // move this to a component
  public static synchronized Object wrap(Object object) {
    if (null == aspects) {
      aspects = new dynaop.Aspects();
      aspects.interceptor(Pointcuts.instancesOf(BlogImpl.class),
          Pointcuts.ALL_METHODS, new BlogACLInterceptor());

      aspects.interceptor(Pointcuts.instancesOf(SnipImpl.class),
          Pointcuts.ALL_METHODS, new ACLInterceptor());

      proxyFactory = ProxyFactory.getInstance(aspects);
    }
    Proxy proxy = (Proxy) proxyFactory.wrap(object);
    if (object instanceof ProxyAware) {
      ((ProxyAware) object).setProxy(proxy);
    }
    return proxy;
  }

}
