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
import com.neotis.snip.SnipLink;

import java.util.Iterator;
import java.util.List;

public class RecentSnipMacro extends Macro {
  StringBuffer buffer;
  SnipSpace space;

  public RecentSnipMacro() {
    buffer = new StringBuffer();
    space = SnipSpace.getInstance();
  }

  public String execute(String[] params, String content, Snip snip) throws IllegalArgumentException {
    if (params.length == 1) {
      buffer.setLength(0);
      buffer.append("<b>recently changed snips: (");
      List snips = space.getChanged(Integer.parseInt(params[0]));
      buffer.append(snips.size());
      buffer.append(") </b><br/>");
      if (snips.size() > 0) {
        buffer.append("<blockquote>");
        Iterator snipsIterator = snips.iterator();
        while (snipsIterator.hasNext()) {
          Snip aSnip = (Snip) snipsIterator.next();
          SnipLink.appendLink(buffer, aSnip.getName());
          if (snipsIterator.hasNext()) {
            buffer.append(", ");
          }
        }
        buffer.append("</blockquote>");
      } else {
        buffer.append("no recently changes snips.");
      }
      return buffer.toString();
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
