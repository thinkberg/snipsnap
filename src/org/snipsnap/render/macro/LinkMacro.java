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

import org.snipsnap.snip.SnipLink;
import org.snipsnap.render.macro.parameter.MacroParameter;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class LinkMacro extends Macro {
  private final static String linkImage = SnipLink.createImage("external-link", "&gt;&gt;");

  public String getName() {
    return "link";
  }

  public String getDescription() {
    return "Generate a weblink.";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    String text = params.get("text", 0);
    String url = params.get("url", 1);
    String img = params.get("img", 2);

    if (url != null && text != null) {
      writer.write("<span class=\"nobr\">");
      if (!"none".equals(img)) {
        writer.write(linkImage);
      }
      writer.write("<a href=\"");
      writer.write(url);
      writer.write("\">");
      writer.write(text);
      writer.write("</a>");
      writer.write("</span>");
    } else if (params.getLength() >= 1) {
      // will be catched by UrlFilter
      writer.write(text);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
    return;
  }
}
