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

package com.neotis.snip.filter.macro;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;

import java.util.Iterator;
import java.util.List;

public class UserSnipMacro extends Macro {
  StringBuffer buffer;
  SnipSpace space;

  public UserSnipMacro() {
    buffer = new StringBuffer();
    space = SnipSpace.getInstance();
  }

  public String execute(String[] params, String content, Snip snip) throws IllegalArgumentException {
    if (params.length == 1) {
      buffer.setLength(0);
      buffer.append("<b>this user's snips: (");
      List snips = space.getByUser(params[0]);
      buffer.append(snips.size());
      buffer.append(") </b><br/>");
      if (snips.size() > 0) {
        buffer.append("<blockquote>");
        Iterator snipsIterator = snips.iterator();
        while (snipsIterator.hasNext()) {
          Snip aSnip = (Snip) snipsIterator.next();
          buffer.append("<a href=\"/space/");
          buffer.append(aSnip.getName());
          buffer.append("\">");
          buffer.append(aSnip.getName());
          buffer.append("</a>");
          if (snipsIterator.hasNext()) {
            buffer.append(", ");
          }
        }
        buffer.append("</blockquote>");
      } else {
        buffer.append("none written yet.");
      }
      return buffer.toString();
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
