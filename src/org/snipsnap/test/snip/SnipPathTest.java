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
import org.snipsnap.snip.SnipSpace;
import org.jmock.Mock;

import java.io.StringWriter;
import java.io.IOException;

public class SnipPathTest extends SnipTestSupport {
  private StringWriter writer;
  private Mock mockSpace;

  public SnipPathTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
    writer = new StringWriter();
    mockSpace = mock(SnipSpace.class);
  }

  public static Test suite() {
    return new TestSuite(SnipPathTest.class);
  }

  public void testPathWithoutSnips() {
    mockSpace.expects(atLeastOnce()).method("exists").will(returnValue(false));

    Snip snip = new SnipImpl("SWT/Stephan/Students", "Test Content");
    try {
      snip.getPath().append(writer, (SnipSpace) mockSpace.proxy());
    } catch (IOException e) {
      fail("Exception thrown "+e.getMessage());
    }
    assertEquals("Path without existing snips", "<a href=\"space/start\">start</a> > SWT > Stephan > Students",
        writer.getBuffer().toString());
  }

}

