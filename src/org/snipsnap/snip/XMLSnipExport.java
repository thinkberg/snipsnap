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

import org.apache.xmlrpc.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.radeox.util.logging.Logger;
import org.snipsnap.snip.storage.SnipSerializer;
import org.snipsnap.snip.storage.UserSerializer;
import org.snipsnap.user.User;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

/**
 * Helper class for exporting Snips and users as XML document.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class XMLSnipExport {
  private static void store(OutputStream out, Document exportDocument) {
    try {
      OutputFormat outputFormat = new OutputFormat();
      outputFormat.setEncoding("UTF-8");
      outputFormat.setNewlines(true);
      XMLWriter xmlWriter = new XMLWriter(out, outputFormat);
      xmlWriter.write(exportDocument);
      xmlWriter.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Stores a list of users and/or snips into the stream in XML format.
   * @param out the output stream to store the xml data in
   * @param snips the list of snips to store
   * @param users the list of users to store
   */
  public static void store(OutputStream out, List snips, List users, File fileStore) {
    Document exportDocument = store(users, snips, null, fileStore);
    store(out, exportDocument);
  }

  /**
   * Stores a list of users and/or snips into the stream in XML format.
   * @param out the output stream to store the xml data in
   * @param snips the list of snips to store
   * @param users the list of users to store
   * @param filter a regex filter to ignore snips
   */
  public static void store(OutputStream out, List snips, List users, String filter, File fileStore) {
    Document exportDocument = store(users, snips, filter, fileStore);
    store(out, exportDocument);
  }

  public static void store(OutputStream out, List snips, List users, String filter, List ignoreElements, File fileStore) {
    Document exportDocument = store(users, snips, filter, ignoreElements, fileStore);
    store(out, exportDocument);
  }

  public static Document store(List users, List snips, String filter, File fileStore) {
    return store(users, snips, filter, null, fileStore);
  }

  /**
   * Store a list of users and snips into an XML document.
   * @param users the users to store
   * @param snips the snips to store
   * @return the XML document
   */
  public static Document store(List users, List snips, String filter, List ignoreElements, File fileStore) {
    Document backupDoc = DocumentHelper.createDocument();
    Element root = backupDoc.addElement("snipspace");

    storeUsers(root, users);
    storeSnips(root, snips, filter, ignoreElements, fileStore);

    return backupDoc;
  }

  private static void storeUsers(Element root, List users) {
    if (users != null && users.size() > 0) {
      UserSerializer userSerializer = UserSerializer.getInstance();
      Iterator userListIterator = users.iterator();
      while (userListIterator.hasNext()) {
        User user = (User) userListIterator.next();
        root.add(userSerializer.serialize(user));
      }
    }
  }

  private static void storeSnips(Element root, List snips, String filter, List ignoreElements, File fileStore) {
    if (snips != null && snips.size() > 0) {
      SnipSerializer snipSerializer = SnipSerializer.getInstance();
      Iterator snipListIterator = snips.iterator();
      while (snipListIterator.hasNext()) {
        Snip snip = (Snip) snipListIterator.next();
        if(filter == null || !snip.getName().matches(filter)) {
          Element snipEl = snipSerializer.serialize(snip);
          if(null != ignoreElements) {
            Iterator filterIt = ignoreElements.iterator();
            while (filterIt.hasNext()) {
              String el = (String) filterIt.next();
              if(snipEl.element(el) != null) {
                snipEl.remove(snipEl.element(el));
              }
            }
          }
          storeAttachments(snipEl, fileStore);
          root.add(snipEl);
        }
      }
    }
  }

  private static void storeAttachments(Element snipEl, File attRoot) {
    Element attachmentsEl = snipEl.element("attachments");
    Iterator attIt = attachmentsEl.elements("attachment").iterator();
    while (attIt.hasNext()) {
      Element att = (Element) attIt.next();
      try {
        addAttachmentFile(att, new File(attRoot, att.elementText("location")));
      } catch (Exception e) {
        Logger.fatal("unable to export attachment: " + e);
        e.printStackTrace();
      }
    }
  }

  public static void addAttachmentFile(Element att, File attFile) throws IOException {
    ByteArrayOutputStream data = new ByteArrayOutputStream();
    BufferedInputStream fileIs = new BufferedInputStream(new FileInputStream(attFile));
    int count = 0;
    byte[] buffer = new byte[8192];
    while ((count = fileIs.read(buffer)) != -1) {
      data.write(buffer, 0, count);
    }
    data.close();
    att.addElement("data").addText(new String(Base64.encode(data.toByteArray()), "UTF-8"));
  }
}
