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

package org.snipsnap.render.filter.links;

import org.snipsnap.snip.Links;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.util.ColorRange;
import org.radeox.util.logging.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Renders sniplinks
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipLinks {
  public static void appendTo(Writer writer, Links snipLinks, int width, String start, String end) {
    Iterator iterator = snipLinks.iterator();
    int size = snipLinks.getSize();
    int percentPerCell = 100 / width;
    ColorRange cr = new ColorRange(start, end, Math.max(size <= 20 ? size : 20, 8));

    try {
      int i = 0;
      if (iterator.hasNext()) {
        writer.write("<table cellspacing=\"0\" cellpadding=\"0\" border=\"0\">\n");
        writer.write("<caption>see also:</caption>\n");
        writer.write("<tr>\n");
        while (iterator.hasNext() && i < 20) {
          if (i % width == 0 && i != 0) {
            writer.write("</tr><tr>");
          }
          String url = (String) iterator.next();
          writer.write("<td bgcolor=\"");
          writer.write(cr.getColor(i++));
          writer.write("\" width=\"");
          writer.write("" + percentPerCell);
          writer.write("%\">");
          writer.write(SnipLink.createLink(url, SnipLink.cutLength(url, 25)));
          // writer.write(" - " + snipLinks.getIntCount(url));
          writer.write("</td>\n");
        }
        writer.write("</tr></table>\n");
      }
    } catch (IOException e) {
      Logger.warn("unable write to writer", e);
    }
  }
}
