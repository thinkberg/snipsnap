/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002,2003 Fraunhofer Gesellschaft
 * Fraunhofer Institut for Computer Architecture and Software Technology
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

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.radeox.util.logging.Logger;
import org.snipsnap.snip.Links;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.attachment.Attachments;
import org.snipsnap.snip.label.Labels;
import org.snipsnap.user.Permissions;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A snip serializer that can store and load snips in XML format.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipSerializer extends SerializerSupport {
  public final static String SNIP = "snip";

  public final static String SNIP_NAME = "name";
  public final static String SNIP_CONTENT = "content";
  public final static String SNIP_OUSER = "oUser";
  public final static String SNIP_CUSER = "cUser";
  public final static String SNIP_MUSER = "mUser";
  public final static String SNIP_CTIME = "cTime";
  public final static String SNIP_MTIME = "mTime";
  public final static String SNIP_PERMISSIONS = "permissions";
  public final static String SNIP_BACKLINKS = "backLinks";
  public final static String SNIP_SNIPLINKS = "snipLinks";
  public final static String SNIP_LABELS = "labels";
  public final static String SNIP_ATTACHMENTS = "attachments";
  public final static String SNIP_VIEWCOUNT = "viewCount";
  public final static String SNIP_VERSION = "version";
  public final static String SNIP_APPLICATION = "application";

  // TODO deprecated
  public final static String SNIP_COMMENTED = "commentSnip";
  public final static String SNIP_PARENT = "parentSnip";

  private static SnipSerializer serializer = null;

  /**
   * Get an instance of the snip serializer.
   * @return the serializer
   */
  public synchronized static SnipSerializer getInstance() {
    if (null == serializer) {
      serializer = new SnipSerializer();
    }
    return serializer;
  }

  protected SnipSerializer() {

  }

  /**
   * Store a snip in an XML node.
   * @param snip the snip to store
   * @return the serialized snip as XML
   */
  public Element serialize(Snip snip) {
//    System.out.println("SnipSerializer.serialize("+snip.getName()+")");
    Element snipElement = DocumentHelper.createElement(SNIP);
    snipElement.addElement(SNIP_NAME).addText(snip.getName());
    snipElement.addElement(SNIP_OUSER).addText(notNull(snip.getOUser()));
    snipElement.addElement(SNIP_CUSER).addText(notNull(snip.getCUser()));
    snipElement.addElement(SNIP_MUSER).addText(notNull(snip.getMUser()));
    snipElement.addElement(SNIP_CTIME).addText(getStringTimestamp(snip.getCTime()));
    snipElement.addElement(SNIP_MTIME).addText(getStringTimestamp(snip.getMTime()));
    snipElement.addElement(SNIP_PERMISSIONS).addText(snip.getPermissions().toString());
    snipElement.addElement(SNIP_BACKLINKS).addText(snip.getBackLinks().toString());
    snipElement.addElement(SNIP_SNIPLINKS).addText(snip.getSnipLinks().toString());
    snipElement.addElement(SNIP_LABELS).addText(snip.getLabels().toString());
    snipElement.add(addXMLContent(snip.getAttachments().toString(), SNIP_ATTACHMENTS));
    snipElement.addElement(SNIP_VIEWCOUNT).addText("" + snip.getViewCount());
    snipElement.addElement(SNIP_CONTENT).addText(snip.getContent());
    snipElement.addElement(SNIP_VERSION).addText("" + snip.getVersion());
    snipElement.addElement(SNIP_APPLICATION).addText(notNull(snip.getApplication()));

    // TODO deprecated
    Snip parentSnip = snip.getParent();
    snipElement.addElement(SNIP_PARENT).addText(parentSnip != null ? parentSnip.getName() : "");
    Snip commentedSnip = snip.getCommentedSnip();
    snipElement.addElement(SNIP_COMMENTED).addText(commentedSnip != null ? commentedSnip.getName() : "");

    return snipElement;
  }

  private Element addXMLContent(String content, String elementName) {
    if(null != content && !"".equals(content)) {
      try {
        StringReader stringReader = new StringReader(content);
        SAXReader saxReader = new SAXReader();
        Document doc = saxReader.read(stringReader);
        return doc.getRootElement();
      } catch (Exception e) {
        Logger.warn("SnipSerializer: unable to add xml content: "+e);
        e.printStackTrace();
      }
    }
    return DocumentHelper.createElement(elementName);
  }

  /**
   * Load snip from XML serialized file.
   * @param snipElement the XML node containing the snip
   * @return the modified snip
   */
  public Snip deserialize(Element snipElement, Snip snip) {
    Map snipMap = getElementMap(snipElement);
    return deserialize(snipMap, snip);
  }

  public Snip deserialize(Map snipMap, Snip snip) {
//    System.out.println("deserializing: "+snipMap.get(SNIP_NAME));
    Iterator elementIt = snipMap.keySet().iterator();
    while (elementIt.hasNext()) {
      String element = (String) elementIt.next();
      String value = "";
      Object elementValue= snipMap.get(element);
      if(elementValue instanceof String) {
        value = notNull((String) snipMap.get(element));
      }

      if (SNIP_NAME.equals(element)) {
//        System.out.println("SnipSerializer.deserialize(" + value + ")");
        snip.setName(value);
      } else if (SNIP_CTIME.equals(element)) {
        snip.setCTime(new Timestamp(Long.parseLong(value)));
      } else if (SNIP_MTIME.equals(element)) {
        snip.setMTime(new Timestamp(Long.parseLong(value)));
      } else if (SNIP_CUSER.equals(element)) {
        if (null == snip.getOUser() || "".equals(snip.getOUser())) {
          snip.setOUser(value);
        }
        snip.setCUser(value);
      } else if (SNIP_MUSER.equals(element)) {
        snip.setMUser(value);
      } else if (SNIP_OUSER.equals(element) && !"".equals(value)) {
        snip.setOUser(value);
      } else if (SNIP_PARENT.equals(element)) {
        if (value.trim().length() != 0) {
          snip.setParentName(value);
        }
      } else if (SNIP_COMMENTED.equals(element)) {
        if (value.trim().length() != 0) {
          snip.setCommentedName(value);
        }
      } else if (SNIP_PERMISSIONS.equals(element)) {
        snip.setPermissions(new Permissions(value));
      } else if (SNIP_BACKLINKS.equals(element)) {
        snip.setBackLinks(new Links(value));
      } else if (SNIP_SNIPLINKS.equals(element)) {
        snip.setSnipLinks(new Links(value));
      } else if (SNIP_LABELS.equals(element)) {
        snip.setLabels(new Labels(snip, value));
      } else if (SNIP_ATTACHMENTS.equals(element)) {
        if(elementValue instanceof Element) {
          snip.setAttachments(new Attachments((Element)elementValue));
        } else {
          snip.setAttachments(new Attachments(value));
        }
      } else if (SNIP_VIEWCOUNT.equals(element)) {
        snip.setViewCount(Integer.parseInt(value));
      } else if (SNIP_CONTENT.equals(element)) {
        snip.setContent(value);
      } else if (SNIP_VERSION.equals(element)) {
        snip.setVersion(Integer.parseInt(value));
      } else if (SNIP_APPLICATION.equals(element)) {
        snip.setApplication(value);
      } else {
        Logger.warn("unknown entry in serialized snip: "+element+"='"+value+"'");
      }
    }
    return snip;
  }

  public Map createSnipMap(Snip snip) {
    Map snipMap = new HashMap();
    snipMap.put(SNIP_NAME, notNull(snip.getName()));
    snipMap.put(SNIP_VERSION, "" + snip.getVersion());
    snipMap.put(SNIP_CTIME, getStringTimestamp(snip.getCTime()));
    snipMap.put(SNIP_MTIME, getStringTimestamp(snip.getMTime()));
    snipMap.put(SNIP_CUSER, notNull(snip.getCUser()));
    snipMap.put(SNIP_MUSER, notNull(snip.getMUser()));
    snipMap.put(SNIP_PERMISSIONS, snip.getPermissions().toString());
    snipMap.put(SNIP_OUSER, notNull(snip.getOUser()));
    snipMap.put(SNIP_BACKLINKS, snip.getBackLinks().toString());
    snipMap.put(SNIP_SNIPLINKS, snip.getSnipLinks().toString());
    snipMap.put(SNIP_LABELS, snip.getLabels().toString());
    snipMap.put(SNIP_ATTACHMENTS, snip.getAttachments().toString());
    snipMap.put(SNIP_VIEWCOUNT, "" + snip.getViewCount());
    snipMap.put(SNIP_APPLICATION, notNull(snip.getApplication()));

    // TODO deprecated
    Snip parent = snip.getParent();
    snipMap.put(SNIP_PARENT, null == parent ? "" : parent.getName());
    Snip comment = snip.getCommentedSnip();
    snipMap.put(SNIP_COMMENTED, null == comment ? "" : comment.getName());
    return snipMap;
  }
}
