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
import org.radeox.util.Encoder;
import org.radeox.util.logging.Logger;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

/**
 * Renders backlinks
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class BackLinks {
  public static void appendTo(Writer writer, Links backLinks, int count) {
    Iterator iterator = backLinks.iterator();

    try {
      if (iterator.hasNext()) {
        writer.write("<span class=\"caption\">people came here from:</span>\n");
        writer.write("<ul class=\"list\">\n");
        while (iterator.hasNext() && --count >= 0) {
          String url = (String) iterator.next();
          writer.write("<li>");
          writer.write("<span class=\"count\">");
          writer.write("" + backLinks.getIntCount(url));
          writer.write("</span>");
          writer.write(" <span class=\"content\"><a href=\"");
          writer.write(Encoder.escape(url));
          writer.write("\">");
          writer.write(SnipLink.cutLength(Encoder.escape(url), 90));
          writer.write("</a></span></li>\n");
        }
        writer.write("</ul>");
      }
    } catch (IOException e) {
      Logger.warn("unable write to writer", e);
    }

  }
}
