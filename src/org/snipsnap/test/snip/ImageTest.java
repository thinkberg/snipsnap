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
import snipsnap.api.snip.Snip;
import org.snipsnap.snip.SnipImpl;
import snipsnap.api.snip.SnipLink;

import java.io.IOException;
import java.io.StringWriter;

public class ImageTest extends SnipTestSupport {
  public ImageTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    super.setUp();
  }

  public static Test suite() {
    return new TestSuite(ImageTest.class);
  }

  public void testImage() throws IOException {
    StringWriter writer = new StringWriter();
    snipsnap.api.snip.SnipLink.appendImage(writer, "test", null);
    assertEquals("Image without alt is rendered",
        "<img src=\"theme/images/test.png\" alt=\"test\" border=\"0\"/>", writer.toString());
  }

  public void testImageAlt() throws IOException {
    StringWriter writer = new StringWriter();
    SnipLink.appendImage(writer, "test", "alttext");
    assertEquals("Image with alt is rendered",
        "<img src=\"theme/images/test.png\" alt=\"alttext\" border=\"0\"/>", writer.toString());
  }

  public void testImageAltExtension() throws IOException {
    StringWriter writer = new StringWriter();
    snipsnap.api.snip.SnipLink.appendImage(writer, "test", "alttext", "jpg");
    assertEquals("Image with alt and extension is rendered",
        "<img src=\"theme/images/test.jpg\" alt=\"alttext\" border=\"0\"/>", writer.toString());
  }

  public void testSnipAttachedImage() throws IOException {
    StringWriter writer = new StringWriter();
    Snip snip = new SnipImpl("test", "test");
    snipsnap.api.snip.SnipLink.appendImage(writer, snip, "test", "alttext", "jpg", null);
    assertEquals("Image without position is rendered",
        "<img src=\"space/test/test.jpg\" alt=\"alttext\" border=\"0\"/>", writer.toString());
  }
}
