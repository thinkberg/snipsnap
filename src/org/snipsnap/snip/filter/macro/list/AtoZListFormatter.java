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

import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.filter.macro.ListoutputMacro;
import org.snipsnap.util.Nameable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Formats a list as AtoZ listing separated by the alphabetical characters.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class AtoZListFormatter implements ListoutputMacro.ListFormatter {

  /**
   * Create a simple list.
   */
  public void format(StringBuffer buffer, String listComment, Collection c, String emptyText) {
    if (c.size() > 0) {
      Iterator it = c.iterator();
      Map atozMap = new HashMap();
      List numberRestList = new ArrayList();
      List otherRestList = new ArrayList();
      while (it.hasNext()) {
        Nameable nameable = (Nameable) it.next();
        String name = nameable.getName();
        String indexChar = name.substring(0, 1).toUpperCase();

        if (indexChar.charAt(0) >= 'A' && indexChar.charAt(0) <= 'Z') {
          if (!atozMap.containsKey(indexChar)) {
            atozMap.put(indexChar, new ArrayList());
          }
          List list = (List) atozMap.get(indexChar);
          list.add(name);
        } else if (indexChar.charAt(0) >= '0' && indexChar.charAt(0) <= '9') {
          numberRestList.add(name);
        } else {
          otherRestList.add(name);
        }
      }

      buffer.append("<table width=\"100%\" class=\"index-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
      for(int idxChar = 'A'; idxChar <= 'Z'; idxChar++) {
        buffer.append("<tr class=\"index-table-header\">");
        for(int i = 0; i < 5 && idxChar + i <= 'Z'; i++) {
          String ch = "" + (char)(idxChar + i);
          buffer.append("<td><b> &nbsp;<a href=\"#idx"+ch+"\">").append(ch).append("</a></b></td>");
          buffer.append("<td>...</td><td>");
          buffer.append(atozMap.get(ch) == null ? 0 : ((List)atozMap.get(ch)).size());
          buffer.append("&nbsp; </td>");
        }
        idxChar += 5;
        if(idxChar >= 'Z') {
          buffer.append("<td></td></td></td><td></td><td></td>");
          buffer.append("<td><b> &nbsp;<a href=\"#idx@\">@</a></b></td>");
          buffer.append("<td>...</td><td>");
          buffer.append(numberRestList.size()).append("&nbsp; </td>");
          buffer.append("<td><b> &nbsp;<a href=\"#idx0-9\">0-9</a></b></td>");
          buffer.append("<td>...</td><td>");
          buffer.append(otherRestList.size()).append("&nbsp; </td>");
        }
        buffer.append("</tr>");

      }
      buffer.append("</table>");

      buffer.append("<b>").append(listComment).append("(").append(c.size()).append("):</b>");
      buffer.append("<table width=\"100%\" class=\"index-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
      for (int ch = 'A'; ch <= 'Z'; ch += 2) {
        String left = "" + (char) ch;
        String right = "" + (char) (ch + 1);

        insertCharHeader(buffer, left, right);
        addRows(buffer, (List) atozMap.get(left), (List) atozMap.get(right));
      }
      insertCharHeader(buffer, "0-9", "@");
      addRows(buffer, numberRestList, otherRestList);
      buffer.append("</table>");
    } else {
      buffer.append(emptyText);
    }
  }

  private void addRows(StringBuffer buffer, List listLeft, List listRight) {
    Iterator leftIt = listLeft != null ? listLeft.iterator() : new EmptyIterator();
    Iterator rightIt = listRight != null ? listRight.iterator() : new EmptyIterator();

    while (leftIt.hasNext() || rightIt.hasNext()) {
      String leftName = (String) (leftIt != null && leftIt.hasNext() ? leftIt.next() : null);
      String rightName = (String) (rightIt != null && rightIt.hasNext() ? rightIt.next() : null);
      insertRow(buffer, leftName, rightName, false);
    }
  }

  private void insertCharHeader(StringBuffer buffer, String leftHeader, String rightHeader) {
    buffer.append("<tr class=\"index-table-header\"><td>");
    buffer.append("<b><a name=\"idx").append(leftHeader).append("\"></a>").append(leftHeader);
    buffer.append("</b></td><td> </td><td>");
    buffer.append("<b><a name=\"idx").append(rightHeader).append("\"></a>").append(rightHeader);
    buffer.append("</b></td></tr>");
  }

  private void insertRow(StringBuffer buffer, String left, String right, boolean odd) {
    buffer.append("<tr><td>");
    if (left != null) {
      SnipLink.appendLink(buffer, left);
    }
    buffer.append("</td><td> </td><td>");
    if (right != null) {
      SnipLink.appendLink(buffer, right);
    }
    buffer.append("</td></tr>");
  }

  private class EmptyIterator implements Iterator {
    public boolean hasNext() {
      return false;
    }

    public Object next() {
      return null;
    }

    public void remove() {
    }
  }
}
