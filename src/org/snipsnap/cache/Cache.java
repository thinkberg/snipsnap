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

import org.snipsnap.snip.storage.Storage;
import org.snipsnap.snip.storage.query.Query;
import org.snipsnap.snip.Snip;
import org.snipsnap.app.Application;

import java.util.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * CacheManager for caching Objects (Snips).
 *
 * @author stephan
 * @version $Id$
 */
public class Cache {
  private Map caches;
  private Map loaders;

  public static Cache instance;
  private static Object monitor = new Object();

  public static Cache getInstance() {
    synchronized (monitor) {
      if (null == instance) {
        instance = new Cache();
      }
    }
    return instance;
  }

  public static void removeInstance() {
    synchronized (monitor) {
      if(null != instance) {
        instance = null;
      }
    }
  }

  private Cache() {
    caches = new HashMap();
    loaders = new HashMap();
  }

  public void setLoader(Class type, Storage loader) {
    loaders.put(type, loader);
    if (!caches.containsKey(type)) {
      caches.put(type, new HashMap());
    }
  }

  public void put(Class type, String name, Object snip) {
    Map cache = (Map) caches.get(type);
    if (null != cache) {
      cache.put(name.toUpperCase(), snip);
    }
    return;
  }

  public boolean contains(Class type, String name) {
    Map cache = (Map) caches.get(type);
    if (null != cache) {
      return cache.containsKey(name.toUpperCase());
    } else {
      return false;
    }
  }

  public List getCache(Class type) {
    //@TODO optimize to use value list, too
    Map m = (Map) caches.get(type);
    Iterator iterator = m.keySet().iterator();
    while (iterator.hasNext()) {
      String name = (String) iterator.next();
      Snip snip = (Snip) m.get(name);
      System.err.println(name+"="+snip.getName());
    }

    return new ArrayList(((Map) caches.get(type)).values());
  }


  public List querySorted(Comparator c, int size, Class type) {
    ArrayList result = new ArrayList(getCache(type));
    Collections.sort(result, c);
    return result.subList(0, Math.min(size, result.size()));
  }

  public List querySorted(Query query, Comparator c, Class type) {
    List result = query(query, type);
    Collections.sort(result, c);
    return result;
  }

  public List querySorted(Query query, Comparator c, int size, Class type) {
    List result = query(query, type);
    Collections.sort(result, c);
    return result.subList(0, Math.min(size, result.size()));
  }

  public List query(Query query, Class type) {
    Iterator iterator = getCache(type).iterator();
    List result = new ArrayList();
    while (iterator.hasNext()) {
      Object object = iterator.next();
      if (query.fit(object)) {
        result.add(object);
      }
    }
    return result;
  }

  public Object get(Class type, String name) {
    Map cache = (Map) caches.get(type);
    if (null != cache) {
      return cache.get(name.toUpperCase());
    } else {
      return null;
    }
  }

  public void remove(Class type, String name) {
    Map cache = (Map) caches.get(type);
    if (null != cache) {
      cache.remove(name.toUpperCase());
    }
    return;
  }

  public Object load(Class type, String name) {
    Map cache = (Map) caches.get(type);
    Object object = null;
    if (contains(type, name)) {
      object = get(type, name);
    } else {
      Storage loader = (Storage) loaders.get(type);
      object = loader.loadObject(name);
      if (null != object) {
        put(type, name, object);
      }
    }
    return object;
  }
}
