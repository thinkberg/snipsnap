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

  public MemorySnipStorage(SnipStorage storage, Cache cache) {
    this.storage = storage;
    this.cache = cache;
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
    },
        new SnipComparator() {
          public int compare(Snip s1, Snip s2) {
            return s1.getName().compareTo(s2.getName());
          }
        }
        , count, type);
  }

  public List storageByParentModifiedOrder(Snip parent, int count) {
    List result = storageByParent(parent);
    Collections.sort(result, new SnipComparator() {
      public int compare(Snip s1, Snip s2) {
        return s2.getMTime().compareTo(s1.getMTime());
      }
    });
    return result;
  }

  public List storageByDateInName(String start, String end) {
    // finder.setString(1, start);
    // finder.setString(2, end);
    // finder.setString(3, "start");
    return storage.storageByDateInName(start, end);
  }
}