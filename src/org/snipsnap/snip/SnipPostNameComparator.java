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

import snipsnap.api.snip.*;
import snipsnap.api.snip.Snip;

import java.util.Comparator;

/**
 * Compares snips with names like 2003-10-11/3 and sorts them
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

public class SnipPostNameComparator implements Comparator {
  private Comparator comparator = new PostNameComparator();

  public int compare(Object o1, Object o2) {
    if (! (o1 instanceof Snip) || !( o2 instanceof snipsnap.api.snip.Snip)) {
      throw new ClassCastException();
    }
    Snip snip1 = (Snip) o1;
    Snip snip2 = (Snip) o2;
    return comparator.compare(snip1.getName(), snip2.getName());
  }
}
