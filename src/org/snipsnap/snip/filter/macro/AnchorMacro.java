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

package org.snipsnap.snip.filter.macro;

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/*
 * Places a HTML anchor tag into the snip
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class AnchorMacro extends Preserved {
  private Map filters;

  public AnchorMacro() {
  }

  public String getName() {
    return "anchor";
  }

  public String getDescription() {
    return "Places a HTML anchor tag in the snip.";
  }


  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    if (params.getLength() == 1) {
      String anchor = params.get("0").replace(' ', '_');
      writer.write("<a name=\"");
      writer.write(anchor);
      writer.write("\"/>");
      writer.write("<a href=\"");
      Snip snip = params.getSnip();
      if (null != snip) {
        SnipLink.appendUrl(writer, snip.getName() + "#" + anchor);
      } else {
        SnipLink.appendUrl(writer, "#" + anchor);
      }
      writer.write("\" title=\"Permalink to ");
      writer.write(anchor);
      writer.write("\">");
      SnipLink.appendImage(writer, "permalink", "");
      writer.write("</a>");
    }
    return;
  }
}
