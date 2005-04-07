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
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.radeox.util.logging.Logger;
import snipsnap.api.container.Components;
import org.snipsnap.jdbc.IntHolder;
import org.snipsnap.snip.storage.SnipSerializer;
import org.snipsnap.snip.storage.UserSerializer;
import snipsnap.api.user.User;
import org.snipsnap.versioning.VersionInfo;
import org.snipsnap.versioning.VersionManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import snipsnap.api.snip.*;
import snipsnap.api.snip.Snip;

/**
 * Helper class for exporting Snips and users as XML document.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class XMLSnipExport {

  private static ThreadLocal instance = new ThreadLocal() {
    protected synchronized Object initialValue() {
      return new IntHolder(0);
    }
  };

  public static IntHolder getStatus() {
    return (IntHolder) instance.get();
  }

  public static void store(OutputStream out, List snips, List users, String filter, List ignoreElements, File fileStore) {
    try {
      OutputFormat outputFormat = new OutputFormat();
      outputFormat.setEncoding("UTF-8");
      outputFormat.setNewlines(true);
      XMLWriter xmlWriter = new XMLWriter(out, outputFormat);
      Element root = DocumentHelper.createElement("snipspace");
      xmlWriter.writeOpen(root);
      storeUsers(xmlWriter, users);
      storeSnips(xmlWriter, snips, filter, ignoreElements, fileStore);
      xmlWriter.writeClose(root);
      xmlWriter.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void storeUsers(XMLWriter xmlWriter, List users) throws IOException {
    if (users != null && users.size() > 0) {
      UserSerializer userSerializer = UserSerializer.getInstance();
      Iterator userListIterator = users.iterator();
      while (userListIterator.hasNext()) {
        User user = (User) userListIterator.next();
        xmlWriter.write(userSerializer.serialize(user));
        getStatus().inc();
      }
    }
  }

  private static void storeSnips(XMLWriter xmlWriter, List snips, String filter, List ignoreElements, File fileStore)
    throws IOException {
    if (snips != null && snips.size() > 0) {
      SnipSerializer snipSerializer = SnipSerializer.getInstance();

      Iterator snipListIterator = snips.iterator();
      while (snipListIterator.hasNext()) {
        snipsnap.api.snip.Snip snip = (Snip) snipListIterator.next();
        if (filter == null || !snip.getName().matches(filter)) {
          Element snipEl = snipSerializer.serialize(snip);
          if (null != ignoreElements) {
            Iterator filterIt = ignoreElements.iterator();
            while (filterIt.hasNext()) {
              String el = (String) filterIt.next();
              if (snipEl.element(el) != null) {
                snipEl.remove(snipEl.element(el));
              }
            }
          }
          storeAttachments(snipEl, fileStore);
          storeVersions(snipEl, snip);
          xmlWriter.write(snipEl);
        }
        getStatus().inc();
      }
    }
  }

  private static void storeVersions(Element snipEl, Snip snip) {
    SnipSerializer serializer = SnipSerializer.getInstance();

    VersionManager versionManager = (VersionManager) Components.getComponent(VersionManager.class);
    List snipVersions = versionManager.getHistory(snip);

    Element versionsElement = snipEl.addElement("versions");
    Iterator versionsIt = snipVersions.iterator();
    while (versionsIt.hasNext()) {
      VersionInfo versionInfo = (VersionInfo) versionsIt.next();
      Snip versionSnip = versionManager.loadVersion(snip, versionInfo.getVersion());
      Element versionSnipEl = serializer.serialize(versionSnip);
//      versionSnipEl.addAttribute("version", "" + versionInfo.getVersion());
      versionsElement.add(versionSnipEl);
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
