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

  String unencodedString = null;
  String encodedString = null;

  protected void setUp() throws Exception {
    super.setUp();
    // the text below is complete nonsense, randomly typed
    unencodedString = new String("سىزذتازىذتازسذ.شسذز.سىازذتا".getBytes(), "UTF-8");
    encodedString = "%D8%B3%D9%89%D8%B2%D8%B0%D8%AA%D8%A7%D8%B2%D9%89%D8%B0%D8%AA%D8%A7%D8%B2%D8%B3%D8%B0.%D8%B4%D8%B3%D8%B0%D8%B2.%D8%B3%D9%89%D8%A7%D8%B2%D8%B0%D8%AA%D8%A7";
  }

  public static Test suite() {
    return new TestSuite(EncoderTest.class);
  }

  public void testUTF8Encoding() throws UnsupportedEncodingException {
    assertEquals("String UTF-8 is encoded correctly",
        encodedString, URLEncoderDecoder.encode(unencodedString, "UTF-8"));
  }

  public void testUTF8Decoding() throws UnsupportedEncodingException  {
    assertEquals("String UTF-8 is decoded correctly",
        unencodedString, URLEncoderDecoder.decode(encodedString, "UTF-8"));
  }

  /*
  public void testCutLength() throws UnsupportedEncodingException {
    assertEquals(unencodedString, SnipLink.cutLength(unencodedString, 10));
  }

  public void testCutLengthLink() throws UnsupportedEncodingException {
    assertEquals("Encoding works with cutted strings",
        "<a href=\"/space/"+encodedString+"\">"+unencodedString.substring(0, 22)+"...</a>",
                 SnipLink.createLink(unencodedString, SnipLink.cutLength(unencodedString, 25)));
  }
  */
}
