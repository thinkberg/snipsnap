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

package org.snipsnap.test.commons;

import junit.framework.Test;
import junit.framework.TestSuite;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.test.snip.SnipTestSupport;
import org.snipsnap.util.URLEncoderDecoder;

import java.io.UnsupportedEncodingException;

public class EncoderTest extends SnipTestSupport {
  public EncoderTest(String name) {
    super(name);
  }

  private final static String UTF8_CHARS = "\u65E5\u672C"; // ?? (nihon)
  private final static String UTF8_ENCODED = "%E6%97%A5%E6%9C%AC";

  String unencodedString = null;
  String encodedString = null;

  protected void setUp() throws Exception {
    super.setUp();
    System.setProperty("file.encoding", "UTF-8");

    StringBuffer longUnencodedString = new StringBuffer();
    for (int chars = 0; chars < 20; chars++) {
      longUnencodedString.append(UTF8_CHARS);
    }
    unencodedString = longUnencodedString.toString();

    StringBuffer longEncodedString = new StringBuffer();
    for (int chars = 0; chars < 20; chars++) {
      longEncodedString.append(UTF8_ENCODED);
    }
    encodedString = longEncodedString.toString();
  }

  public static Test suite() {
    return new TestSuite(EncoderTest.class);
  }

  public void testUTF8Characters() {
    assertEquals(UTF8_CHARS, unencodedString.substring(0, UTF8_CHARS.length()));
  }

  public void testUTF8Encoding() throws UnsupportedEncodingException {
    assertEquals("String UTF-8 is not correctly encoded",
                 encodedString, URLEncoderDecoder.encode(unencodedString, "UTF-8"));
  }

  public void testUTF8Decoding() throws UnsupportedEncodingException {
    assertEquals("String UTF-8 is not correctly decoded",
                 unencodedString, URLEncoderDecoder.decode(encodedString, "UTF-8"));
  }

  public void testCutLength() throws UnsupportedEncodingException {
    assertEquals(unencodedString.substring(0, 7) + "...", SnipLink.cutLength(unencodedString, 10));
  }

  public void testCutLengthLink() throws UnsupportedEncodingException {
    assertEquals("Cutting link text is broken",
                 "<a href=\"/space/" + encodedString + "\">" + unencodedString.substring(0, 22) + "...</a>",
                 SnipLink.createLink(unencodedString, SnipLink.cutLength(unencodedString, 25)));
  }
}
