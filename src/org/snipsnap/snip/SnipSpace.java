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
package org.snipsnap.snip;

import org.apache.lucene.search.Hits;
import org.snipsnap.app.Application;
import org.snipsnap.cache.Cache;
import org.snipsnap.jdbc.Finder;
import org.snipsnap.jdbc.FinderFactory;
import org.snipsnap.jdbc.JDBCCreator;
import org.snipsnap.notification.Notification;
import org.radeox.filter.LinkTester;
import org.snipsnap.snip.storage.JDBCSnipStorage;
import org.snipsnap.snip.storage.MemorySnipStorage;
import org.snipsnap.snip.storage.SnipStorage;
import org.snipsnap.snip.storage.Storage;
import org.snipsnap.user.Digest;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;
import org.snipsnap.util.Queue;
import org.snipsnap.util.mail.PostDaemon;
import org.radeox.util.logging.Logger;
import org.snipsnap.xmlrpc.WeblogsPing;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

/**
 * SnipSpace handles all the data storage.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class SnipSpace implements LinkTester {
  private Map missing;
  private Queue changed;
  private List delayed;
  private Cache cache;
  private SnipIndexer indexer;
  private FinderFactory finders;
  private Timer timer, pop3Timer;
  private String eTag;
  private SnipStorage storage;

  private static SnipSpace instance;

  public static synchronized SnipSpace getInstance() {
    if (null == instance) {
      instance = new SnipSpace();
      instance.init();
    }
    return instance;
  }

  public static synchronized void removeInstance() {
    if (instance != null) {
      instance = null;
      Cache.removeInstance();
    }
  }

  private SnipSpace() {
  }

  private void init() {
    missing = new HashMap();
    changed = new Queue(100);
    cache = Cache.getInstance();
    storage = new JDBCSnipStorage(cache);
    finders = new FinderFactory("SELECT name, content, cTime, mTime, cUser, mUser, parentSnip, commentSnip, permissions, " +
        " oUser, backLinks, snipLinks, labels, attachments, viewCount " +
        " FROM Snip ", cache, Snip.class, "name", (JDBCCreator) storage);
    cache.setLoader(Snip.class, (Storage) storage);

    // Fully fill the cache with all Snips
    if ("full".equals(Application.get().getConfiguration().getCache())) {
      Finder finder = finders.getFinder();
      finder.execute();

      // If we keep all snips in memory we can use queries directly on the snip list
      storage = new MemorySnipStorage(storage, cache);
      cache.setLoader(Snip.class, (Storage) storage);
      Logger.debug("Cache strategy is: keep full, using MemorySnipStorage");
    }

    indexer = new SnipIndexer();
    delayed = new ArrayList();

    changed.fill(storage.storageByRecent(50));
    setETag();
    timer = new Timer();
    timer.schedule(new TimerTask() {
      public void run() {
        synchronized (delayed) {
          ListIterator iterator = delayed.listIterator();
          while (iterator.hasNext()) {
            Snip snip = (Snip) iterator.next();
            systemStore(snip);
            iterator.remove();
          }
        }
      }
      // execute after 5 minutes and then
      // every 5 minutes
    }, 5 * 60 * 1000, 5 * 60 * 1000);

    // getting this will trigger Postdaemon
    PostDaemon.getInstance();
  }

  public String getETag() {
    return "\"" + eTag + "\"";
  }

  // A snip is changed by the user (created, stored)
  public void changed(Snip snip) {
    changed.add(snip);
    setETag();
  }

  public void setETag() {
    eTag = Digest.getDigest(new java.util.Date().toString());
  }

  public int getSnipCount() {
    return storage.storageCount();
  }

  public List getChanged() {
    return getChanged(15);
  }

  public List getChanged(int count) {
    return changed.get(count);
  }

  public List getAll() {
    if ("full".equals(Application.get().getConfiguration().getCache())) {
      return cache.getCache(Snip.class);
    } else {
      return storage.storageAll();
    }
  }

  public List getSince(Timestamp date) {
    return storage.storageByDateSince(date);
  }

  public List getByDate(String start, String end) {
    return storage.storageByDateInName(start, end);
  }

  /**
   * A list of Snips, ordered by "hotness", currently
   * viewcount.
   *
   * @param count number of snips in the result
   * @return List of snips, ordered by hotness
   */
  public List getHot(int count) {
    return storage.storageByHotness(count);
  }

  public List getComments(Snip snip) {
    return storage.storageByComments(snip);
  }

  public List getByUser(String login) {
    return storage.storageByUser(login);
  }

  public List getChildren(Snip snip) {
    return storage.storageByParent(snip);
  }

  public List getChildrenDateOrder(Snip snip, int count) {
    return storage.storageByParentNameOrder(snip, count);
  }

  public List getChildrenModifiedOrder(Snip snip, int count) {
    return storage.storageByParentModifiedOrder(snip, count);
  }

  public void reIndex() {
    List snips = getAll();
    Iterator iterator = snips.iterator();
    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      indexer.reIndex(snip);
    }
  }

  public Hits search(String queryString) {
    return indexer.search(queryString);
  }

  public String getContent(String title, String content) {
    return content = "1 " + title + " {anchor:" + title + "}\n" + content;
  }

  public Snip post(String content, String title) {
    return post(getContent(title, content));
  }

  public Snip post(String content) {
    Date date = new Date(new java.util.Date().getTime());
    return post(content, date);
  }

  public Snip post(String content, Date date) {
    Snip start = load("start");
    return post(start, content, date);
  }

  public String getPostName() {
    Date date = new Date(new java.util.Date().getTime());
    return SnipUtil.toName(date);
  }

  public Snip post(Snip weblog, String content, Date date) {
    String name = SnipUtil.toName(date);
    Snip snip = null;
    if (exists(name)) {
      snip = load(name);
      snip.setContent(content + "\n\n" + snip.getContent());
    } else {
      snip = create(name, content);
    }
    snip.setParent(weblog);
    snip.addPermission(Permissions.EDIT, Roles.OWNER);
    store(snip);

    // Ping weblogs.com that we changed our site
    WeblogsPing.ping(weblog);
    return snip;
  }

  public boolean exists(String name) {
    if (missing.containsKey(name)) {
      return false;
    }

    if (null == load(name)) {
      missing.put(name, new Integer(0));
      return false;
    } else {
      return true;
    }
  }

  public Snip load(String name) {
    return (Snip) cache.load(Snip.class, name);
  }

  public void store(Snip snip) {
    Application app = Application.get();
    long start = app.start();
    snip.setMUser(app.getUser());
    changed(snip);
    snip.setMTime(new Timestamp(new java.util.Date().getTime()));
    storage.storageStore(snip);
    indexer.reIndex(snip);
    app.stop(start, "store - " + snip.getName());
    return;
  }

  /**
   * Method with with wich the system can store snips.
   * This methode does not change the mTime, the mUser,
   * reindex the snip or add the snip to the modified list
   *
   * @param snip The snip to store
   */
  public void systemStore(Snip snip) {
    //Logger.debug("systemStore - "+snip.getName());
    Application app = Application.get();
    long start = app.start();
    storage.storageStore(snip);
    indexer.reIndex(snip);
    app.stop(start, "systemStore - " + snip.getName());
    return;
  }


  /**
   * Delays the storage of a snip for some time. Some information
   * in a snip are changeg every view. To not store a snip every
   * time it is viewed, delay the store and wait until some changes
   * are cummulated. Should only be used, when the loss of the
   * changes is tolerable.
   *
   * @param snip Snip to delay for storage
   */
  public void delayedStrore(Snip snip) {
    //Logger.debug("delayedStore - "+snip.getName());
    Logger.log(Logger.DEBUG, "delayedStore");
    synchronized (delayed) {
      if (!delayed.contains(snip)) {
        delayed.add(snip);
      }
    }
  }

  public Snip create(String name, String content) {
    name = name.trim();
    Snip snip = storage.storageCreate(name, content);
    cache.put(Snip.class, name, snip);
    if (missing.containsKey(name)) {
      missing.remove(name);
    }
    changed(snip);
    indexer.index(snip);
    Application.get().notify(Notification.SNIP_CREATE, snip);
    return snip;
  }

  public void remove(Snip snip) {
    cache.remove(Snip.class, snip.getName());
    changed.remove(snip);
    storage.storageRemove(snip);
    indexer.removeIndex(snip);
    return;
  }

// SnipStorage System dependend Methods

}