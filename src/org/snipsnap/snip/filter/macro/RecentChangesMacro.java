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

import org.snipsnap.snip.SnipSpace;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/*
 * Macro that displays a list of currently logged on users.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class RecentChangesMacro extends ListOutputMacro {
  public String getName() {
    return "recent-changes";
  }

  public String getDescription() {
    return "Displays a list of recently changes snips.";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    String type = "Vertical";
    boolean showSize = false;
    int length = 10;
    if (params.getLength() > 0) {
      try {
        length = Integer.parseInt(params.get("0"));
      } catch (NumberFormatException e) {
        System.err.println("RecentChangesMacro: illegal parameter count='" + params.get("1") + "'");
      }
    }
    if (params.getLength() > 1) {
      type = params.get("1");
    }

    if (params.getLength() <= 3) {
      List changed = SnipSpace.getInstance().getChanged(length);
      output(writer, "Recently Changed:", changed, "No changes yet.", type, showSize);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
