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
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.snipsnap.cache.QueryList;
import org.snipsnap.snip.storage.query.Query;

import java.util.ArrayList;
import java.util.List;

public class QueryListTest extends TestCase {
  protected QueryList list;

  public QueryListTest(String name) {
    super(name);
  }

  public abstract class IntegerQuery implements Query {
    public boolean fit(Object object) {
      if (!(object instanceof Integer)) return false;
      return fit((Integer) object);
    }

    public abstract boolean fit(Integer i);
  }

  protected void setUp() throws Exception {
    list = new QueryList(new ArrayList());
    list.add(new Integer(1));
    list.add(new Integer(5));
    list.add(new Integer(2));
    super.setUp();
  }

  public static Test suite() {
    return new TestSuite(QueryListTest.class);
  }

  public void testQuery() {
    List test = new ArrayList();
    test.add(new Integer(2));
    assertEquals(test, list.query(
        new IntegerQuery() {
          public boolean fit(Integer i) {
            return i.intValue() == 2;
          }
        }
    ));
  }
}
