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
import org.snipsnap.snip.SnipLink;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.*;

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
    cache = serialized;
  }

  public Attachments() {
    cache = "";
  }

  private Map attachments = null;

  public Attachment addAttachment(String name, String contentType, int size, String location) {
    if (null == attachments) deserialize();
    Attachment attachment = new Attachment(name, contentType, size, new Date(), location);
    attachments.put(name, attachment);
    return attachment;
  }

  public Attachment getAttachment(String name) {
    if (null == attachments) deserialize();
    return (Attachment) attachments.get(name);
  }

  public void removeAttachment(String name, boolean destroy) {
    if (null == attachments) deserialize();
    Attachment attachment = (Attachment) attachments.get(name);
    attachments.remove(name);
  }

  public Iterator iterator() {
    if (null == attachments) deserialize();
    return attachments.values().iterator();
  }

  public Collection getAll() {
    if (null == attachments) deserialize();
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

  private final SAXBuilder saxBuilder = new SAXBuilder();

  private void deserialize() {
    attachments = new HashMap();

    Document attXml = new Document();
    try {
      attXml = saxBuilder.build(new StringReader("<" + ATTACHMENTS + ">" + cache + "</" + ATTACHMENTS + ">"));
      Element root = attXml.getRootElement();
      Iterator it = root.getChildren().iterator();
      while (it.hasNext()) {
        Element attElement = (Element) it.next();
        String name = attElement.getChildText(NAME);
        String contentType = attElement.getChildText(CONTENTTYPE);
        int size = Integer.parseInt(attElement.getChildTextTrim(SIZE));
        Date date = new Date(Long.parseLong(attElement.getChildTextTrim(DATE)));
        String location = attElement.getChildTextTrim(LOCATION);
        attachments.put(name, new Attachment(name, contentType, size, new Date(), location));
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
      attElement.addContent(new Element(LOCATION).addContent(attachment.getLocation()));
      root.add(attElement);
    }
    return cache = xmlOutputter.outputString(root);
  }

  public String toString() {
    return serialize();
  }
}
