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

package org.snipsnap.snip;

import java.util.Comparator;

/**
 * Compares names like 2003-10-11/3 and sorts them
 * in reverse order
 *
 * 2003-10-05/11 2003-10-06/1  2003-10-05/1 2003-10-05/2
 *
 * is sorted to:
 *
 * 2003-10-06/1 2003-10-05/11 2003-10-05/2 2003-10-05/1
 *
 * @author stephan
 * @version $Id$
 */

public class PostNameComparator implements Comparator {
  public int compare(Object o1, Object o2) {
    if (! (o1 instanceof String) || !( o2 instanceof String)) {
      throw new ClassCastException();
    }
    String name1 = (String) o1;
    String name2 = (String) o2;
    int index1 = name1.lastIndexOf("/");
    int index2 = name2.lastIndexOf("/");
    int result = name2.substring(0, index2 != -1 ? index2 : name2.length())
      .compareTo(name1.substring(0, index1 != -1 ? index1 : name1.length()));
    if (0 == result) {
      int number1 = Integer.parseInt(name1.substring(index1+1));
      int number2 = Integer.parseInt(name2.substring(index2+1));
      result = number1 > number2 ? -1 : 1;
    }
    return result;
  }
}
