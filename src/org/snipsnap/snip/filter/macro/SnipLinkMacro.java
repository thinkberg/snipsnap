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
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.filter.MacroFilter;
import org.snipsnap.snip.filter.links.BackLinks;
import org.snipsnap.snip.filter.links.SnipLinks;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.User;
import org.snipsnap.app.Application;

import java.util.*;
import java.io.IOException;
import java.io.Writer;

/*
 * Macro that displays a list of the backlinks for the snip
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class SnipLinkMacro extends ListoutputMacro {
  public String getName() {
    return "sniplinks";
  }

  public String getDescription() {
    return "Renders a table of sniplinks for the snip.";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException
  {
    String start = "#ffffff";
    String end = "#b0b0b0";
    int width = 4;
    if (params == null || params.getLength() > 1) {
      if (params.getLength() == 1) {
        width  = Integer.parseInt(params.get("0"));
      }
      SnipLinks.appendTo(writer, params.getSnip().getAccess().getBackLinks(), width, start, end);
    } else {
      throw new IllegalArgumentException("SnipLinkMacro: number of arguments does not match");
    }
  }
}
