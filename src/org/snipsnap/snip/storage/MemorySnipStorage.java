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
import org.snipsnap.util.PartialSearcher;
import org.snipsnap.util.ApplicationAwareMap;
import org.snipsnap.app.Application;
import org.snipsnap.app.ApplicationManager;
import org.snipsnap.app.ApplicationStorage;

import java.sql.Timestamp;
import java.util.*;

/**
 * Wrapper with finders for in-memory searching. Can
 * be used with JDBC...Storage when all Snips are
 * kept in memory.
 *
 //@TODO replace with dynamic proxy.
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class MemorySnipStorage implements SnipStorage {
  public static final String NOT_SUPPORTED_EXCEPTION_MSG =
      "Method not supported, do not call MemorySnipStorage directly";

  private SnipStorage storage;

  private ApplicationAwareMap cache;

  // @TODO Keep list sorted with comparator
  // This is used to keep an list of all Snips
  // for faster storageAll() operations
  private Map allList;

  public MemorySnipStorage(SnipStorage storage, ApplicationManager manager) {
    this.storage = storage;

//    if (!(storage instanceof CacheableStorage)) {
//      //@TODO optimize with array
//      Iterator iterator = allList.iterator();
//      while (iterator.hasNext()) {
//        Snip snip = (Snip) iterator.next();
//        map.put(snip.getName().toUpperCase(), snip);
//      }
//    }

    cache = new ApplicationAwareMap(HashMap.class, PartialSearcher.class);

    if (storage instanceof CacheableStorage) {
      ((CacheableStorage) storage).setCache(cache);
    }

    // hash of list of snips
    // applicationOid -> [snip, snip, ...]
    allList = new HashMap();

    //This should also fill the cache
    Iterator iterator = manager.getApplications().iterator();
    while (iterator.hasNext()) {
      String oid = ((Properties)iterator.next()).getProperty(ApplicationStorage.OID);
      List instanceList = storage.storageAll(oid); 
      allList.put(oid, instanceList);
    }
  }

  // Basic manipulation methods Load,Store,Create,Remove
  public Snip[] match(String pattern) {
    return ((PartialSearcher) cache.getMap()).match(pattern.toUpperCase());
  }

  public Snip[] match(String start, String end) {
    return ((PartialSearcher) cache.getMap()).match(start.toUpperCase(), end.toUpperCase());
  }

  public Snip storageLoad(String name) {
    return (Snip) cache.getMap().get(name.toUpperCase());
  }

  public Object loadObject(String name) {
    return (Snip) cache.getMap().get(name.toUpperCase());
  }

  public void storageStore(Snip snip) {
    storage.storageStore(snip);
  }

  public Snip storageCreate(String name, String content) {
    Snip snip = storage.storageCreate(name, content);

    // TODO fix this, the allList is not necessarily correctly initialized!
    List allSnips = (List) allList.get(snip.getApplication());
    if(null == allSnips) {
      allSnips = storage.storageAll(snip.getApplication());
      allList.put(snip.getApplication(), allSnips);
    }
    allSnips.add(snip);
    cache.getMap().put(snip.getName().toUpperCase(), snip);
    return snip;
  }

  public void storageRemove(Snip snip) {
    storage.storageRemove(snip);
    List allSnips = (List) allList.get(snip.getApplication());
    if (null != allSnips) {
      allSnips.remove(snip);
    } else {
      System.err.println("WARNING: access to unknown oid: "+snip.getApplication());
    }
    cache.getMap().remove(snip.getName().toUpperCase());
  }

  public int storageCount() {
    String application = (String) Application.get().getObject(Application.OID);
    List allSnips = (List) allList.get(application);
    return allSnips != null ? allSnips.size() : 0;
  }

  public List storageAll(String applicationOid) {
    List all = (List) allList.get(applicationOid);
    return all;
  }

  public List storageAll() {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    return storageAll(applicationOid);
  }

  // Finder methods
  // Those should not be called
  // MemorySnipStorage should be wrapped with
  // a Query Storage (e.g. QuerySnipStorage or
  // a future XPathSnipStorage)
  public class MethodNotSupportedException extends RuntimeException {
    public MethodNotSupportedException(String s) {
      super(s);
    }
  };

  public List storageByHotness(int size) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByUser(String login) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByDateSince(Timestamp date) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByRecent(String applicationOid, int size) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByComments(Snip parent) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByParent(Snip parent) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByParentNameOrder(Snip parent, int count) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByParentModifiedOrder(Snip parent, int count) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByDateInName(String nameSpace, String start, String end) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }
}
