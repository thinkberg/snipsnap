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

/*
 * Macro that displays all Snips by user
 *
 * @author stephan
 * @version $Id$
 */
package org.snipsnap.snip.filter.macro;

import org.snipsnap.snip.*;
import org.snipsnap.snip.storage.SnipStorage;

import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.Writer;

public class WeblogMacro extends Macro {
  SnipSpace space;

  public WeblogMacro() {
    space = SnipSpace.getInstance();
  }

  public String getName() {
    return "weblog";
  }

  public String getDescription() {
    return "Renders the sub-snips of the snip as a weblog.";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    if (params == null || params.getLength() < 2) {
      int count = 0;
      if (params != null && params.getLength() == 1) {
        count = Integer.parseInt(params.get("0"));
      } else {
        count = 10;
      }

      List snips = SnipSpace.getInstance().getChildrenDateOrder(params.getSnip(), count);
      Iterator iterator = snips.iterator();
      while (iterator.hasNext()) {
        Snip entry = (Snip) iterator.next();
        writer.write("<div class=\"blog-date\">");
        writer.write(Snip.toDate(entry.getName()));
        writer.write(" <a href=\"");
        SnipLink.appendUrl(writer, entry.getName());
        writer.write("\" title=\"Permalink to ");
        writer.write(entry.getName());
        writer.write("\">");
        SnipLink.appendImage(writer,"permalink","");
        writer.write("</a>");
        writer.write("</div>");
        writer.write(entry.getXMLContent());
        writer.write("<div class=\"snip-post-comments\">");
        writer.write(entry.getComments().getCommentString());
        writer.write(" | ");
        writer.write(entry.getComments().getPostString());
        writer.write("</div>\n\n");
      }

      return;
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
