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
 * Class that applies a RegexFilter, can be subclassed
 * for special Filters
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package org.snipsnap.snip.filter.regex;

import org.apache.oro.text.regex.Pattern;
import org.apache.oro.text.regex.Perl5Substitution;
import org.apache.oro.text.regex.Util;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.filter.macro.context.FilterContext;
import org.snipsnap.util.log.Logger;

public class RegexReplaceFilter extends RegexFilter {

  public RegexReplaceFilter() {
    super();
  }

  /**
   * create a new regular expression that takes input as multiple lines
   */
  public RegexReplaceFilter(String regex, String substitute) {
    addRegex(regex, substitute);
  }

  /**
   * create a new regular expression and set
   */
  public RegexReplaceFilter(String regex, String substitute, boolean multiline) {
    addRegex(regex, substitute, multiline);
  }

  public String filter(String input, FilterContext context) {
    String result = input;
    int size = pattern.size();
    for (int i = 0; i < size; i++) {
      Pattern p = (Pattern) pattern.get(i);
      String s = (String) substitute.get(i);
      try {
        result = Util.substitute(matcher, p, new Perl5Substitution(s, interps), result, limit);
      } catch (Exception e) {
        Logger.log("<span class=\"error\">Exception</span>: " + this + ": " + e);
        System.err.println("Exception for: " + this);
        e.printStackTrace();
      } catch (Error err) {
        Logger.log("<span class=\"error\">Error</span>: " + this + ": " + err);
        System.err.println("Error for: " + this);
        err.printStackTrace();
      }
    }
    return result;
  }
}