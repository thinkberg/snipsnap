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

package org.snipsnap.render.macro;

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipUtil;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.render.filter.links.BackLinks;
import org.radeox.macro.parameter.MacroParameter;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

/*
 * Macro that displays a weblog. All subsnips are read and
 * displayed in reverse chronological order.
 *
 * @author stephan
 * @version $Id$
 */

public class WeblogMacro extends SnipMacro {
  private SnipSpace space;
  private String[] paramDescription = {"?1: number of shown posts"};

  public WeblogMacro() {
    space = SnipSpaceFactory.getInstance();
  }

  public String getName() {
    return "weblog";
  }

  public String getDescription() {
    return "Renders the sub-snips of the snip as a weblog.";
  }

  public String[] getParamDescription() {
    return paramDescription;
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {

    if (params.getLength() < 2) {
      int count = 0;
      if (params.getLength() == 1) {
        count = Integer.parseInt(params.get("0"));
      } else {
        count = 10;
      }

      List snips = SnipSpaceFactory.getInstance().getChildrenDateOrder(params.getSnip(), count);

      Iterator iterator = snips.iterator();
      while (iterator.hasNext()) {
        Snip entry = (Snip) iterator.next();
        writer.write("<div class=\"blog-date\">");
        writer.write(SnipUtil.toDate(entry.getName()));
        writer.write(" <a href=\"");
        SnipLink.appendUrl(writer, entry.getName());
        writer.write("\" title=\"Permalink to ");
        writer.write(entry.getName());
        writer.write("\">");
        SnipLink.appendImage(writer, "permalink", "PermaLink");
        writer.write("</a>");
        writer.write("</div>");
        writer.write(entry.getXMLContent());
        writer.write("<div class=\"snip-post-comments\">");
        writer.write(entry.getComments().getCommentString());
        writer.write(" | ");
        writer.write(entry.getComments().getPostString());
        writer.write("</div>\n\n");
        writer.write("<div class=\"snip-backlinks\">");
        BackLinks.appendTo(writer, entry.getAccess().getBackLinks(), 5);
        writer.write("</div>");
      }
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
