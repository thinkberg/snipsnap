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
 * Transforms multiple \ into single backspaces and escapes other characters.
 *
 * @author leo
 * @team other
 * @version $Id$
 */
package org.snipsnap.render.filter;

import org.apache.oro.text.regex.MatchResult;
import org.snipsnap.snip.Snip;
import org.snipsnap.render.filter.regex.RegexTokenFilter;
import org.snipsnap.render.filter.context.FilterContext;

public class EscapeFilter extends RegexTokenFilter {

  public EscapeFilter() {
    super("\\\\(\\\\\\\\)|\\\\(.)|([<>])");
  }

  public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
    buffer.append(handleMatch(result, context));
  }

  public String handleMatch(MatchResult result, FilterContext context) {
    if (result.group(1) == null) {
      String match = result.group(2);
      if (match == null) {
        match = result.group(3);
      }
      if ("\\".equals(match)) {
        return "\\\\";
      }
      return EscapeFilter.escape(match.charAt(0));
    } else {
      return "&#x005c;";
    }
  }

  public static String escape(int c) {
    return "&#x" + Integer.toHexString(c) + ";";
  }
}
