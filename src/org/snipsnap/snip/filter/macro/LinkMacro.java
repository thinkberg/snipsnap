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
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.macro;

import com.neotis.snip.Snip;

public class LinkMacro extends Macro {
  public String execute(String[] params, String content, Snip snip) throws IllegalArgumentException {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<img border=\"0\" alt=\">>\" src=\"../images/arrow.right.gif\">");
    if (params.length == 2) {
      buffer.append("<a href=\"").append(params[1]).append("\">");
    } else if(params.length == 1) {
      buffer.append("<a href=\"").append(params[0]).append("\">");
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
    return (buffer.append(params[0]).append("</a>")).toString();
  }
}
