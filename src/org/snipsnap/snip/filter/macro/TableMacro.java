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

import java.util.StringTokenizer;

public class TableMacro extends Macro {

  public String execute(String[] params, String content, Snip snip) throws IllegalArgumentException {
    content = content.trim()+"\n";

    StringTokenizer tokenizer = new StringTokenizer(content, "|\n", true);
    StringBuffer result = new StringBuffer("<table class=\"snip-table\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");
    StringBuffer cell = new StringBuffer();
    StringBuffer row = new StringBuffer();
    boolean firstLine = true;
    boolean odd = true;
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if ("|".equals(token)) {
        cell.insert(0, "<td>").append("</td>");
        row.append(cell);
        cell = new StringBuffer();
      } else if ("\n".equals(token)) {
        // add rest of cell
        cell.insert(0, "<td>").append("</td>");
        row.append(cell);
        cell = new StringBuffer();

        // add row
        result.append("<tr valign=\"top\"");
        if (firstLine) {
          result.append(" class=\"snip-table-header\">");
          firstLine = false;
        } else if (odd) {
          result.append(" class=\"snip-table-odd\">");
          odd = false;
        } else {
          result.append(" class=\"snip-table-even\">");
          odd = true;
        }
        result.append(row).append("</tr>\n");
        row = new StringBuffer();
      } else {
        cell.append(token);
      }
    }
    result.append("</table>");
    return result.toString().trim();
  }

}
