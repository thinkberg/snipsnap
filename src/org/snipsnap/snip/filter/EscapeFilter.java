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
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexTokenFilter;
import com.neotis.snip.Snip;
import org.apache.oro.text.regex.MatchResult;

public class EscapeFilter extends RegexTokenFilter {

  public EscapeFilter() {
    super("\\\\(\\\\\\\\)|\\\\(.)");
  }

  public void handleMatch(StringBuffer buffer, MatchResult result, Snip snip) {
    buffer.append(handleMatch(result, snip));
  }

  public String handleMatch(MatchResult result, Snip snip) {
    if (result.group(1) == null) {
      String match = result.group(2);
      if("\\".equals(match)) {
        return "\\\\";
      }
      return "&#x" + Integer.toHexString((int) result.group(2).charAt(0)) + ";";
    } else {
      return "&#x005c;";
    }
  }

}
