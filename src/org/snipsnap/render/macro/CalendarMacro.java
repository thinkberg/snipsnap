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
package org.snipsnap.render.macro;

import org.snipsnap.date.Month;
import org.snipsnap.render.macro.parameter.MacroParameter;

import java.io.IOException;
import java.io.Writer;

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

  public String getDescription() {
    return "Displays a monthly calendar view with links to postings.";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {
    int year = -1;
    int month = -1;
    int paramCount = params.getLength();

    if (paramCount==2) {
      try {
        year = Integer.parseInt(params.get("0"));
      } catch (NumberFormatException e) {
        //System.err.println("CalendarMacro: year is not a number: " + params.get("0"));
      }
      try {
        month = Integer.parseInt(params.get("1"));
      } catch (NumberFormatException e) {
        //System.err.println("CalendarMacro: month is not a number: " + params.get("1"));
      }
    } else if (params.getLength() > 0) {
      System.err.println("CalendarMacro: illegal number of arguments: " + params.getLength());
    }

    Month m = new Month();
    if (-1 == year || -1 == month) {
      writer.write(m.getView(paramCount==2));
    } else {
      // Only show navigatgion when there where two parameters
      writer.write(m.getView(month, year, paramCount==2));
    }
  }

}
