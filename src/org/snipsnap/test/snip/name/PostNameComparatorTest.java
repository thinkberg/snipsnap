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

package org.snipsnap.test.snip.name;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;

import org.snipsnap.snip.PostNameComparator;

public class PostNameComparatorTest extends TestCase {
  public PostNameComparatorTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(PostNameComparatorTest.class);
  }

   public void testSortOrder() {
     List list = new ArrayList();
     list.add("2003-10-05/1");
     list.add("2003-10-05/11");
     list.add("2003-10-06/1");
     list.add("2003-10-05/2");

     Collections.sort(list, new PostNameComparator());

     Iterator iterator = list.iterator();
     String resultList = "";
     while (iterator.hasNext()) {
       String name = (String) iterator.next();
       resultList = resultList + name + " ";
     }
     assertEquals("Correct order", "2003-10-06/1 2003-10-05/11 2003-10-05/2 2003-10-05/1 ", resultList);
  }
}