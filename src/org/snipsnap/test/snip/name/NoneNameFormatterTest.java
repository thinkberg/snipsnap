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

package org.snipsnap.test.snip.name;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.snipsnap.snip.name.NameFormatter;
import org.snipsnap.snip.name.NoneFormatter;

public class NoneNameFormatterTest extends TestCase {
  private NameFormatter formatter;

  public NoneNameFormatterTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(NoneNameFormatterTest.class);
  }

  protected void setUp() throws Exception {
    super.setUp();
    formatter = new NoneFormatter();
  }

  public void testNotAltered() {
    assertEquals("Name is not altered", "test", formatter.format("test"));
  }

  public void testUsesParent() {
    formatter.setParent( new NameFormatter() {
      public void setParent(NameFormatter parent) {
      }

      public String format(String name) {
        return "XXX";
      }
    });
    assertEquals("Parent formatter is used", "XXX", formatter.format("test"));
  }
}