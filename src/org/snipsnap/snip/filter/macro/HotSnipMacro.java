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

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipLink;

import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.Writer;


public class HotSnipMacro extends Macro {
  SnipSpace space;

  public HotSnipMacro() {
    space = SnipSpace.getInstance();
  }

  public String getName() {
    return "snips-by-hotness";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    int length = 10;
    boolean showSize = false;
    if(params != null) {
      if(params.getLength() > 0) {
        try {
          length = Integer.parseInt(params.get("0"));
        } catch (NumberFormatException e) {
          System.err.println("RecentChangesMacro: illegal parameter count='"+params.get("0")+"'");
        }
      }
    }

    if (params == null || params.getLength() <= 3) {
      Collection c = space.getHot(length);
      Iterator iterator = c.iterator();
      writer.write("<div class=\"list\"><div class=\"list-title\">Most viewed:");
      if(showSize) {
        writer.write(" (");
        writer.write(""+length);
        writer.write(")");
      }
      writer.write("</div><ul>");
      while (iterator.hasNext()) {
        Snip hotSnip = (Snip) iterator.next();
        writer.write("<li><span class=\"count\">");
        writer.write(""+hotSnip.getViewCount());
        writer.write("</span>");
        writer.write("<span class=\"content\">");
        SnipLink.appendLink(writer, hotSnip);
        writer.write("</span></li>");
      }
      writer.write("</ul></div>");
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}