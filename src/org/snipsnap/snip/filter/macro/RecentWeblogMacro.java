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

import java.util.Collection;
import java.util.Iterator;


public class RecentWeblogMacro extends ListoutputMacro {
  public String getName() {
    return "recent-weblog";
  }

  public void execute(StringBuffer buffer, String[] params, String content, Snip snip) throws IllegalArgumentException {
    if (params.length == 1) {
      Collection c = SnipSnapPing.getInstance().getChanged(Integer.parseInt(params[0]));
      Iterator iterator = c.iterator();
      while (iterator.hasNext()) {
        Weblog weblog = (Weblog) iterator.next();
        weblog.appendTo(buffer);
        buffer.append("<br/>");
      }

    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
