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
 * Transforms header style lines into subsections.
 *
 * @author leo
 * @team other
 * @version $Id$
 */
package org.snipsnap.snip.filter;

import org.snipsnap.snip.filter.regex.RegexTokenFilter;
import org.snipsnap.snip.Snip;
import org.apache.oro.text.regex.MatchResult;

public class HeadingFilter extends RegexTokenFilter {

  public HeadingFilter() {
    super("^[ :space:]*(1(\\.1)*) (.*?)[ :space:]*$");
  }

  public void handleMatch(StringBuffer buffer, MatchResult result, Snip snip) {
    buffer.append(handleMatch(result, snip));
  }

  public String handleMatch(MatchResult result, Snip snip) {
    String indent = result.group(1).replace('.', '-');
    return "<h3 class=\"heading-"+indent+"\">" + result.group(3) + "</h3>";
  }
}
