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

package org.snipsnap.snip.storage;

import org.snipsnap.cache.Cache;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.storage.query.SnipComparator;
import org.snipsnap.snip.storage.query.SnipQuery;

import java.sql.Timestamp;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**
 * Wrapper with finders for in-memory searching. Can
 * be used with JDBC...Storage when all Snips are
 * kept in memory.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class MemorySnipStorage implements SnipStorage {
  //@TODO replace with dynamic proxy.

  private SnipStorage storage;
  private Cache cache;
  private Class type = Snip.class;
  private Comparator nameComparator, nameComparatorDesc, mTimeComparatorDesc;

  public MemorySnipStorage(SnipStorage storage, Cache cache) {
    this.storage = storage;
    this.cache = cache;

    this.nameComparator = new SnipComparator() {
          public int compare(Snip s1, Snip s2) {
            return s1.getName().compareTo(s2.getName());
          }
        };

    this.nameComparatorDesc = new SnipComparator() {
          public int compare(Snip s1, Snip s2) {
            return s2.getName().compareTo(s1.getName());
          }
        };

    this.mTimeComparatorDesc = new SnipComparator() {
      public int compare(Snip s1, Snip s2) {
        return s2.getMTime().compareTo(s1.getMTime());
      }
    };
  }

  // Basic manipulation methods Load,Store,Create,Remove
  public Snip storageLoad(String name) {
    return storage.storageLoad(name);
  }

  public Object loadObject(String name) {
    return storage.loadObject(name);
  }

  public void storageStore(Snip snip) {
    storage.storageStore(snip);
  }

  public Snip storageCreate(String name, String content) {
    return storage.storageCreate(name, content);
  }

  public void storageRemove(Snip snip) {
    storage.storageRemove(snip);
  }

  // Finder methods
  public int storageCount() {
    return cache.getCache(type).size();
  }

  public List storageAll() {
    return cache.getCache(type);
  }

  public List storageByHotness(int size) {
    return cache.querySorted(
        new SnipComparator() {
          public int compare(Snip s1, Snip s2) {
            return s1.getAccess().getViewCount() < s2.getAccess().getViewCount() ? 1:-1;
          }
        }, size, type);
  }

  public List storageByUser(final String login) {
    return cache.query(new SnipQuery() {
      public boolean fit(Snip snip) {
        return (login.equals(snip.getCUser()));
      }
    }, type);
  }

  public List storageByDateSince(final Timestamp date) {
    return cache.query(new SnipQuery() {
      public boolean fit(Snip snip) {
        return (date.before(snip.getMTime()));
      }
    }, type);
  }

  public List storageByRecent(int size) {
    return cache.querySorted(new SnipComparator() {
      public int compare(Snip s1, Snip s2) {
        return s2.getMTime().compareTo(s1.getMTime());
      }
    }, size, type);
  }

  public List storageByComments(final Snip parent) {
    return cache.query(new SnipQuery() {
      public boolean fit(Snip snip) {
        return (parent == snip.getCommentedSnip());
      }
    }, type);
  }

  public List storageByParent(final Snip parent) {
    return cache.query(new SnipQuery() {
      public boolean fit(Snip snip) {
        return (parent == snip.getParent());
      }
    }, type);
  }

  public List storageByParentNameOrder(final Snip parent, int count) {
    return cache.querySorted(new SnipQuery() {
      public boolean fit(Snip snip) {
        return (parent == snip.getParent());
      }
    }, nameComparatorDesc , count, type);
  }

  public List storageByParentModifiedOrder(Snip parent, int count) {
    List result = storageByParent(parent);
    Collections.sort(result, mTimeComparatorDesc);
    return result;
  }

  public List storageByDateInName(final String start, final String end) {
    return cache.querySorted(new SnipQuery() {
      public boolean fit(Snip snip) {
        String name = snip.getName();
        Snip parent = snip.getParent();
        //System.err.print(" name="+name);
        //if (parent != null) {
        // System.err.print(" parent="+parent.getName());
        //}
        //System.err.print(" start="+start);
        //System.err.print(" end="+end);
        return (start.compareTo(name) <= 0 && end.compareTo(name) >=0 &&
            null != parent && "start".equals(parent.getName()));
      }
    }, nameComparator, type);
  }
}
