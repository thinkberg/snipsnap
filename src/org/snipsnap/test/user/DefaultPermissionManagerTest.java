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

package org.snipsnap.test.user;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.framework.TestCase;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipImpl;
import org.snipsnap.test.mock.MockSnipSpace;
import org.snipsnap.user.*;

import java.io.StringWriter;
import java.io.IOException;

public class DefaultPermissionManagerTest extends TestCase {

  private AuthenticationService service;

  public DefaultPermissionManagerTest(String name) {
    super(name);
  }


  public static Test suite() {
    return new TestSuite(DefaultPermissionManagerTest.class);
  }

  protected void setUp() throws Exception {
    service = new AuthenticationService() {
      public User authenticate(String login, String passwd) {
        return null;
      }

      public boolean isAuthenticated(User user) {
        return false;
      }
    };
  }

  public void testInit() {
    PermissionManager manager = new DefaultPermissionManager(service);
    assertNotNull("PermissionManager not null", manager);
  }

  public void testEditorHasRemoveSnipPermission() {
    PermissionManager manager = new DefaultPermissionManager(service);

    User user = new User("test", "testpw", "testmail");
    Roles roles = new Roles();
    roles.add("Editor");
    user.setRoles(roles);
    assertTrue("Editor has REMOVE_SNIP permission", manager.check(Permission.REMOVE_SNIP, user, null));

  }
}

