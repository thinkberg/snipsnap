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

import org.snipsnap.snip.Modified;

import java.sql.Timestamp;

import junit.framework.Test;
import junit.framework.TestSuite;

public class NiceTimeTest extends SnipTestSupport {
  private final static long ONE_SECOND = 1000;
  private final static long ONE_MINUTE = 60 * ONE_SECOND;
  private final static long ONE_HOUR = 60 * ONE_MINUTE;
  private final static long ONE_DAY = 24 * ONE_HOUR;
  private final static long ONE_YEAR = 365 * ONE_DAY;

  private final static long NOW = ONE_YEAR;

  public NiceTimeTest(String name) {
    super(name);
  }
  protected void setUp() throws Exception {
    super.setUp();
  }

  public static Test suite() {
    return new TestSuite(ImageTest.class);
  }

  public void testNiceSeconds() {
    assertEquals("just a blink of an eye", Modified.getNiceTime(NOW, NOW - ONE_SECOND));
  }

  public void testNiceMinutes() {
    assertEquals("one minute", Modified.getNiceTime(NOW, NOW - (ONE_MINUTE + 30 * ONE_SECOND)));
    assertEquals("5 minutes", Modified.getNiceTime(NOW, NOW - (5 * ONE_MINUTE)));
  }

  public void testNiceHours() {
    assertEquals("one hour", Modified.getNiceTime(NOW, NOW - ONE_HOUR));
    assertEquals("one hour and one minute", Modified.getNiceTime(NOW, NOW - (ONE_HOUR + ONE_MINUTE)));
    assertEquals("one hour and 5 minutes", Modified.getNiceTime(NOW, NOW - (ONE_HOUR + 5 * ONE_MINUTE)));
  }

  public void testNiceDays() {
    assertEquals("one day", Modified.getNiceTime(NOW, NOW - ONE_DAY));
    assertEquals("5 days", Modified.getNiceTime(NOW, NOW -  5 * ONE_DAY));
  }

  public void testNiceYears() {
    assertEquals("one year", Modified.getNiceTime(NOW, NOW - ONE_YEAR));
    assertEquals("2 years", Modified.getNiceTime(NOW, NOW - 2 * ONE_YEAR));
    assertEquals("one year and one day", Modified.getNiceTime(NOW, NOW - (ONE_YEAR + ONE_DAY)));
    assertEquals("5 years and 4 days", Modified.getNiceTime(NOW, NOW - (5 * ONE_YEAR + 4 * ONE_DAY)));
  }

}

