/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * --LICENSE NOTICE--
 */
package org.snipsnap.render.macro.list;

import org.radeox.util.Linkable;
import org.radeox.util.Nameable;
import org.radeox.util.i18n.ResourceManager;

import java.io.IOException;
import java.io.Writer;
import java.text.CollationKey;
import java.text.Collator;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 * Formats a list as AtoZ listing separated by the alphabetical characters.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class CollationListFormatter implements ListFormatter {
  public String getName() {
    return "collator";
  }

  private String removeParents(String name) {
    int index = name.lastIndexOf("/");
    if (-1 == index) {
      return name;
    } else if (name.length() == index + 1) {
      return name.substring(0, index);
    } else {
      return name.substring(index + 1);
    }
  }

  /**
   * Create a listing of entries sorted using a collator initialized in the
   * locale provided.
   */
  public void format(Writer writer, Linkable current, String listComment, Collection c, String emptyText, boolean showSize)
    throws IOException {
    Collator collator = Collator.getInstance(ResourceManager.getLocale("radeox_messages"));

    if (c.size() > 0) {
      Iterator it = c.iterator();
      Map atozMap = new TreeMap();
      while (it.hasNext()) {
        Object object = it.next();
        String name, indexChar;
        if (object instanceof Nameable) {
          name = ((Nameable) object).getName();
        } else {
          name = object.toString();
        }
        String finalName = removeParents(name);
        indexChar = finalName.substring(0, 1).toUpperCase();
        if (object instanceof Linkable) {
          name = ((Linkable) object).getLink();
        }

        CollationKey cKey = collator.getCollationKey(indexChar);

        if (!atozMap.containsKey(cKey)) {
          atozMap.put(cKey, new TreeMap());
        }
        Map list = (Map) atozMap.get(cKey);
        list.put(finalName, name);
      }

      writer.write("<table width=\"100%\" class=\"index-top\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
      writer.write("<colgroup width='5.5%' span='18'/>");
      Iterator collatorIt = atozMap.keySet().iterator();
      while(collatorIt.hasNext()) {
        writer.write("<tr>");
        for(int i = 0; i < 6 && collatorIt.hasNext(); i++) {
          CollationKey cKey = (CollationKey) collatorIt.next();
          String ch = cKey.getSourceString();
          writer.write("<th><b> &nbsp;<a href=\"");
          writer.write(current.getLink());
          writer.write("#idx" + Integer.toHexString(ch.charAt(0)) + "\">");
          writer.write(ch);
          writer.write("</a></b></th>");
          writer.write("<th>...</th><th>");
          writer.write("" + (atozMap.get(cKey) == null ? 0 : ((Map) atozMap.get(cKey)).size()));
          writer.write("&nbsp; </th>");
        }
        writer.write("</tr>");
      }
      writer.write("</table>");

      writer.write("<div class=\"list-title\">");
      writer.write(listComment);
      if (showSize) {
        writer.write(" (");
        writer.write("" + c.size());
        writer.write(")");
      }
      writer.write("</div>");
      writer.write("<table width=\"100%\" class=\"index\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
      collatorIt = atozMap.keySet().iterator();
      while(collatorIt.hasNext()) {
        CollationKey leftKey = (CollationKey)collatorIt.next();
        CollationKey rightKey = collatorIt.hasNext() ? (CollationKey)collatorIt.next() : null;

        insertCharHeader(writer,
                         leftKey.getSourceString(),
                         rightKey != null ? rightKey.getSourceString() : null);
        addRows(writer,
                (Map) atozMap.get(leftKey),
                rightKey != null ? (Map) atozMap.get(rightKey) : null);
      }
      writer.write("</table>");
    } else {
      writer.write(emptyText);
    }
  }

  private void addRows(Writer writer, Map listLeft, Map listRight) throws IOException {
    Iterator leftIt = listLeft != null ? listLeft.values().iterator() : new EmptyIterator();
    Iterator rightIt = listRight != null ? listRight.values().iterator() : new EmptyIterator();

    while (leftIt.hasNext() || rightIt.hasNext()) {
      String leftName = (String) (leftIt != null && leftIt.hasNext() ? leftIt.next() : null);
      String rightName = (String) (rightIt != null && rightIt.hasNext() ? rightIt.next() : null);
      insertRow(writer, leftName, rightName, false);
    }
  }

  private void insertCharHeader(Writer writer, String leftHeader, String rightHeader) throws IOException {
    writer.write("<tr><th>");
    writer.write("<b><a name=\"idx");
    writer.write(Integer.toHexString(leftHeader.charAt(0)));
    writer.write("\"></a>");
    writer.write(leftHeader);
    writer.write("</b></th><th> </th><th>");
    if(null != rightHeader) {
      writer.write("<b><a name=\"idx");
      writer.write(Integer.toHexString(rightHeader.charAt(0)));
      writer.write("\"></a>");
      writer.write(rightHeader);
      writer.write("</b>");
    }
    writer.write("</th></tr>");
  }

  private void insertRow(Writer writer, String left, String right, boolean odd) throws IOException {
    writer.write("<tr><td>");
    if (left != null) {
      writer.write(left);
    }
    writer.write("</td><td> </td><td>");
    if (right != null) {
      writer.write(right);
    }
    writer.write("</td></tr>");
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
