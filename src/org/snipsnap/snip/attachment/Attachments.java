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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Class for grouping and managing attachments for a snip
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class Attachments {

  private String cache = null;

  /**
   * Initialize Attachments object with a serialized string.
   */
  public Attachments(String serialized) {
    if(null != serialized) {
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

  public Attachment addAttachment(String name, String contentType, long size, String location) {
    if (null == attachments) {
      deserialize();
    }
    Attachment attachment = new Attachment(name, contentType, size, new Date(), location);
    attachments.put(name, attachment);
    return attachment;
  }

  public Attachment getAttachment(String name) {
    if (null == attachments) {
      deserialize();
    }
    return (Attachment) attachments.get(name);
  }

  public void removeAttachment(String name) {
    if (null == attachments) {
      deserialize();
    }
    Attachment attachment = (Attachment) attachments.get(name);
    if (attachment != null) {
      removeAttachment(attachment);
    }
  }

  public void removeAttachment(Attachment attachment) {
    if (null == attachments) {
      deserialize();
    }
    attachments.remove(attachment.getName());
  }

  public Iterator iterator() {
    if (null == attachments) {
      deserialize();
    }
    return attachments.values().iterator();
  }

  public Collection getAll() {
    if (null == attachments) {
      deserialize();
    }
    return attachments.values();
  }

  public boolean empty() {
    return null == attachments || attachments.size() == 0;
  }

  private final static String ATTACHMENTS = "attachments";
  private final static String ATTACHMENT = "attachment";

  private final static String NAME = "name";
  private final static String CONTENTTYPE = "content-type";
  private final static String SIZE = "size";
  private final static String DATE = "date";
  private final static String LOCATION = "location";

  private void deserialize() {
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
    outputFormat.setEncoding("UTF-8");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      XMLWriter xmlWriter = new XMLWriter(out, outputFormat);
      xmlWriter.write(attElement);
      xmlWriter.flush();
    } catch (IOException e) {
      System.err.println("Attachments: unable to serialize: " + e);
    }
    return out.toString();
  }

  public String toString() {
    return serialize();
  }
}
