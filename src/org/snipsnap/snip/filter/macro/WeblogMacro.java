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

import java.util.Iterator;
import java.util.List;

public class WeblogMacro extends Macro {
  SnipSpace space;

  public WeblogMacro() {
    space = SnipSpace.getInstance();
  }

  public String getName() {
    return "weblog";
  }

  public void execute(StringBuffer buffer, String[] params, String content, Snip snip) throws IllegalArgumentException {
    if (params.length < 2) {
      int count = 0;
      if (params.length == 1) {
        count = Integer.parseInt(params[0]);
      } else {
        count = 10;
      }

      List snips = SnipSpace.getInstance().getChildrenDateOrder(snip, count);
      Iterator iterator = snips.iterator();
      while (iterator.hasNext()) {
        Snip entry = (Snip) iterator.next();
        buffer.append("<div class=\"blog-date\">");
        buffer.append(Snip.toDate(entry.getName()));
        buffer.append("</div>");
        buffer.append(entry.getXMLContent());
        buffer.append("<div class=\"comment\"");
        SnipLink.appendLink(buffer, entry.getName(), "Link me");
        buffer.append(" | ");
        buffer.append(entry.getComments().getCommentString());
        buffer.append(" | ");
        buffer.append(entry.getComments().getPostString());
        buffer.append("</div>\n\n");
      }

      return;
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
