/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * --LICENSE NOTICE--
 */
package org.snipsnap.test.render.macro.list;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.radeox.util.Linkable;
import org.radeox.util.Nameable;
import org.snipsnap.render.macro.list.SimpleList;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

public class SimpleListTest extends ListFormatterSupport {
  public SimpleListTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(SimpleListTest.class);
  }

  protected void setUp() throws Exception {
    super.setUp();
    formatter = new SimpleList();
  }

  public void testNameable() {
    Collection c = Arrays.asList(new Nameable[]{
      new Nameable() {
        public String getName() {
          return "name:test";
        }
      }
    });
    try {
      formatter.format(writer, emptyLinkable, "", c, "", false);
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("Nameable is rendered",
        "<div class=\"list\"><div class=\"list-title\"></div><blockquote>name:test</blockquote></div>",
        writer.toString());
  }

  public void testLinkable() {
    Collection c = Arrays.asList(new Linkable[]{
      new Linkable() {
        public String getLink() {
          return "link:test";
        }
      }
    });
    try {
      formatter.format(writer, emptyLinkable, "", c, "", false);
    } catch (IOException e) {
      e.printStackTrace();
    }

    assertEquals("Linkable is rendered",
        "<div class=\"list\"><div class=\"list-title\"></div><blockquote>link:test</blockquote></div>",
        writer.toString());
  }

  public void testSingeItem() {
    Collection c = Arrays.asList(new String[]{"test"});
    try {
      formatter.format(writer,emptyLinkable, "", c, "", false);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals("Single item is rendered",
        "<div class=\"list\"><div class=\"list-title\"></div><blockquote>test</blockquote></div>",
        writer.toString());
  }


  public void testSize() {
    Collection c = Arrays.asList(new String[]{"test"});
    try {
      formatter.format(writer, emptyLinkable, "", c, "", true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals("Size is rendered",
        "<div class=\"list\"><div class=\"list-title\"> (1)</div><blockquote>test</blockquote></div>",
        writer.toString());
  }

  public void testEmpty() {
    Collection c = Arrays.asList(new String[]{});
    try {
      formatter.format(writer, emptyLinkable, "", c, "No items", false);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals("Empty list is rendered",
        "<div class=\"list\"><div class=\"list-title\"></div>No items</div>",
        writer.toString());
  }

  public void testTwoItems() {
    Collection c = Arrays.asList(new String[]{"test1", "test2"});
    try {
      formatter.format(writer, emptyLinkable, "", c, "", false);
    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals("Two items are rendered",
        "<div class=\"list\"><div class=\"list-title\"></div><blockquote>test1, test2</blockquote></div>",
        writer.toString());
  }

}
