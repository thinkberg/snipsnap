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
import org.snipsnap.user.UserManager;
import org.snipsnap.user.User;
import org.snipsnap.app.Application;

import java.util.Iterator;
import java.util.List;
import java.io.IOException;
import java.io.Writer;

/*
 * Macro that displays a list of currently logged on users.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class RecentChangesMacro extends ListoutputMacro {
  public String getName() {
    return "recent-changes";
  }

  public void execute(Writer writer, String[] params, String content, Snip snip)
      throws IllegalArgumentException, IOException {

    String type = "Vertical";
    int length = 10;
    if(params != null) {
      if(params.length > 0) {
        type = params[0];
      }
      if(params.length > 1) {
        try {
          length = Integer.parseInt(params[1]);
        } catch (NumberFormatException e) {
          System.err.println("RecentChangesMacro: illegal parameter count='"+params[1]+"'");
        }
      }
    }

    if (params == null || params.length <= 1) {
      List changed = SnipSpace.getInstance().getChanged(length);
      output(writer, "Recently Changed:", changed, "No changes yet.", type);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
