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

package org.snipsnap.cache;

import org.snipsnap.snip.storage.query.Query;
import org.snipsnap.snip.storage.query.QueryKit;

import java.util.*;

/**
 * List with a query interface
 *
 * @author stephan
 * @version $Id$
 */

public class QueryList implements List {
  private List list;

  public QueryList(List list) {
    this.list = list;
  }

  public List querySorted(Comparator c, int size) {
    return QueryKit.querySorted(list, c, size);
  }

  public List querySorted(Query query, Comparator c) {
    return QueryKit.querySorted(list, query, c);
  }

  public List querySorted(Query query, Comparator c, int size) {
    return QueryKit.querySorted(list, query, c, size);
  }

  public List query(Query query) {
    return QueryKit.query(list, query);
  }

  // List interface
  public int size() {
    return list.size();
  }

  public boolean isEmpty() {
    return list.isEmpty();
  }

  public boolean contains(Object o) {
    return list.contains(o);
  }

  public Iterator iterator() {
    return list.iterator();
  }

  public Object[] toArray() {
    return list.toArray();
  }

  public Object[] toArray(Object[] objects) {
    return list.toArray(objects);
  }

  public boolean add(Object o) {
    return list.add(o);
  }

  public boolean remove(Object o) {
    return list.remove(o);
  }

  public boolean containsAll(Collection collection) {
    return list.containsAll(collection);
  }

  public boolean addAll(Collection collection) {
    return list.addAll(collection);
  }

  public boolean addAll(int i, Collection collection) {
    return list.addAll(i, collection);
  }

  public boolean removeAll(Collection collection) {
    return list.removeAll(collection);
  }

  public boolean retainAll(Collection collection) {
    return list.retainAll(collection);
  }

  public void clear() {
    list.clear();
  }

  public Object get(int i) {
    return list.get(i);
  }

  public Object set(int i, Object o) {
    return list.set(i, o);
  }

  public void add(int i, Object o) {
    list.add(i, o);
  }

  public Object remove(int i) {
    return list.remove(i);
  }

  public int indexOf(Object o) {
    return list.indexOf(o);
  }

  public int lastIndexOf(Object o) {
    return list.lastIndexOf(o);
  }

  public ListIterator listIterator() {
    return list.listIterator();
  }

  public ListIterator listIterator(int i) {
    return list.listIterator(i);
  }

  public List subList(int i, int i1) {
    return list.subList(i, i1);
  }

  public int hashCode() {
    return list.hashCode();
  }

  public boolean equals(Object o) {
    return list.equals(o);
  }
}

