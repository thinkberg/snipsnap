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

package org.snipsnap.test.render.macro.list;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllListTests extends TestCase {
  public AllListTests(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(SimpleListTest.class);
    s.addTestSuite(ExampleListFormatterTest.class);
    s.addTestSuite(AtoZListFormatterTest.class);
    return s;
  }
}
