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

package org.snipsnap.test.admin;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.test.snip.SnipTestSupport;
import org.snipsnap.admin.install.Installer;

public class InstallerTest extends TestCase {
  public InstallerTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  public static Test suite() {
    return new TestSuite(InstallerTest.class);
  }

  public void testShortPassword() {
    assertTrue("Password to short", ! Installer.checkPassword("test","test"));
  }

  public void testDifferentPasswords() {
    assertTrue("Passwords not equal", ! Installer.checkPassword("test123","test321"));
  }

  public void test6Chars() {
    assertTrue("Password too short", Installer.checkPassword("test123","test123"));
  }

  public void testNullPassword() {
    assertTrue("Password is null", ! Installer.checkPassword(null ,"test123"));
  }

  public void testShortUserName() {
    assertTrue("User name too short", ! Installer.checkUserName("te"));
  }

  public void testUserName() {
    assertTrue("User name too short", Installer.checkUserName("tes"));
  }

  public void testNullUserName() {
    assertTrue("User is null", ! Installer.checkUserName(null));
  }


}
