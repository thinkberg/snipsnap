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

import org.snipsnap.date.Month;
import org.snipsnap.snip.Snip;

/*
 * Macro that displays a list of currently logged on users.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class CalendarMacro extends Macro {
  public String getName() {
    return "calendar";
  }

  public void execute(StringBuffer buffer, String[] params, String content, Snip snip) throws IllegalArgumentException {
    int year = -1;
    int month = -1;
    if (params != null && params.length == 2) {
      try {
        year = Integer.parseInt(params[0]);
      } catch (NumberFormatException e) {
        System.err.println("CalendarMacro: year is not a number: " + params[0]);
      }
      try {
        month = Integer.parseInt(params[1]);
      } catch (NumberFormatException e) {
        System.err.println("CalendarMacro: month is not a number: " + params[1]);
      }
    } else if(params != null && params.length > 0) {
      System.err.println("CalendarMacro: illegal number of arguments: "+params.length);
    }

    Month m = new Month();
    if (-1 == year || -1 == month) {
      buffer.append(m.getView());
    } else {
      buffer.append(m.getView(month, year));
    }
  }
}
