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
package com.neotis.test.filter;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import com.neotis.snip.Snip;
import com.neotis.snip.filter.BoldFilter;
import com.neotis.snip.filter.Filter;
import com.neotis.snip.filter.ItalicFilter;

public class FilterTest extends TestCase {
  public FilterTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(FilterTest.class);
    return s;
  }

  public Snip getMock() {
    return new Snip("mock","mock");
  }

  public void testBold() {
    Filter filter = new BoldFilter();
    assertEquals("<span class=\"bold\">Text</span>", filter.filter("__Text__" , getMock()));
  }

  public void testItalic() {
    Filter filter = new ItalicFilter();
    assertEquals("<span class=\"italic\">Text</span>", filter.filter("~~Text~~", getMock()));
  }

}
