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

import org.dom4j.Element;
import org.radeox.util.logging.Logger;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SerializerSupport {

  /**
   * Produce a map with all elements and its values found below the top element.
   * @param el the root element
   * @return the map with names and values
   */
  public Map getElementMap(Element el) {
    Map elements = new HashMap();
    Iterator childIterator = el.elementIterator();
    while (childIterator.hasNext()) {
      Element element = (Element) childIterator.next();
      if (element.isTextOnly()) {
        elements.put(element.getName(), notNull(element.getText()));
      } else {
        elements.put(element.getName(), element);
      }
    }
    return elements;
  }

  /**
   * Create a timestamp out of a string value.
   * @param value the string value
   * @return the timestamp
   */
  protected Timestamp getTimestamp(String value) {
    if(null != value && !"".equals(value)) {
      try {
        return new Timestamp(Long.parseLong(value));
      } catch (NumberFormatException e) {
        Logger.warn("SerializerSupport: timestamp value invalid: "+value);
      }
    }
    return null;
  }

  /**
   * Create a string representation of a timestamp.
   * @param ts the timestamp
   * @return the string representation
   */
  protected String getStringTimestamp(Timestamp ts) {
    if(null != ts) {
      return "" + ts.getTime();
    }
    return "";
  }

  /**
   * Make sure a string is not null but rather an empty string
   * @param str a value
   * @return the string
   */
  protected String notNull(String str) {
    return str == null ? "" : str;
  }

  /**
   * Make sure the object is not null but rather an empty string or
   * convert the object to string.
   * @param obj the object
   * @return the string value or ""
   */
  protected String notNull(Object obj) {
    return obj == null ? "" : obj.toString();
  }

}
