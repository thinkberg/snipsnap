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

import com.neotis.user.Permissions;
import junit.framework.*;

import java.util.HashSet;
import java.util.Set;

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
    Set s1 = new HashSet();
    Set s2 = new HashSet();
    Set s3 = new HashSet();
    s1.add("user 1");
    s1.add("user 2");
    s2.add("user 1");
    s3.add("user 3");
    Permissions perms = new Permissions();
    assertTrue("Sets contain same items", perms.containsAny(s1, s2));
    assertTrue("Sets don't contain same items", !perms.containsAny(s1, s3));
  }

  public void testCheck() {
    Permissions perms = new Permissions();
    perms.add("Edit", "role 1");
    Set roles = new HashSet();
    roles.add("role 1");
    Set roles2 = new HashSet();
    roles.add("role x");

    assertTrue("User has Edit permission", perms.check("Edit", roles));
    assertTrue("User has not Edit permission", ! perms.check("Edit", roles2));
  }

  public void testEmptyRoles() {
    Permissions perms = new Permissions();
    perms.add("Edit", "role 1");
    Set roles = new HashSet();
    assertTrue("User has not Edit permission", ! perms.check("Edit", roles));
  }

  public void testEmptyPerms() {
    Permissions perms = new Permissions();
    Set roles = new HashSet();
    assertTrue("User with no roles has Edit permission", perms.check("Edit", roles));
    Set roles2 = new HashSet();
    roles.add("role x");
    assertTrue("User with roles has Edit permission", perms.check("Edit", roles2));

  }

  public void testNoRoles() {
    Permissions perms = new Permissions();
    perms.add("Edit");
    Set roles = new HashSet();
    roles.add("role 1");
    assertTrue("Permission with no roles grants no permissions", ! perms.check("Edit", roles));
  }
}
