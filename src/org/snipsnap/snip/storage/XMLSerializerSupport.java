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

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class XMLSerializerSupport {
  protected Timestamp getTimestamp(String t) {
    return new Timestamp(new Date(Long.parseLong(t)).getTime());
  }

  protected static Map getElements(Node snipNode) {
    NodeList children = snipNode.getChildNodes();
    Map elements = new HashMap();

    for (int i = 0; i < children.getLength(); i++) {
      Node node = children.item(i);
      String name = node.getNodeName();
      String value = null;
      NodeList cl = node.getChildNodes();
      for (int c = 0; c < cl.getLength(); c++) {
        if (cl.item(c).getNodeType() == Node.TEXT_NODE) {
          value = cl.item(c).getNodeValue();
        }
      }
      elements.put(name, value);
    }
    return elements;
  }
}
