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
package com.neotis.test.snip;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.app.Application;

public class SnipSpaceTest extends TestCase {
  public SnipSpaceTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(SnipSpaceTest.class);
    return s;
  }

  public void testSingleton() {
    assertNotNull("Singleton instance", SnipSpace.getInstance());
  }

  public void testLoadSnip() {
    Application app = new Application();
    Snip snip1 = SnipSpace.getInstance().create("A","A Content", app);
    Snip snip2 = SnipSpace.getInstance().load("A");
    assertEquals(snip2.getName(), "A");
    SnipSpace.getInstance().remove(snip1);
  }

  public void testParent() {
    Application app = new Application();
    Snip snip1 = SnipSpace.getInstance().create("A","A Content", app);
    Snip snip2 = SnipSpace.getInstance().create("B","B Content", app);
    snip2.setParent(snip1);
    assertEquals(snip1, snip2.getParent());
    SnipSpace.getInstance().remove(snip1);
    SnipSpace.getInstance().remove(snip2);
  }

  public void testExists() {
    Application app = new Application();
    Snip snip1 = SnipSpace.getInstance().create("A","A Content", app);
    assertTrue("Snip exists", SnipSpace.getInstance().exists("A"));
    SnipSpace.getInstance().remove(snip1);
  }

  public void testCreateAndDeleteSnip() {
    Application app = new Application();
    Snip snip1 = SnipSpace.getInstance().create("A","A Content", app);
    assertEquals(snip1.getName(), "A");
    assertEquals(snip1.getContent(), "A Content");
    SnipSpace.getInstance().remove(snip1);
  }

}