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

import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.interceptor.Aspects;
import org.snipsnap.snip.Links;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipFactory;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.Permissions;
import org.snipsnap.util.ApplicationAwareMap;
import org.snipsnap.versioning.VersionInfo;
import org.snipsnap.versioning.VersionStorage;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * SnipStorage backend that uses files for persisting data. This storage
 * has limitations in the snip name length and possibly characters as well
 * since not all filesystems can store UTF-8 file names.
 *
 * This Storage navigates to the correct directory. Sub classes should
 * then persist the snip to one or several files.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public abstract class FileSnipStorage implements CacheableStorage, VersionStorage, SnipStorage {
  public static final String NOT_SUPPORTED_EXCEPTION_MSG =
    "Method not supported, do not call FileSnipStorage directly";

  protected ApplicationAwareMap cache;

  private SnipSerializer serializer = SnipSerializer.getInstance();

  // Cacheable Storage
  public void setCache(ApplicationAwareMap cache) {
    this.cache = cache;
  }

  /**
   * Return the directory where all snips are stored
   *
   * @return
   */
  public File getWorkingDir() {
    Application app = Application.get();
    return new File(app.getConfiguration().getFileStore(), "snips");
  }

  /**
   * Return the directory where all snips are stored
   *
   * @param applicationOid
   * @return
   */
  public File getWorkingDir(String applicationOid) {
    Application app = Application.get();
    return app.getConfiguration().getFilePath(applicationOid);
  }

  public void storageRemove(Snip snip) {
    File snipDir = new File(getWorkingDir(), snip.getName());
    storageRemoveFile(snip, snipDir);
  }

  /**
   * Remove one or more files from the given directory.
   * Subclasses should employ strategies like backup etc.
   *
   * @param snip Snip to remove
   * @param snipDir Directory with the stored snips
   */
  protected abstract void storageRemoveFile(Snip snip, File snipDir);

  protected abstract Map createSnipFromFile(File snipDir) throws IOException;

  /**
   * Load the snip from a directory. Navigate to
   * the correct directory then load and create
   * the snip
   *
   * @param name Name of Snip to load
   * @return
   */
  public Snip storageLoad(String name) {
    Snip snip = null;
    File snipDir = new File(getWorkingDir(), name);
    try {
      snip = parseSnip(createSnipFromFile(snipDir));
    } catch (IOException e) {
      Logger.log("unable to load snip", e);
    }
    return snip;
  }

  // VersionStorage

  /**
   * Store a version of the snip to the
   * file system with the given directory
   *
   * @param snip
   * @param versionDir
   */
  protected abstract void storeVersion(Snip snip, File versionDir);


  /**
   * Stora a version of a snip in the storage.
   * Navigates to a directory for the version
   * files, then calls storeVersion() which is
   * implemented in subclasses.
   *
   * @param snip Snip to store
   */
  public void storeVersion(Snip snip) {
    File snipDir = new File(getWorkingDir(), snip.getName());
    File versionDir = new File(snipDir, "version");
    storeVersion(snip, versionDir);
  }

  /**
   * Return a list of VersionInfo objects for the
   * given snip. Objects should be ordered by decreasing version.
   * Navigates to the directory and scans for versions of snips
   *
   * @param snip Snip for which the revision should be loaded
   * @return
   */
  public List getVersionHistory(Snip snip) {
    File snipDir = new File(getWorkingDir(), snip.getName());
    File versionDir = new File(snipDir, "version");
    List versions = getVersionHistory(snip, versionDir);
    return versions;
  }

  /**
   * Return the checker object for this file storage.
   * Depends on the filename that is used by the
   * storage to store snip versions.
   *
   * @return
   */
  protected abstract VersionFileNameChecker getVersionFileNameChecker();

  /**
   * Read snips from the directory and return a list of
   * VersionInfo objects.
   *
   * @param snip Snip to get version history
   * @param versionDir Directory to read versions from
   * @return
   */

  protected List getVersionHistory(Snip snip, File versionDir) {
    VersionFileNameChecker checker = getVersionFileNameChecker();

    String[] files = versionDir.list(checker);

//    System.out.println("Version files="+Arrays.asList(files));
    List versions = new ArrayList();
    try {
      for (int i = 0; i < files.length; i++) {
        String fileName = files[i];
        int version = checker.getVersion(fileName);
        Map map = loadVersion(snip, versionDir, version);
        VersionInfo info = new VersionInfo();
        info.setVersion(version);
        info.setViewCount(Integer.parseInt((String) map.get(SnipSerializer.SNIP_VIEWCOUNT)));
        info.setMUser((String) map.get(SnipSerializer.SNIP_MUSER));
        info.setMTime(new Timestamp(Long.parseLong((String) map.get(SnipSerializer.SNIP_MTIME))));
        info.setSize(((String) map.get(SnipSerializer.SNIP_CONTENT)).length());
        versions.add(info);
      }
    } catch (Exception e) {
      Logger.log("TwoFileSnipStorage: unable read version history of snip" + snip.getName(), e);
    }
    Collections.sort(versions, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((VersionInfo) o1).getVersion() > ((VersionInfo) o2).getVersion() ? -1 : 1;
      }
    });

    return versions;
  }

  /**
   * Load a version of a snip from the file system
   * and the given directory
   *
   * @param snip
   * @param versionDir
   * @return
   */
  protected abstract Map loadVersion(Snip snip, File versionDir, int version) throws IOException;

  /**
   * Load a version of a snip from the storage
   *
   * @param snip Example of a snip to load
   * @param version Version number
   * @return
   */
  public Snip loadVersion(Snip snip, int version) {
    File snipDir = new File(getWorkingDir(), snip.getName());
    File versionDir = new File(snipDir, "version");
    String name = snip.getName();
    try {
      Snip newSnip = SnipFactory.createSnip(name, "");                                                
      return serializer.deserialize(loadVersion(snip, versionDir, version), newSnip);
    } catch (IOException e) {
      Logger.log("FileSnipStorage: Unable to load version snip " + snip.getName() + " " + version);
    }
    return null;
  }

  /**
   * Store a snip to a given directory. Subclasses should
   * implement this and store the snip to one or more files.
   *
   * @param snip Snip to store
   * @param snipDir Directory to store the snip in
   */
  protected abstract void storeSnip(Snip snip, File snipDir);

  /**
   * Stors a version of a snip in the storage.
   * Navigates to a directory then calls storeSnip() from
   * a subclass to store the snip.
   *
   * @param snip Snip to store
   */
  public void storageStore(Snip snip) {
    File snipDir = new File(getWorkingDir(), snip.getName());
    storeSnip(snip, snipDir);
  }

  public Snip storageCreate(String name, String content) {
    Application app = Application.get();
    String applicationOid = (String) app.getObject(Application.OID);
    String login = app.getUser().getLogin();

    Snip snip = SnipFactory.createSnip(name, content);
    Timestamp cTime = new Timestamp(new java.util.Date().getTime());
    Timestamp mTime = (Timestamp) cTime.clone();
    snip.setCTime(cTime);
    snip.setMTime(mTime);
    snip.setCUser(login);
    snip.setMUser(login);
    snip.setOUser(login);
    snip.setPermissions(new Permissions());
    snip.setBackLinks(new Links());
    snip.setSnipLinks(new Links());
    snip.setLabels(new Labels());
    snip.setAttachments(new Attachments());
    snip.setApplication(applicationOid);
    storageStore(snip);
    return (Snip) Aspects.newInstance(snip, Snip.class);
  }

  private Snip parseSnip(Map snipMap) {
    // the application oid is a special for file snip storage
    String applicationOid = (String) snipMap.get(SnipSerializer.SNIP_APPLICATION);
    String name = (String) snipMap.get(SnipSerializer.SNIP_NAME);
    if (cache.getMap(applicationOid).containsKey(name.toUpperCase())) {
      return (Snip) cache.getMap(applicationOid).get(name.toUpperCase());
    }

    Snip newSnip = SnipFactory.createSnip(name, (String) snipMap.get(SnipSerializer.SNIP_CONTENT));
    Snip snip = serializer.deserialize(snipMap, newSnip);

    // Aspects.setTarget(proxy, snip);
    // return proxy;
    snip = (Snip) Aspects.newInstance(snip, Snip.class);
    cache.getMap(applicationOid).put(name.toUpperCase(), snip);
    return snip;
  }

  // SnipStorage
  public List storageAll() {
    String applicationOid = (String) Application.get().getObject(Application.OID);
    return storageAll(applicationOid);
  }

  public int storageCount() {
    Application app = Application.get();
    File fileStore = new File(app.getConfiguration().getFileStore());
    return traverseFileStore(fileStore, new ArrayList()).size();
  }

  public List storageAll(String applicationOid) {
    return traverseFileStore(getWorkingDir(applicationOid), new ArrayList());
  }

  private List traverseFileStore(File root, List list) {
    try {
      Map map = createSnipFromFile(root);
      if (null != map) {
        list.add(parseSnip(map));
      }
    } catch (IOException e) {
      // ignored because empty dirs may exist
    }

    File[] files = root.listFiles();
    for (int entry = 0; files != null && entry < files.length; entry++) {
      if (files[entry].isDirectory()) {
        traverseFileStore(files[entry], list);
      }
    }
    return list;
  }

  /**
   * Not implemented, should be handled on a higher level
   * e.g. QuerySnipStorage
   */
  public List storageByHotness(int size) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByUser(final String login) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByDateSince(final Timestamp date) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByRecent(String applicationOid, int size) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG+" : storageByRecent(applicationOid,size)");
  }

  public List storageByComments(final Snip parent) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public List storageByParent(final Snip parent) {
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

  public Snip[] match(String pattern) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public Snip[] match(String start, String end) {
    throw new MethodNotSupportedException(NOT_SUPPORTED_EXCEPTION_MSG);
  }

  public class MethodNotSupportedException extends RuntimeException {
    public MethodNotSupportedException(String s) {
      super(s);
    }
  }
}
