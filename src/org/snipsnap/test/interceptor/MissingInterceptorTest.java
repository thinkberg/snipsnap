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

package org.snipsnap.test.interceptor;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.snipsnap.interceptor.Aspects;
import org.snipsnap.interceptor.custom.MissingInterceptor;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.test.mock.MockObject;
import org.snipsnap.test.mock.MockSnipSpace;

import java.lang.reflect.Proxy;

public class MissingInterceptorTest extends TestCase {
  private MockObject mock;
  private SnipSpace space;
  private MissingInterceptor interceptor;

  public MissingInterceptorTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    mock = new MockSnipSpace();
    Aspects aspect = new Aspects(mock);
    space = (SnipSpace) Proxy.newProxyInstance(MockSnipSpace.class.getClassLoader(),
        new Class[]{SnipSpace.class}, aspect);

    interceptor = new MissingInterceptor();
    aspect.addInterceptor(interceptor);
  }

  public static Test suite() {
    return new TestSuite(MissingInterceptorTest.class);
  }

  public void testExistsUsesCache() {
    space.exists("TestSnip"); // put in missing cache
    assertEquals("Exists() called once", 1, mock.getCount("exists"));
    space.exists("TestSnip"); // should read from cache
    assertEquals("Exists() not called when in cache", 1, mock.getCount("exists"));
  }

  public void testMissingExists() {
    assertTrue("Snip does not exist", !space.exists("TestSnip"));
    assertTrue("Snip is in missing set", interceptor.getMissing().contains("TESTSNIP"));
  }

  public void testMissingCreate() {
    space.exists("TestSnip");
    space.create("TestSnip", "TestContent");
    assertTrue("Snip is not missing set", !interceptor.getMissing().contains("TestSnip"));
    assertEquals("MissingInterceptor calls create()", 1, mock.getCount("create"));
  }

}
