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

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.serialization.Appendable;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
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

public class Attachments implements Appendable {

  private String cache = null;

  /**
   * Initialize Attachments object with a serialized string.
   */
  public Attachments(String serialized) {
    cache = serialized;
  }

  public Attachments() {
    cache = "";
  }

  private Map attachments = null;

  public Attachment addAttachment(String name, String contentType, long size, File location) {
    if (null == attachments) deserialize();
    Attachment attachement = new Attachment(name, contentType, size, new Date(), location);
    attachments.put(name, attachement);
    return attachement;
  }

  public Attachment getAttachment(String name) {
    if (null == attachments) deserialize();
    return (Attachment) attachments.get(name);
  }

  public void removeAttachment(String name, boolean destroy) {
    if (null == attachments) deserialize();
    Attachment attachment = (Attachment) attachments.get(name);
    if (destroy) {
      attachment.destroy();
    }
    attachments.remove(name);
  }

  public Iterator iterator() {
    if (null == attachments) deserialize();
    return attachments.keySet().iterator();
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

  private final SAXBuilder saxBuilder = new SAXBuilder();

  private void deserialize() {
    attachments = new HashMap();

    AppConfiguration config = Application.get().getConfiguration();
    // todo make this a configuration setting
    File fileStore = new File(config.getFile().getParentFile().getParentFile(), "images");

    Document attXml = new Document();
    try {
      attXml = saxBuilder.build(new StringReader("<" + ATTACHMENTS + ">" + cache + "</" + ATTACHMENTS + ">"));
      Element root = attXml.getRootElement();
      Iterator it = root.getChildren().iterator();
      while (it.hasNext()) {
        Element attElement = (Element) it.next();
        String name = attElement.getChildText(NAME);
        String contentType = attElement.getChildText(CONTENTTYPE);
        long size = Long.parseLong(attElement.getChildTextTrim(SIZE));
        Date date = new Date(Long.parseLong(attElement.getChildTextTrim(DATE)));
        String location = attElement.getChildTextTrim(LOCATION);
        File file = new File(fileStore, location);
        if (!file.exists()) {
          System.err.println("Attachments: missing attached file '" + file + "'");
        }
        attachments.put(name, new Attachment(name, contentType, size, new Date(), new File(fileStore, location)));
      }
    } catch (Exception e) {
      System.err.println("Attachments: unable to deserialize: " + cache);
    }
  }

  private final XMLOutputter xmlOutputter = new XMLOutputter();

  private String serialize() {
    if (null == attachments) return cache;

    List root = new ArrayList();
    Iterator it = attachments.values().iterator();
    while (it.hasNext()) {
      Attachment attachment = (Attachment) it.next();
      Element attElement = new Element(ATTACHMENT);
      attElement.addContent(new Element(NAME).addContent(attachment.getName()));
      attElement.addContent(new Element(CONTENTTYPE).addContent(attachment.getContentType()));
      attElement.addContent(new Element(SIZE).addContent("" + attachment.getSize()));
      attElement.addContent(new Element(DATE).addContent("" + attachment.getDate().getTime()));
      attElement.addContent(new Element(LOCATION).addContent(attachment.getFile().getName()));
      root.add(attElement);
    }
    return cache = xmlOutputter.outputString(root);
  }

  public String toString() {
    return serialize();
  }

  public String getListString() {
    if (null == attachments) deserialize();
    if (attachments.size() > 0) {
      StringBuffer tmp = new StringBuffer();
      tmp.append("<div class=\"attachments\">");
      Iterator it = attachments.values().iterator();
      while (it.hasNext()) {
        Attachment att = (Attachment) it.next();
        tmp.append(att.getName());
        tmp.append(" (");
        tmp.append(att.getSize());
        tmp.append(")");
        if (it.hasNext()) {
          tmp.append(", ");
        }
      }
      tmp.append("</div>");
      return tmp.toString();
    }
    return "";
  }

  public Writer appendTo(Writer s) throws IOException {
    s.write(getListString());
    return s;
  }
}
