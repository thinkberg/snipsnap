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

package org.snipsnap.util;

import snipsnap.api.snip.Snip;

import java.util.*;

/**
 * Implements a partial search wrapper around a hashmap
 *
 * This file is based on code from Jack Shirazi which
 * was public domain. Thanks. http://www.javaperformancetuning.com
 *
 * credit Jack Shirazi
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class PartialSearcher implements Map {
  private Map hash;
  private String[] sortedArray;

  public PartialSearcher() {
    this(HashMap.class);
  }

   public PartialSearcher(Class klass) {
     try {
       hash = (Map) klass.newInstance();
     } catch (Exception e) {
       hash = new HashMap();
     }
     createSortedArray();
  }

  public Snip[] match(String s) {
     return match(s, s + '\uFFFF');
  }

  public snipsnap.api.snip.Snip[] match(String start, String end) {
    int startIdx = binarySearch(sortedArray, start, 0, sortedArray.length - 1);
    int endIdx = binarySearch(sortedArray, end, 0, sortedArray.length - 1);

    snipsnap.api.snip.Snip[] objs = new snipsnap.api.snip.Snip[endIdx - startIdx];
    for (int i = startIdx; i < endIdx; i++) {
      objs[i - startIdx] = (snipsnap.api.snip.Snip) hash.get(sortedArray[i]);
    }
    return objs;
  }

  public void createSortedArray() {
    sortedArray = new String[hash.size()];
    // @replace with toArray?
    Iterator iterator = hash.keySet().iterator();
    for (int i = 0; iterator.hasNext(); i++) {
      sortedArray[i] = (String) iterator.next();
    }
    quicksort(sortedArray, 0, sortedArray.length - 1);
  }

  public static int binarySearch(String[] arr, String elem, int fromIndex, int toIndex) {
    int mid,cmp;
    while (fromIndex <= toIndex) {
     mid = (fromIndex + toIndex) / 2;
      if ((cmp = arr[mid].compareTo(elem)) < 0) {
        fromIndex = mid + 1;
      } else if (cmp > 0) {
        toIndex = mid - 1;
      } else {
        return mid;
      }
    }
    return fromIndex;
  }

  public void quicksort(String[] arr, int lo, int hi) {
    if (lo >= hi) {
      return;
    }

    int mid = (lo + hi) / 2;
    String tmp;
    String middle = arr[mid];

    if (arr[lo].compareTo(middle) > 0) {
      arr[mid] = arr[lo];
      arr[lo] = middle;
      middle = arr[mid];
    }

    if (middle.compareTo(arr[hi]) > 0) {
      arr[mid] = arr[hi];
      arr[hi] = middle;
      middle = arr[mid];

      if (arr[lo].compareTo(middle) > 0) {
        arr[mid] = arr[lo];
        arr[lo] = middle;
        middle = arr[mid];
      }
    }

    int left = lo + 1;
    int right = hi - 1;

    if (left >= right) {
      return;
    }

    for (; ;) {
      while (arr[right].compareTo(middle) > 0) {
        right--;
      }

      while (left < right && arr[left].compareTo(middle) <= 0) {
        left++;
      }

      if (left < right) {
        tmp = arr[left];
        arr[left] = arr[right];
        arr[right] = tmp;
        right--;
      } else {
        break;
      }
    }

    quicksort(arr, lo, left);
    quicksort(arr, left + 1, hi);
  }

  public int size() {
    return hash.size();
  }

  public boolean isEmpty() {
    return hash.isEmpty();
  }

  public boolean containsKey(Object o) {
    return hash.containsKey(o);
  }

  public boolean containsValue(Object o) {
    return hash.containsValue(o);
  }

  public Object get(Object o) {
    return hash.get(o);
  }

  public Object put(Object o, Object o1) {
    Object object = hash.put(o,o1);
    createSortedArray();
    return object;
  }

  public Object remove(Object o) {
    Object object = hash.remove(o);
    createSortedArray();
    return object;
  }

  public void putAll(Map map) {
    hash.putAll(map);
    createSortedArray();
  }

  public void clear() {
    hash.clear();
    createSortedArray();
  }

  public Set keySet() {
    return hash.keySet();
  }

  public Collection values() {
    return hash.values();
  }

  public Set entrySet() {
    return hash.entrySet();
  }
}
