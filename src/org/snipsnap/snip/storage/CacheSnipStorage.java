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
import org.snipsnap.util.ApplicationAwareMap;

import java.sql.Timestamp;
import java.util.List;

/**
 * Wrapper with caching for loading snips
 *
 * DOES NOT WORK!
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class CacheSnipStorage implements SnipStorage, CacheStorage {
  private ApplicationAwareMap cache;
  private SnipStorage storage;

  public CacheSnipStorage(SnipStorage storage) {
    this.storage = storage;
  }

  // Basic manipulation methods Load,Store,Create,Remove
  public Snip[] match(String pattern) {
    return storage.match(pattern);
  }

  public Snip[] match(String start, String end) {
    return storage.match(start, end);
  }

  // Basic manipulation methods Load,Store,Create,Remove
  public Snip storageLoad(String name) {
    Snip snip;
    if (cache.getMap().containsKey(name)) {
      snip = (Snip) cache.getMap().get(name);
    } else {
      snip = storage.storageLoad(name);
      cache.getMap().put(snip.getName(), snip);
    }
    return snip;
  }

  public ApplicationAwareMap getCache() {
    return cache;
  }

  public Object loadObject(String name) {
    return storageLoad(name);
  }

  public void storageStore(Snip snip) {
    storage.storageStore(snip);
  }

  public void storageStore(List snips) {
    storage.storageStore(snips);
  }

  public Snip storageCreate(String name, String content) {
    Snip snip = storage.storageCreate(name, content);
    cache.getMap().put(snip.getName(), snip);
    return snip;
  }

  public void storageRemove(Snip snip) {
    cache.getMap().remove(snip);
    storage.storageRemove(snip);
  }

  // Finder methods
  public int storageCount() {
    return storage.storageCount();
  }

  public List storageAll(String applicationOid) {
    return storage.storageAll(applicationOid);
  }

  public List storageAll() {
    return storage.storageAll();
  }

  public List storageByHotness(int size) {
    return storage.storageByHotness(size);
  }

  public List storageByUser(String login) {
    return storage.storageByUser(login);
  }

  public List storageByDateSince(Timestamp date) {
    return storage.storageByDateSince(date);
  }

  public List storageByRecent(String applicationOid, int size) {
    return storage.storageByRecent(applicationOid, size);
  }

  public List storageByComments(Snip parent) {
    return storage.storageByComments(parent);
  }

  public List storageByParent(Snip parent) {
    return storage.storageByParent(parent);
  }

  public List storageByParentNameOrder(Snip parent, int count) {
    return storage.storageByParentNameOrder(parent, count);
  }

  public List storageByParentModifiedOrder(Snip parent, int count) {
    return storage.storageByParentModifiedOrder(parent, count);
  }

  public List storageByDateInName(String nameSpace, String start, String end) {
    return storage.storageByDateInName(nameSpace, start, end);
  }
}