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

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.jdbc.Loader;

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
  private Map cache;
  private Loader loader;

  public Cache(Loader loader) {
    cache = new HashMap();
    this.loader = loader;
  }

  public void put(String name, Snip snip) {
    cache.put(name, snip);
    return;
  }

  public boolean contains(String name) {
    return cache.containsKey(name);
  }

  public Snip get(String name) {
    return (Snip) cache.get(name);
  }

  public void remove(String name) {
    cache.remove(name);
    return;
  }

  public Snip load(String name) {
    Snip snip = null;
    if (contains(name)) {
      snip = get(name);
    } else {
      snip = loader.storageLoad(name);
      if (null != snip) {
        cache.put(name, snip);
      }
    }
    return snip;
  }
}
