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

import java.io.StringReader;
import java.util.Map;

/**
 * A snip data serializer that can store snip data in XML format.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipDataSerializer extends SerializerSupport implements Serializer {
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

  /**
   * Special serialize method that does not have dependencies on regular Snip
   * or other implementations used throughout snipsnap. This is necessary to
   * be able to dump a database with a small utility.
   *
   * @param snipMap a map containing the snips data
   * @return an element that can be serializes as XML
   */
  public Element serialize(Map snipMap) {
    Element snipElement = DocumentHelper.createElement(SNIP);

    snipElement.addElement(SNIP_NAME).addText((String) snipMap.get(SNIP_NAME));
    snipElement.addElement(SNIP_OUSER).addText(notNull(snipMap.get(SNIP_OUSER)));
    snipElement.addElement(SNIP_CUSER).addText(notNull(snipMap.get(SNIP_CUSER)));
    snipElement.addElement(SNIP_MUSER).addText(notNull(snipMap.get(SNIP_MUSER)));
    snipElement.addElement(SNIP_CTIME).addText(notNull(snipMap.get(SNIP_CTIME)));
    snipElement.addElement(SNIP_MTIME).addText(notNull(snipMap.get(SNIP_MTIME)));
    snipElement.addElement(SNIP_PERMISSIONS).addText(notNull(snipMap.get(SNIP_PERMISSIONS)));
    snipElement.add(addCDATAContent(SNIP_BACKLINKS, (String) snipMap.get(SNIP_BACKLINKS)));
    snipElement.add(addCDATAContent(SNIP_SNIPLINKS, (String) snipMap.get(SNIP_SNIPLINKS)));
    snipElement.add(addCDATAContent(SNIP_LABELS, (String)snipMap.get(SNIP_LABELS)));
    snipElement.add(addXMLContent(SNIP_ATTACHMENTS, notNull(snipMap.get(SNIP_ATTACHMENTS))));
    snipElement.addElement(SNIP_VIEWCOUNT).addText(notNull(snipMap.get(SNIP_VIEWCOUNT)));
    snipElement.add(addCDATAContent(SNIP_CONTENT, (String) snipMap.get(SNIP_CONTENT)));
    snipElement.addElement(SNIP_VERSION).addText(notNull(snipMap.get(SNIP_VERSION)));
    snipElement.addElement(SNIP_APPLICATION).addText(notNull(snipMap.get(SNIP_APPLICATION)));

    // TODO deprecated
    snipElement.addElement(SNIP_PARENT).addText(notNull(snipMap.get(SNIP_PARENT)));
    snipElement.addElement(SNIP_COMMENTED).addText(notNull(snipMap.get(SNIP_COMMENTED)));

    return snipElement;
  }

  /**
   * Add an element whose content is put into a cdata section
   * @param elementName the element to be created
   * @param content the content as a string
   * @return the newly created element
   */
  private Element addCDATAContent(String elementName, String content) {
    Element element = DocumentHelper.createElement(elementName);
    if (null == content || "".equals(content)) {
      return element;
    }
    element.addCDATA(content);
    return element;
  }

  /**
   * Create an element whose content is also xml to make sure it is not
   * escaped in the output
   * @param elementName name of the new element
   * @param content the actual xml as a string
   * @return the serialized element
   */
  private Element addXMLContent(String elementName, String content) {
    if (null != content && !"".equals(content)) {
      try {
        StringReader stringReader = new StringReader(content);
        SAXReader saxReader = new SAXReader();
        Document doc = saxReader.read(stringReader);
        return doc.getRootElement();
      } catch (Exception e) {
        Logger.warn("SnipSerializer: unable to add xml content: " + e);
        e.printStackTrace();
      }
    }
    return DocumentHelper.createElement(elementName);
  }
}
