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
package org.snipsnap.test.snip;

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.app.Application;
import org.snipsnap.user.User;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ChildrenTest extends TestCase {
  public ChildrenTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(ChildrenTest.class);
    return s;
  }

  public void testChildren() {
    Application app = Application.get();
    User user = new User("user 1", "password 1","user@user.de");
    app.setUser(user);
    Snip snip1 = SnipSpace.getInstance().create("A", "A Content");
    Snip snip2 = SnipSpace.getInstance().create("B", "B Content");
    snip1.addSnip(snip2);
    assertTrue("Children not null", snip1.getChildren() != null);
    assertEquals("Correct Parent", snip1, snip2.getParent());
    assertTrue("Children contain added Snip", snip1.getChildren().contains(snip2));
    assertTrue("One Child", snip1.getChildren().size() == 1);

    snip1.removeSnip(snip2);
    assertTrue("Empty after removal", snip2.getChildren().isEmpty());

    SnipSpace.getInstance().remove(snip1);
    SnipSpace.getInstance().remove(snip2);
  }

}