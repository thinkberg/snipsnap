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

import org.snipsnap.render.filter.links.SnipLinks;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro that displays a list of the sniplinks for the snip
 * This was inspired by the Everything2
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipLinkMacro extends ListOutputMacro {
  public String getName() {
    return "sniplinks";
  }

  public String getDescription() {
    return "Renders a table of sniplinks for the snip.";
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {
    String start = "#ffffff";
    String end = "#b0b0b0";
    int width = 4;
    if (params.getLength() >= 1) {
      width = Integer.parseInt(params.get("0"));
    }
    SnipLinks.appendTo(writer, params.getSnip().getAccess().getBackLinks(), width, start, end);
  }
}
