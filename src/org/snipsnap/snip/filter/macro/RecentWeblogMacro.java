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
import org.snipsnap.xmlrpc.SnipSnapPing;
import org.snipsnap.util.Weblog;
import org.snipsnap.serialization.StringBufferWriter;
import org.snipsnap.serialization.Appendable;

import java.util.Collection;
import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;

public class RecentWeblogMacro extends ListoutputMacro {
  public String getName() {
    return "recent-weblog";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {
    String type = "Vertical";
    boolean showSize = false;
    int length = 10;
        if(params != null) {
      if(params.getLength() > 0) {
        type = params.get("0");
      }
      if(params.getLength() > 1) {
        try {
          length = Integer.parseInt(params.get("1"));
        } catch (NumberFormatException e) {
          System.err.println("RecentWeblogMacro: illegal parameter count='"+params.get("1")+"'");
        }
      }
    }

    if (params == null || params.getLength() <= 2) {
      Collection c = SnipSnapPing.getInstance().getChanged(length);
      output(writer, "Recently Changed Weblogs", c, "No new changes", type, showSize);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
