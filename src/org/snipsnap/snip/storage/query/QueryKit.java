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

import java.util.*;

/**
 * Kit which applies Queries to Lists
 *
 * @author stephan
 * @version $Id$
 */

public class QueryKit {
  /**
   * Sort a list with a comparator. The result list size can be
   * limited.
   *
   * @param list List to query
   * @param c Comparator which defines the sorting order
   * @param size How many elements should be returned
   */
  public static List querySorted(List list, Comparator c, int size) {
    ArrayList result = new ArrayList(list);
    Collections.sort(result, c);
    return result.subList(0, Math.min(size, result.size()));
  }

  /**
   * Query and sort a list with a comparator and a given query.
   *
   * @param list List to query
   * @param query Query which defines what elements to return
   * @param c Comparator which defines the sorting order
   * @param size How many elements should be returned
   */
  public static List querySorted(List list, Query query, Comparator c) {
    List result = query(list, query);
    Collections.sort(result, c);
    return result;
  }

  /**
   * Query and sort a list with a comparator and a given query.
   * The result list size can be limited.
   *
   * @param list List to query
   * @param query Query which defines what elements to return
   * @param c Comparator which defines the sorting order
   * @param size How many elements should be returned
   */
  public static List querySorted(List list, Query query, Comparator c, int size) {
    List result = query(list, query);
    Collections.sort(result, c);
    return result.subList(0, Math.min(size, result.size()));
  }
  /**
   * Query a list with  a given query.
   *
   * @param list List to query
   * @param query Query which defines what elements to return
   */
  public static List query(List list, Query query) {
    Iterator iterator = list.iterator();
    List result = new ArrayList();
    while (iterator.hasNext()) {
      Object object = iterator.next();
      if (query.fit(object)) {
        result.add(object);
      }
    }
    return result;
  }
}
