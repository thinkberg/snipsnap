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

import org.snipsnap.jdbc.Loader;
import org.snipsnap.app.Application;

import java.util.HashMap;
import java.util.Map;
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

  private Cache() {
    caches = new HashMap();
    loaders = new HashMap();
  }

  public void setLoader(Class type, Loader loader) {
    loaders.put(type, loader);
    if (!caches.containsKey(type)) {
      caches.put(type, new HashMap());
    }
  }

  public void put(Class type, String name, Object snip) {
    Map cache = (Map) caches.get(type);
    if (null != cache) {
      cache.put(name, snip);
    }
    return;
  }

  public boolean contains(Class type, String name) {
    Map cache = (Map) caches.get(type);
    if (null != cache) {
      return cache.containsKey(name);
    } else {
      return false;
    }
  }

  public Object get(Class type, String name) {
    Map cache = (Map) caches.get(type);
    if (null != cache) {
      return cache.get(name);
    } else {
      return null;
    }
  }

  public void remove(Class type, String name) {
    Map cache = (Map) caches.get(type);
    if (null != cache) {
      cache.remove(name);
    }
    return;
  }

  public Object load(Class type, String name) {
    Map cache = (Map) caches.get(type);
    Object object = null;
    if (contains(type, name)) {
      object = get(type, name);
    } else {
      Loader loader = (Loader) loaders.get(type);
      object = loader.loadObject(name);
      if (null != object) {
        cache.put(name, object);
      }
    }
    return object;
  }
}
