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

package org.snipsnap.util.collection;

import java.util.*;

/**
 * Collects some useful methods on collections.
 * Original author <a href="mailto:mgrosze@web.de">Michael Gro&szlig;e</a>
 *
 * @author    stephan
 * @version   $Id$
 */
public class Collections {

  /**
   * Filters the list to a new ArrayList with the filter.
   *
   * @param aList    a list to filter, is copied before any operation
   * @param aFilter  the applied filter
   * @return         an ArrayList containing all elements not filtered
   */
  public static List filter(List aList, Filterator aFilter) {
    List filteredList = new ArrayList();
    filteredList.addAll(aList);

    Iterator iterator = filteredList.iterator();
    while (iterator.hasNext()) {
      if (aFilter.filter(iterator.next())) {
        // also removes the element in the collection
        iterator.remove();
      }
    }
    return filteredList;
  }


  /**
   * Transforms a list to a new ArrayList with the transformer.
   *
   * @param aList         a list to transformed, is copied before any operation
   * @param aTransformer  Description of Parameter
   * @return              an ArrayList containing all transformed elements
   */
  public static List transform(List aList, Transformer aTransformer) {
    List transformedList = new ArrayList();
    transformedList.addAll(aList);

    ListIterator iterator = transformedList.listIterator();
    while (iterator.hasNext()) {
      iterator.set(aTransformer.transform(iterator.next()));
    }
    return transformedList;
  }


  /**
   * Joins all entries of a collection with a delimiter to a string. The function
   * works like the perl-function join.
   *
   * @param aCollection   a collection to be joined
   * @param delimiter     a delimiter dividing the entries
   * @return              an ArrayList a String with all entries of the list
   *                      separated by the delimiter
   */
  public static String join(Collection aCollection, String delimiter) {
    StringBuffer sb = new StringBuffer();

    Iterator iterator = aCollection.iterator();
    while (iterator.hasNext()) {
      sb.append(iterator.next().toString());
      if (iterator.hasNext()) {
        sb.append(delimiter);
      }
    }
    return sb.toString();
  }


  /**
   * Splits a String on a delimiter to a List. The function works like
   * the perl-function split.
   *
   * @param aString    a String to split
   * @param delimiter  a delimiter dividing the entries
   * @return           a String with all entries of the list
   *      separated by the delimiter
   */
  public static List split(String aString, String delimiter) {
    List list = new ArrayList();

    StringTokenizer st = new StringTokenizer(aString, delimiter);
    while (st.hasMoreTokens()) {
      list.add(st.nextToken());
    }
    return list;
  }
}
