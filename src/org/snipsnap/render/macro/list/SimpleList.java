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

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;

/**
 * Simple list formatter.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SimpleList implements ListFormatter {
  public String getName() {
    return "simple";
  }


  public void format(Writer writer, Linkable current, String listComment, Collection c, String emptyText, boolean showSize)
      throws IOException {
    writer.write("<div class=\"list\"><div class=\"list-title\">");
    writer.write(listComment);
    if (showSize) {
      writer.write(" (");
      writer.write("" + c.size());
      writer.write(")");
    }
    writer.write("</div>");
    if (c.size() > 0) {
      writer.write("<blockquote>");
      Iterator nameIterator = c.iterator();
      while (nameIterator.hasNext()) {
        Object object = nameIterator.next();
        if (object instanceof Linkable) {
          writer.write(((Linkable) object).getLink());
        } else if (object instanceof Nameable) {
          writer.write(((Nameable) object).getName());
        } else {
          writer.write(object.toString());
        }

        if (nameIterator.hasNext()) {
          writer.write(", ");
        }
      }
      writer.write("</blockquote>");
    } else {
      writer.write(emptyText);
    }
    writer.write("</div>");
  }
}
