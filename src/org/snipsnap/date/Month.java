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
package org.snipsnap.date;

import org.snipsnap.snip.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Object that generates a View of the month
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 **/
public class Month {

  private String[] months = { "January", "February", "March", "April", "May", "June", "July", "August", "September",
                              "October", "November", "December" };

//  private String[] months = {
//    "Januar", "Februar", "Maerz", "April",
//    "Mai", "Juni", "Juli", "August",
//    "September", "Oktober", "November", "Dezember"
//  };
  private String[] monthsValue = {
    "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"
  };

  // @TODO Use locale
  private String[] weekDaysShort = { "Mo", "Tu", "We", "Th", "Fr", "Sa", "Su" };
  private String[] weekDaysLong = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };

  /** The days in each month. */
  public final static int dom[] = {
    31, 28, 31, 30, /* jan feb mar apr */
    31, 30, 31, 31, /* may jun jul aug */
    30, 31, 30, 31	/* sep oct nov dec */
  };

  public Month() {
  }

  public String toKey(int year, int month, int day) {
    return year + "-" + (month < 10 ? "0" + month : ""+month) + "-" + (day < 10 ? "0" + day : ""+day);
  }

  /**
   * Returns a set of days (String) in yyyy-mm-dd format with
   * posts in this weblog
   *
   */
  public Set getDays(int month, int year) {
    String start = toKey(year, month, 1);
    String end = toKey(year, month, 31);
    List snips = SnipSpace.getInstance().getByDate(start, end);
    Iterator iterator = snips.iterator();

    Set days = new HashSet();

    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      // test for "2002-03-26" format
      if (snip.getName().length() == 10) {
        days.add(snip.getName());
      }
    }
    return days;
  }

  public String getView() {
    Calendar today = new GregorianCalendar();
    today.setTime(new java.util.Date());
    return getView(today.get(Calendar.MONTH), today.get(Calendar.YEAR));
  }

  // @TODO: convert to use month=1,2,... instead of 0,1,....
  public String getView(int month, int year) {

    Set days = getDays(month+1, year);

    StringBuffer view = new StringBuffer();
    view.append("<table summary=\"Monthly calendar with links to each day's posts\">");
    view.append("<caption class=\"calendar-head\">");
    view.append(months[month]);
    view.append(" ");
    view.append(year);
    view.append("</caption>");

    int leadGap = 0;  // for German Style Monday starting weeks

    if (month < 0 || month > 11)
      throw new IllegalArgumentException("Month " + month + " bad, must be 0-11");

    Calendar today = new GregorianCalendar();
    today.setTime(new java.util.Date());
    int todayNumber = today.get(Calendar.DAY_OF_MONTH);

    GregorianCalendar calendar = new GregorianCalendar(year, month, 1);

    // Compute how much to leave before the first day.
    // getDay() returns 0 for Sunday, which is just right.
    leadGap = calendar.get(Calendar.DAY_OF_WEEK) - 1;
    leadGap = (leadGap - 1);
    if (leadGap < 0) leadGap = 6;

    int daysInMonth = dom[month];
    if (calendar.isLeapYear(calendar.get(Calendar.YEAR)) && month == 1)
      ++daysInMonth;

    view.append(getHeader());
    // view.append("<tr><td>Mo</td><td>Di</td><td>Mi</td><td>Do</td><td>Fr</td><td>Sa</td><td>So</td></tr>");

    StringBuffer week = new StringBuffer();
    // Blank out the labels before 1st day of month
    for (int i = 0; i < leadGap; i++) {
      week.append("<td></td>");
    }

    // Fill in numbers for the day of month.

    for (int i = 1; i <= daysInMonth; i++) {
      String day = "" + i;

      if (days.contains(toKey(year, month+1, i))) {
        day =  SnipLink.createLink( toKey(year, month+1, i), day);
      }

      if (i == todayNumber && month == today.get(Calendar.MONTH) && year == today.get(Calendar.YEAR)) {
        day = "<span class=\"calendar-today\">" + day + "</span>";
      }
      week.append("<td>");
      week.append(day);
      week.append("</td>");

      // wrap if end of line.
      if ((leadGap + i) % 7 == 0) {
        view.append("<tr align=\"right\">");
        view.append(week);
        week.setLength(0);
        view.append("</tr>");
      }

    }
    view.append("<tr>");
    view.append(week);
    view.append("</tr>");
    view.append("</table>");
    return view.toString();
  }

  private String getHeader() {
    StringBuffer buffer = new StringBuffer();
    buffer.append("<tr>");
    for (int i=0; i<weekDaysShort.length; i++) {
      buffer.append("<td class=\"calendar-weekday\" abbr=\"");
      buffer.append(weekDaysLong[i]);
      buffer.append("\">");
      buffer.append(weekDaysShort[i]);
      buffer.append("</td>");
    }
    buffer.append("</tr>");
    return buffer.toString();
  }
}
