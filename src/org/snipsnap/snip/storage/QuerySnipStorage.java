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

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.storage.query.SnipComparator;
import org.snipsnap.snip.storage.query.SnipQuery;
import org.snipsnap.snip.storage.query.QueryKit;
import org.snipsnap.app.Application;
import org.radeox.util.logging.Logger;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Wrapper with finders for in-memory searching. Can
 * be used with JDBC...Storage when all Snips are
 * kept in memory (via MemorySnipStorage).
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class QuerySnipStorage implements SnipStorage {
  //@TODO replace with dynamic proxy.
  private SnipStorage storage;

  // Count comparators, make the comparator by default
  // with most usages. Make this dynamic
  private Comparator nameComparator, nameComparatorDesc, mTimeComparatorDesc, cTimeComparator;

  public QuerySnipStorage(SnipStorage storage) {
    this.storage = storage;

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

    this.cTimeComparator = new SnipComparator() {
      public int compare(Snip s1, Snip s2) {
        return s1.getCTime().compareTo(s2.getCTime());
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
    return storage.storageAll().size();
  }

  public List storageAll() {
    return storage.storageAll();
  }

  public List storageByHotness(int size) {
    return QueryKit.querySorted(storage.storageAll(),
        new SnipComparator() {
          public int compare(Snip s1, Snip s2) {
            return s1.getAccess().getViewCount() < s2.getAccess().getViewCount() ? 1:-1;
          }
        }, size);
  }

  public List storageByUser(final String login) {
    return QueryKit.query(storage.storageAll(), new SnipQuery() {
      public boolean fit(Snip snip) {
        return (login.equals(snip.getCUser()));
      }
    });
  }

  public List storageByDateSince(final Timestamp date) {
    return QueryKit.query(storage.storageAll(), new SnipQuery() {
      public boolean fit(Snip snip) {
        return (date.before(snip.getMTime()));
      }
    });
  }

  public List storageByRecent(int size) {
    return QueryKit.querySorted(storage.storageAll(), new SnipComparator() {
      public int compare(Snip s1, Snip s2) {
        return s2.getMTime().compareTo(s1.getMTime());
      }
    }, size);
  }

  public List storageByComments(final Snip parent) {
    return QueryKit.querySorted(storage.storageAll(), new SnipQuery() {
      public boolean fit(Snip snip) {
        return (parent == snip.getCommentedSnip());
      }
    }, cTimeComparator);
  }

  public List storageByParent(final Snip parent) {
    return QueryKit.query(storage.storageAll(),new SnipQuery() {
      public boolean fit(Snip snip) {
        return (parent == snip.getParent());
      }
    });
  }

  public List storageByParentNameOrder(final Snip parent, int count) {
 //   Logger.debug("Children date order. parent="+parent.getName()+" "+parent);
    Logger.debug("all = "+storage.storageAll());
    Logger.debug("childs for="+parent);
    List list = QueryKit.querySorted(storage.storageAll(), new SnipQuery() {
      public boolean fit(Snip snip) {
//        Logger.debug("snip.parent = "+snip.getParent());
//        Logger.debug("snip.name = "+snip.getName());
//         if (snip.getParent() != null) {
//          Logger.debug("snip.parent.name = "+snip.getParent().getName());
//        }
        Logger.debug("snip="+snip.getParent());
        Logger.debug("? "+(parent == snip.getParent()));
        return (parent == snip.getParent());
      }
    }, nameComparatorDesc, count);
   Logger.debug("result = "+list.toString());
   return list;
  }

  public List storageByParentModifiedOrder(Snip parent, int count) {
    List result = storageByParent(parent);
    Collections.sort(result, mTimeComparatorDesc);
    return result.subList(0, Math.min(count, result.size()));
  }

  public List storageByDateInName(final String start, final String end) {
    final String startName = Application.get().getConfiguration().getStartName();
    return QueryKit.querySorted(storage.storageAll(), new SnipQuery() {
      public boolean fit(Snip snip) {
        String name = snip.getName();
        Snip parent = snip.getParent();
        return (start.compareTo(name) <= 0 && end.compareTo(name) >= 0 &&
            null != parent && startName.equals(parent.getName()));
      }
    }, nameComparator);
  }
}
