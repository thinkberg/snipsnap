/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * --LICENSE NOTICE--
 */
package org.snipsnap.test.dynaop;

import junit.framework.Test;
import junit.framework.TestSuite;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.snipsnap.render.macro.list.AtoZListFormatter;
import org.snipsnap.snip.BlogImpl;
import org.snipsnap.snip.SnipImpl;
import org.snipsnap.interceptor.custom.BlogACLInterceptor;
import org.snipsnap.interceptor.custom.ACLInterceptor;
import org.jmock.MockObjectTestCase;
import org.jmock.Mock;
import dynaop.*;

public class DynaopTest extends MockObjectTestCase {
  private boolean called = false;

  public static Test suite() {
    return new TestSuite(DynaopTest.class);
  }

  protected void setUp() throws Exception {
  }

  public void test() {
    Aspects aspects = new Aspects();

    ProxyAware aware = new ProxyAware() {
      public void setProxy(Proxy proxy) {
        called = true;
      }
    };

    aspects = new dynaop.Aspects();
    ProxyFactory proxyFactory = ProxyFactory.getInstance(aspects);
    ProxyAware wrapped = (ProxyAware) proxyFactory.wrap(aware);
//    assertTrue("setProxy was called", called);
  }
}
