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
import org.snipsnap.snip.SnipLink;

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

  public void testImage() {
    assertEquals("<img src=\"http://snipsnap.org:8668/images/test.png\" alt=\"test\" border=\"0\"/>", SnipLink.createImage("test"));
  }

  public void testImageAlt() {
    assertEquals("<img src=\"http://snipsnap.org:8668/images/test.png\" alt=\"alttext\" border=\"0\"/>", SnipLink.createImage("test", "alttext"));
  }

  public void testImageAltExtension() {
    assertEquals("<img src=\"http://snipsnap.org:8668/images/test.jpg\" alt=\"alttext\" border=\"0\"/>", SnipLink.createImage("test", "alttext", "jpg"));
  }

}
