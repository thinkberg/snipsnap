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

import junit.framework.Test;
import junit.framework.TestSuite;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipImpl;
import org.snipsnap.snip.SnipLink;

import java.io.IOException;
import java.io.StringWriter;

public class PermalinkTest extends SnipTestSupport {
  protected StringWriter writer;

  public PermalinkTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    writer = new StringWriter();
    super.setUp();
  }

  public static Test suite() {
    return new TestSuite(PermalinkTest.class);
  }

  public void testUrl() {
    Snip snip = new SnipImpl("test", "test");
    String anchor = "anchor";
    try {
      SnipLink.appendUrl(writer, snip.getName(), anchor);
    } catch (IOException e) {
      // Ignore
    }
    assertEquals("Url with Permalink is rendered correctly",
        "http://snipsnap.org:8668/space/test#anchor", writer.getBuffer().toString());
  }
}
