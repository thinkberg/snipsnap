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

package org.snipsnap.snip.storage.query;

import snipsnap.api.snip.Snip;

import java.util.Comparator;

/**
 * Compares to snips for sorting
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public abstract class SnipComparator implements Comparator {
  /**
   * Implementation of the Comparator interface compare method that
   * takes to objects and casts them to snips
   *
   * @param o1 Snip to compare
   * @param o2 Snip to compare
   */
  public int compare(Object o1, Object o2) {
    if (!((o1 instanceof snipsnap.api.snip.Snip) && (o2 instanceof snipsnap.api.snip.Snip))) {
      throw new ClassCastException();
    }
    return compare((snipsnap.api.snip.Snip) o1, (snipsnap.api.snip.Snip) o2);

  }

  /**
   * Compares two snips according to the rules of the Comparator
   * interface. This method is used for determing the sorting order
   * of snips.
   *
   * @param s1 Snip to compare
   * @param s2 Snip to compare
   */
  public abstract int compare(snipsnap.api.snip.Snip s1, snipsnap.api.snip.Snip s2);
}
