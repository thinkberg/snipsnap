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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
  private final static String SNIP_XML = "snip.xml";

  private static DocumentBuilder documentBuilder;

  private Map cache = new HashMap();
  private SnipSerializer serializer = SnipSerializer.getInstance();

  public static void createStorage() {
  }

  public FileSnipStorage() throws ParserConfigurationException {
    documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
    long start = app.start();
    List all = traverseFileStore(fileStore, new ArrayList());
    app.stop(start, "storageAll()");
    return all;
  }

  private List traverseFileStore(File root, List list) {
    // it is unclear whether the name of the directory is correct on every file system
    Snip snip = createSnip(root.getName(), root);
    if (snip != null) {
      list.add(snip);
    }

    File[] files = root.listFiles();
    for (int entry = 0; files != null && entry < files.length; entry++) {
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

    snip = createSnip(name, snipDir);
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

    File snipFile = new File(snipDir, SNIP_XML);

    if (snipFile.exists()) {
      File backup = new File(snipFile.getPath() + ".removed");
      snipFile.renameTo(backup);
    }
  }

  private synchronized Snip createSnip(String name, File snipDir) {
    if (cache.containsKey(name.toUpperCase())) {
      return (Snip) cache.get(name.toUpperCase());
    }

    return createSnip(snipDir);
  }

  private synchronized Snip createSnip(File snipDir) {
    File snipFile = new File(snipDir, SNIP_XML);

    if (snipFile.exists()) {
      try {
        Document snipDocument = documentBuilder.parse(new BufferedInputStream(new FileInputStream(snipFile)));
        Snip snip = serializer.deserialize(snipDocument.getFirstChild(), SnipFactory.createSnip("", ""));
        snip = (Snip) Aspects.newInstance(snip, Snip.class);
        cache.put(snip.getName().toUpperCase(), snip);
        return snip;
      } catch (SAXException e) {
        Logger.log("FileSnipStorage: unable to parse " + snipFile, e);
      } catch (IOException e) {
        Logger.log("FileSnipStorage: i/o exception while parsing " + snipFile, e);
      }
    }
    return null;
  }

  private void storeSnip(Snip snip, File snipDir) {
    if (!snipDir.exists()) {
      snipDir.mkdirs();
    }

    File snipFile = new File(snipDir, SNIP_XML);

    if (snipFile.exists()) {
      Logger.log("FileSnipStorage: backing up " + snipFile.getPath());
      File backup = new File(snipFile.getPath() + ".bck");
      snipFile.renameTo(backup);
    }

    Document snipDocument = documentBuilder.newDocument();
    snipDocument.appendChild(serializer.serialize(snipDocument, snip));

    try {
      BufferedOutputStream xmlStream = new BufferedOutputStream(new FileOutputStream(snipFile));
      StreamResult streamResult = new StreamResult(xmlStream);
      TransformerFactory tf = SAXTransformerFactory.newInstance();
      Transformer serializer = tf.newTransformer();
      serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
      serializer.setOutputProperty(OutputKeys.INDENT, "yes");
      serializer.transform(new DOMSource(snipDocument), streamResult);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
