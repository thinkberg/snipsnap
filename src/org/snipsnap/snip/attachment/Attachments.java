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

package org.snipsnap.snip.attachment;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.snip.attachment.storage.AttachmentStorage;
import snipsnap.api.snip.SnipLink;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class for grouping and managing attachments for a snip
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class Attachments {

  private String cache = null;
  private final static Comparator attSorter = new Comparator() {
    public int compare(Object o1, Object o2) {
      return ((Attachment) o1).getName().compareTo(((Attachment) o2).getName());
    }
  };

  /**
   * Initialize Attachments object with a serialized string.
   */
  public Attachments(String serialized) {
    if (null != serialized) {
      if (serialized.startsWith("<" + ATTACHMENTS + ">")) {
        cache = serialized;
      } else {
        cache = "<" + ATTACHMENTS + ">" + serialized + "</" + ATTACHMENTS + ">";
      }
    }
  }

  public Attachments(Element serialized) {
    cache = toString(serialized);
  }

  public Attachments() {
    cache = "";
  }

  private Map attachments = null;

  public Attachment addAttachment(Attachment attachment) {
    deserialize();
    attachments.put(attachment.getName(), attachment);
    return attachment;
  }

  public Attachment addAttachment(String name, String contentType, long size, String location) {
    Attachment attachment = new Attachment(name, contentType, size, new Date(), location);
    addAttachment(attachment);
    return attachment;
  }

  public Attachment getAttachment(String name) {
    deserialize();
    return (Attachment) attachments.get(name);
  }

  public void removeAttachment(String name) {
    deserialize();
    Attachment attachment = (Attachment) attachments.get(name);
    if (attachment != null) {
      removeAttachment(attachment);
    }
  }

  public void removeAttachment(Attachment attachment) {
    deserialize();
    attachments.remove(attachment.getName());
  }

  public Iterator iterator() {
    return getAll().iterator();
  }

  public List getAll() {
    deserialize();
    List list = new ArrayList(attachments.values());
    Collections.sort(list, attSorter);
    return list;
  }

  public boolean isEmpty() {
    deserialize();
    return null == attachments || attachments.size() == 0;
  }

  public final static String ATTACHMENTS = "attachments";
  public final static String ATTACHMENT = "attachment";

  public final static String NAME = "name";
  public final static String CONTENTTYPE = "content-type";
  public final static String SIZE = "size";
  public final static String DATE = "date";
  public final static String LOCATION = "location";

  /**
   * Deserialize the attachments from the database string.
   * TODO: synchronized due to race conditions while starting up
   */
  private synchronized void deserialize() {
    if (null == attachments) {
      attachments = new HashMap();
      if (null != cache && !"".equals(cache)) {
        Document attXml;
        try {
          SAXReader saxReader = new SAXReader();
          attXml = saxReader.read(new StringReader(cache));
          Element root = attXml.getRootElement();
          Iterator it = root.elementIterator(ATTACHMENT);
          while (it.hasNext()) {
            Element attElement = (Element) it.next();
            try {
              String name = attElement.element(NAME).getText();
              String contentType = attElement.element(CONTENTTYPE).getTextTrim();
              int size = Integer.parseInt(attElement.element(SIZE).getTextTrim());
              Date date = new Date(Long.parseLong(attElement.element(DATE).getTextTrim()));
              String location = attElement.element(LOCATION).getTextTrim();
              attachments.put(name, new Attachment(name, contentType, size, date, location));
            } catch (Exception e) {
              Logger.warn("Attachments: ignoring attachment: " + attElement);
            }
          }
        } catch (Exception e) {
          Logger.warn("Attachments: unable to deserialize: '" + cache + "'");
        }
      }
    }
  }

  private String serialize() {
    if (null == attachments) {
      return cache;
    }

    Element attElement = DocumentHelper.createElement(ATTACHMENTS);
    Iterator it = attachments.values().iterator();
    while (it.hasNext()) {
      Attachment attachment = (Attachment) it.next();
      Element attachmentNode = attElement.addElement(ATTACHMENT);
      attachmentNode.addElement(NAME).addText(attachment.getName());
      attachmentNode.addElement(CONTENTTYPE).addText(attachment.getContentType());
      attachmentNode.addElement(SIZE).addText("" + attachment.getSize());
      attachmentNode.addElement(DATE).addText("" + attachment.getDate().getTime());
      attachmentNode.addElement(LOCATION).addText(attachment.getLocation());
    }

    return cache = toString(attElement);
  }

  private String toString(Element attElement) {
    OutputFormat outputFormat = OutputFormat.createCompactFormat();
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      XMLWriter xmlWriter = new XMLWriter(out, outputFormat);
      xmlWriter.write(attElement);
      xmlWriter.flush();
    } catch (IOException e) {
      Logger.warn("Attachments: unable to serialize", e);
    }
    
    try {
      String enc = Application.get().getConfiguration().getEncoding();
      return out.toString(enc == null ? "UTF-8" : enc);
    } catch (UnsupportedEncodingException e) {
      return out.toString();
    }
  }

  public String getLinks(String name) {
    AttachmentStorage storage = (AttachmentStorage) Components.getComponent(AttachmentStorage.class);

    StringBuffer tmp = new StringBuffer();
    Iterator it = iterator();
    while (it.hasNext()) {
      Attachment att = (Attachment) it.next();
      if (storage.exists(att)) {
        tmp.append(snipsnap.api.snip.SnipLink.createLink(SnipLink.getSpaceRoot() + "/" + snipsnap.api.snip.SnipLink.encode(name), att.getName(), att.getName()));
        tmp.append(" (").append(att.getSize()).append(")");
        if (it.hasNext()) {
          tmp.append("<br/> ");
        }
      } else {
        Logger.log(Logger.WARN, att.getLocation() + " is missing");
      }
    }
    return tmp.toString();
  }

  public Attachments copy(String name) {
    AttachmentStorage storage = (AttachmentStorage) Components.getComponent(AttachmentStorage.class);
    Attachments copy = new Attachments();
    List atts = getAll();
    Iterator attsIt = atts.iterator();
    while (attsIt.hasNext()) {
      Attachment oldAtt = (Attachment) attsIt.next();
      Attachment att = copy.addAttachment(oldAtt.getName(), oldAtt.getContentType(), oldAtt.getSize(), oldAtt.getLocation());

      try {
        storage.copy(oldAtt, att);
      } catch (IOException e) {
        copy.removeAttachment(att);
      }
    }
    return copy;
  }

  public String toString() {
    return serialize();
  }
}
