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
 * Class that finds Regex and handles each token.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.regex;

import org.apache.oro.text.regex.MatchResult;
import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Util;
import com.neotis.snip.Snip;

public abstract class RegexTokenFilter extends RegexFilter implements ActionMatch {

  /**
   * create a new regular expression and set
   */
  public RegexTokenFilter(String regex, boolean multiline) {
    addRegex(regex, "", multiline);
  }

  /**
   * create a new regular expression and set
   */
  public RegexTokenFilter(String regex) {
    addRegex(regex, "");
  }

  public abstract void handleMatch(StringBuffer buffer, MatchResult result, Snip snip);

  public String filter(String input, Snip snip) {
    String result = input;
    int size = pattern.size();
    for (int i = 0; i < size; i++) {
      Pattern p = (Pattern) pattern.get(i);
      String s = (String) substitute.get(i);
      result = Util.substitute(matcher, p, new ActionSubstitution(s, this, snip), result, limit);
    }
    return result;

  }
}