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
import org.snipsnap.snip.storage.query.Query;
import org.snipsnap.snip.storage.query.QueryKit;
import org.snipsnap.user.Permissions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * SnipStorage backend that uses files for persisting data. This storage
 * has limitations in the snip name length and possibly characters as well
 * since not all filesystems can store UTF-8 file names.
 * 
 * TODO: optimize file system usage by keeping a consistent map of files
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class FileSnipStorage implements SnipStorage, CacheableStorage {
  private Map cache = new HashMap();

  private final static String SNIP_NAME = "Name";
  private final static String SNIP_CTIME = "CTime";
  private final static String SNIP_MTIME = "MTime";
  private final static String SNIP_CUSER = "CUser";
  private final static String SNIP_MUSER = "MUser";
  private final static String SNIP_PARENT = "ParentSnip";
  private final static String SNIP_COMMENTED = "CommentedSnip";
  private final static String SNIP_PERMISSIONS = "Permissions";
  private final static String SNIP_OUSER = "Owner";
  private final static String SNIP_BACKLINKS = "BackLinks";
  private final static String SNIP_SNIPLINKS = "SnipLinks";
  private final static String SNIP_LABELS = "Labels";
  private final static String SNIP_ATTACHMENTS = "Attachments";
  private final static String SNIP_VIEWCOUNT = "ViewCount";

  private final static String SNIP_FILE_PROPERTIES = "content.info";
  private final static String SNIP_FILE_CONTENT = "content.txt";

  public static void createStorage() {
    // there is nothing to create here, all handled dynamically
  }

  public FileSnipStorage() {
  }

  public void setCache(Map cache) {
    this.cache = cache;
  }

  public int storageCount() {
    Application app = Application.get();
    File fileStore = new File(app.getConfiguration().getFilePath());
    return traverseFileStore(fileStore, new ArrayList()).size();
  }

  public List storageAll() {
    Application app = Application.get();
    File fileStore = new File(app.getConfiguration().getFilePath());
    return traverseFileStore(fileStore, new ArrayList());
  }

  private List traverseFileStore(File root, List list) {
    try {
      Snip snip = createSnip(root);
      if (snip != null) {
        list.add(snip);
      }
    } catch (IOException e) {
      // ignored because empty dirs may exist
    }

    File[] files = root.listFiles();
    for (int entry = 0; entry < files.length; entry++) {
      if (files[entry].isDirectory()) {
        traverseFileStore(files[entry], list);
      }
    }
    return list;
  }

  public List storageByHotness(int size) {
    List all = storageAll();
    Collections.sort(all, new Comparator() {
      public int compare(Object object, Object object1) {
        Snip snipA = (Snip) object;
        Snip snipB = (Snip) object1;
        return snipA.getViewCount() - snipB.getViewCount();
      }
    });
    return all.subList(0, size);
  }

  public List storageByUser(final String login) {
    return QueryKit.query(storageAll(), new Query() {
      public boolean fit(Object object) {
        return login.equals(((Snip) object).getCUser());
      }
    });
  }

  public List storageByDateSince(final Timestamp date) {
    return QueryKit.query(storageAll(), new Query() {
      public boolean fit(Object object) {
        return date.before(((Snip) object).getMTime());
      }
    });
  }

  public List storageByRecent(int size) {
    List all = storageAll();
    Collections.sort(all, new Comparator() {
      public int compare(Object object, Object object1) {
        Snip snipA = (Snip) object;
        Snip snipB = (Snip) object1;
        return (int) (snipA.getMTime().getTime() - snipB.getMTime().getTime());
      }
    });
    return all.subList(0, size);
  }

  public List storageByComments(final Snip parent) {
    return QueryKit.query(storageAll(), new Query() {
      public boolean fit(Object object) {
        return parent.equals(((Snip) object).getCommentedSnip());
      }
    });
  }

  public List storageByParent(final Snip parent) {
    return QueryKit.query(storageAll(), new Query() {
      public boolean fit(Object object) {
        return parent.equals(((Snip) object).getParent());
      }
    });
  }

  public List storageByParentNameOrder(Snip parent, int count) {
    // TODO implement this
    return new ArrayList();
  }

  public List storageByParentModifiedOrder(Snip parent, int count) {
    // TODO implement this
    return new ArrayList();
  }

  public List storageByDateInName(String start, String end) {
    // TODO implement this
    return new ArrayList();
  }

  // Basic manipulation methods Load,Store,Create,Remove
  public Snip[] match(String pattern) {
    //@TODO implement this with LIKE
    return new Snip[]{};
  }

  public Snip[] match(String start, String end) {
    //@TODO implement this with LIKE
    return new Snip[]{};
  }

  public Snip storageLoad(String name) {
    //System.out.println("storageLoad('" + name + "')");

    Application app = Application.get();
    long start = app.start();
    Snip snip = null;

    File fileStore = new File(app.getConfiguration().getFilePath());
    File snipDir = new File(fileStore, name);

    try {
      snip = createSnip(snipDir);
    } catch (IOException e) {
      Logger.log("unable to load snip", e);
    }
    app.stop(start, "storageLoad - " + name);
    return snip;
  }

  public void storageStore(Snip snip) {
    //System.out.println("storageLoad('" + snip.getName() + "')");

    Application app = Application.get();
    File fileStore = new File(app.getConfiguration().getFilePath());
    File snipDir = new File(fileStore, snip.getName());
    storeSnip(snip, snipDir);
  }

  public Snip storageCreate(String name, String content) {
    Application app = Application.get();
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

    storageStore(snip);
    return (Snip) Aspects.newInstance(snip, Snip.class);
  }


  public void storageRemove(Snip snip) {
    Application app = Application.get();
    File fileStore = new File(app.getConfiguration().getFilePath());
    File snipDir = new File(fileStore, snip.getName());

    File snipPropsFile = new File(snipDir, SNIP_FILE_PROPERTIES);
    File snipFile = new File(snipDir, SNIP_FILE_CONTENT);

    if (snipFile.exists()) {
      File backup = new File(snipFile.getPath() + ".removed");
      snipFile.renameTo(backup);
    }

    if (snipPropsFile.exists()) {
      File backup = new File(snipPropsFile.getPath() + ".removed");
      snipPropsFile.renameTo(backup);
    }
  }

  private synchronized Snip createSnip(File snipDir) throws IOException {
    File snipPropsFile = new File(snipDir, SNIP_FILE_PROPERTIES);
    File snipFile = new File(snipDir, SNIP_FILE_CONTENT);

    if (!snipPropsFile.exists()) {
      return null;
    }

    Properties snipProps = new Properties();
    snipProps.load(new FileInputStream(snipPropsFile));

    String name = snipProps.getProperty(SNIP_NAME);
    //System.out.println("createSnip('" + name + "')");
    if (cache.containsKey(name.toUpperCase())) {
      return (Snip) cache.get(name.toUpperCase());
    }

    StringBuffer content = new StringBuffer();
    if (snipFile.exists()) {
      BufferedReader snipReader = new BufferedReader(new FileReader(snipFile));
      char[] buffer = new char[8192];
      int length = 0;
      while ((length = snipReader.read(buffer)) != -1) {
        content.append(buffer, 0, length);
      }
    }
    Snip snip = SnipFactory.createSnip(name, content.toString());

    snip.setCTime(new Timestamp(Long.parseLong(snipProps.getProperty(SNIP_CTIME))));
    snip.setMTime(new Timestamp(Long.parseLong(snipProps.getProperty(SNIP_MTIME))));
    snip.setCUser(snipProps.getProperty(SNIP_CUSER));
    snip.setMUser(snipProps.getProperty(SNIP_MUSER));
    String parentName = snipProps.getProperty(SNIP_PARENT);
    if (parentName.trim().length() != 0) {
      snip.setParentName(parentName);
    }
    String commentedName = snipProps.getProperty(SNIP_COMMENTED);
    if (commentedName.trim().length() != 0) {
      snip.setCommentedName(commentedName);
    }
    snip.setPermissions(new Permissions(snipProps.getProperty(SNIP_PERMISSIONS)));
    snip.setBackLinks(new Links(snipProps.getProperty(SNIP_BACKLINKS)));
    snip.setSnipLinks(new Links(snipProps.getProperty(SNIP_SNIPLINKS)));
    snip.setLabels(new Labels(snipProps.getProperty(SNIP_LABELS)));
    snip.setAttachments(new Attachments(snipProps.getProperty(SNIP_ATTACHMENTS)));
    snip.setViewCount(Integer.parseInt(snipProps.getProperty(SNIP_VIEWCOUNT)));

    // Aspects.setTarget(proxy, snip);
    // return proxy;
    snip = (Snip) Aspects.newInstance(snip, Snip.class);
    cache.put(name.toUpperCase(), snip);
    return snip;
  }

  private void storeSnip(Snip snip, File snipDir) {
    if (!snipDir.exists()) {
      snipDir.mkdirs();
    }

    File snipPropsFile = new File(snipDir, "content.info");
    File snipFile = new File(snipDir, "content.txt");

    if (snipPropsFile.exists()) {
      Logger.log("FileSnipStorage: backing up " + snipPropsFile.getPath());
      File backup = new File(snipPropsFile.getPath() + ".bck");
      snipPropsFile.renameTo(backup);
    }

    if (snipFile.exists()) {
      Logger.log("FileSnipStorage: backing up " + snipFile.getPath());
      File backup = new File(snipFile.getPath() + ".bck");
      snipFile.renameTo(backup);
    }

    Properties snipProps = new Properties();
    snipProps.setProperty(SNIP_NAME, notNull(snip.getName()));
    snipProps.setProperty(SNIP_CTIME, "" + snip.getCTime().getTime());
    snipProps.setProperty(SNIP_MTIME, "" + snip.getMTime().getTime());
    snipProps.setProperty(SNIP_CUSER, notNull(snip.getCUser()));
    snipProps.setProperty(SNIP_MUSER, notNull(snip.getMUser()));
    Snip parent = snip.getParent();
    snipProps.setProperty(SNIP_PARENT, null == parent ? "" : parent.getName());
    Snip comment = snip.getCommentedSnip();
    snipProps.setProperty(SNIP_COMMENTED, null == comment ? "" : comment.getName());
    snipProps.setProperty(SNIP_PERMISSIONS, snip.getPermissions().toString());
    snipProps.setProperty(SNIP_OUSER, notNull(snip.getOUser()));
    snipProps.setProperty(SNIP_BACKLINKS, snip.getBackLinks().toString());
    snipProps.setProperty(SNIP_SNIPLINKS, snip.getSnipLinks().toString());
    snipProps.setProperty(SNIP_LABELS, snip.getLabels().toString());
    snipProps.setProperty(SNIP_ATTACHMENTS, snip.getAttachments().toString());
    snipProps.setProperty(SNIP_VIEWCOUNT, "" + snip.getViewCount());
    try {
      snipProps.store(new FileOutputStream(snipPropsFile), "Properties for " + snip.getName());
    } catch (IOException e) {
      Logger.log("FileSnipStorage: unable to store properties for '" + snip.getName() + "'");
    }

    PrintWriter snipWriter = null;
    try {
      snipWriter = new PrintWriter(new BufferedWriter(new FileWriter(snipFile)));
      snipWriter.print(snip.getContent());
    } catch (IOException e) {
      Logger.log("FileSnipStorage: unable to store snip " + snip.getName(), e);
    } finally {
      snipWriter.flush();
      snipWriter.close();
    }
  }

  private String notNull(String str) {
    return str == null ? "" : str;
  }
}
