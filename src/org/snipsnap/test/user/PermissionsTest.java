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
package com.neotis.test.user;

import com.neotis.user.*;
import com.neotis.app.Application;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import junit.framework.*;

public class PermissionsTest extends TestCase {
  public PermissionsTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(PermissionsTest.class);
    return s;
  }

  public void testContainsAny() {
    Roles s1 = new Roles();
    Roles s2 = new Roles();
    Roles s3 = new Roles();
    s1.add("user 1");
    s1.add("user 2");
    s2.add("user 1");
    s3.add("user 3");
    assertTrue("Sets contain same items", s1.containsAny(s1));
    assertTrue("Sets don't contain same items", !s1.containsAny(s3));
  }

  public void testCheck() {
    Permissions perms = new Permissions();
    perms.add("Edit", "role 1");
    Roles roles = new Roles();
    roles.add("role 1");
    Roles roles2 = new Roles();
    roles.add("role x");

    assertTrue("User has Edit permission", perms.check("Edit", roles));
    assertTrue("User has not Edit permission", ! perms.check("Edit", roles2));
  }

  public void testEmptyRoles() {
    Permissions perms = new Permissions();
    perms.add("Edit", "role 1");
    Roles roles = new Roles();
    assertTrue("User has not Edit permission", ! perms.check("Edit", roles));
  }

  public void testEmptyPerms() {
    Permissions perms = new Permissions();
    Roles roles = new Roles();
    assertTrue("User with no roles has Edit permission", perms.check("Edit", roles));
    Roles roles2 = new Roles();
    roles.add("role x");
    assertTrue("User with roles has Edit permission", perms.check("Edit", roles2));

  }

  public void testOwner() {
    User user1 = new User("user1 1", "password 1","user1@user1.de");
    User user2 = new User("user1 2","password 2","user1@user1.de");

    Application app = new Application();
    app.setUser(user1);

    // create with user1 1
    Snip snip1 = SnipSpace.getInstance().create("A","A Content", app);

    // modify with user1 2
    app.setUser(user2);
    snip1.setContent("B content");
    SnipSpace.getInstance().store(snip1, app);

    Roles roles = new Roles();
    roles.add(Security.OWNER);
    // user1 2 is not owner of snip
    assertTrue(! Security.hasRoles(user2, snip1, roles));
    // user1 1 is owner of snip
    assertTrue(Security.hasRoles(user1, snip1, roles));

    SnipSpace.getInstance().remove(snip1);
  }

  public void testNoRoles() {
    Permissions perms = new Permissions();
    perms.add("Edit");
    Roles roles = new Roles();
    roles.add("role 1");
    assertTrue("Permission with no roles grants no permissions", ! perms.check("Edit", roles));
  }

  public void testSerialize() {
    Permissions perms = new Permissions();
    perms.add("Edit", "role 1");
    assertEquals("Edit:role 1", perms.toString());
  }

  public void testDeserialize() {
    String permString = "Edit:role 1|Remove:role 2";
    Permissions perms = new Permissions(permString);
    Roles roles = new Roles();
    roles.add("role 1");
    roles.add("role 2");
    assertTrue(perms.check("Edit", roles));
    assertTrue(perms.check("Remove", roles));
  }
}
