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
 * Class that finds snippets like
 * {link|neotis|http://www.neotis.de} ---> <elink ....>
 * {neotis} -> include neotis wiki
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package org.snipsnap.render.filter;

import org.apache.oro.text.regex.MatchResult;
import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.Snip;
import org.snipsnap.render.filter.regex.RegexTokenFilter;
import org.snipsnap.render.filter.context.FilterContext;
import org.snipsnap.render.filter.context.SnipFilterContext;

public class CalendarFilter extends RegexTokenFilter {

  public CalendarFilter() {
    super("^BEGIN:VCALENDAR(.*?)END:VCALENDAR", SINGLELINE);
  }

  private final static int CALENDAR_PREFIX_LENGTH = "calendar-".length();

  public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
    Snip snip = ((SnipFilterContext) context).getSnip();
    Application app = Application.get();
    AppConfiguration config = app.getConfiguration();
    Snip parent = snip.getParent();

    buffer.append("subscribe to").append(" <a href=\"");

    String file = null;
    StringBuffer url = new StringBuffer("/exec/ical/");
    if (parent != null) {
      file = snip.getName().substring(CALENDAR_PREFIX_LENGTH + parent.getName().length() + 1);
      url.append(parent.getName()).append("/");
      url.append(file);
    } else {
      file = snip.getName().substring(CALENDAR_PREFIX_LENGTH);
      url.append(file);
    }

    String webcalUrl = config.getUrl(url.toString());
    webcalUrl = webcalUrl.substring(webcalUrl.indexOf("//") + 2);
    buffer.append("webcal://").append(webcalUrl);
    buffer.append("\">").append("calendar ").append(file).append("</a>");
  }
}
