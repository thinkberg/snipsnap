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
package org.snipsnap.snip.filter.macro.list;

import org.snipsnap.snip.filter.macro.ListoutputMacro;

import java.util.Collection;

/**
 * Formats a list as AtoZ listing separated by the alphabetical characters.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AtoZListFormatter implements ListoutputMacro.ListFormatter {

  private final static String[] atoz = new String[]{
    "0-9", "N",
    "A", "O",
    "B", "P",
    "C", "Q",
    "D", "R",
    "E", "S",
    "F", "T",
    "G", "U",
    "H", "V",
    "I", "W",
    "J", "X",
    "K", "Y",
    "L", "Z",
    "M", "@"
  };

  /**
   * Create a simple list.
   */
  public void format(StringBuffer buffer, String listComment, Collection c, String emptyText) {
    if (c.size() > 0) {
      /*     for (int i = 0; i < atoz.length; i += 2) {
             Iterator leftIt = Collections.
             while(
             buffer.append("<blockquote>");
             Iterator nameIterator = c.iterator();
             while (nameIterator.hasNext()) {
               Nameable nameable = (Nameable) nameIterator.next();
               SnipLink.appendLink(buffer, nameable.getName());
               if (nameIterator.hasNext()) {
                 buffer.append(", ");
               }
             }
             buffer.append("</blockquote>");*/

    } else {
      buffer.append(emptyText);
    }
  }


  private void insertCharHeader(StringBuffer buffer, String leftHeader, String rightHeader) {
    buffer.append("<tr class=\"snip-table-header\"><td>");
    buffer.append(leftHeader);
    buffer.append("</td><td>");
    buffer.append(rightHeader);
    buffer.append("</td></tr>");
  }

  private void insertRow(StringBuffer buffer, String left, String right, boolean odd) {
    buffer.append("<tr class=\"snip-table");
    buffer.append(odd ? "-odd" : "-even");
    buffer.append("\"><td>");
    buffer.append(left);
    buffer.append("</td><td>");
    buffer.append(right);
    buffer.append("</td></tr>");
  }
}
