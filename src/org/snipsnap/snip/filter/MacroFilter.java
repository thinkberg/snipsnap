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

package org.snipsnap.snip.filter;

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.filter.macro.*;
import org.snipsnap.snip.filter.regex.RegexTokenFilter;
import org.apache.oro.text.regex.MatchResult;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MacroFilter extends RegexTokenFilter {

  private static Map macros;

  public MacroFilter() {
    // @TODO replace with singleton/static
    super("\\{([^:}]*):?(.*?)\\}(.*?)\\{(\\1)\\}", SINGLELINE);
    addRegex("\\{([^:}]*):?(.*?)\\}", "", MULTILINE);

    synchronized(this) {
      if (null == macros) {
        macros = new HashMap();
        add(new LinkMacro());
        add(new AnnotationMacro());
        add(new CodeMacro());
        add(new IsbnMacro());
        add(new ApiMacro());
        add(new TableMacro());
        add(new UserSnipMacro());
        add(new RecentSnipMacro());
        add(new UserMacro());
        add(new SearchMacro());
        add(new WeblogMacro());
        add(new IndexSnipMacro());
        add(new ImageMacro());
      }
    }
  }

  public void add(Macro macro) {
    macros.put(macro.getName(), macro);
  }

  /**
   * Splits a String on a delimiter to a List. The function works like
   * the perl-function split.
   *
   * @param aString    a String to split
   * @param delimiter  a delimiter dividing the entries
   * @return           a Array of splittet Strings
   */

  public static String[] split(String aString, String delimiter) {
    StringTokenizer st = new StringTokenizer(aString, delimiter);
    String[] result = new String[st.countTokens()];
    int i = 0;

    while (st.hasMoreTokens()) {
      result[i++] = st.nextToken();
    }

    return result;
  }

  public void handleMatch(StringBuffer buffer, MatchResult result, Snip snip) {
    String[] params = null;
    String content = null;
    String command = result.group(1);

//    for (int i=0; i<result.groups(); i++) {
//      System.err.println(i+" "+result.group(i));
//    }

    // {tag} ... {tag}
    if (result.group(1).equals(result.group(result.groups() - 1))) {
      // {tag:1|2} ... {tag}
      if (!"".equals(result.group(2))) {
        params = split(result.group(2), "|");
      }
      content = result.group(3);
      // {tag}
    } else {
      if (result.groups() > 1) {
        params = split(result.group(2), "|");
      }
    }

    // @DANGER: recursive calls may replace macros in included source code
    try {
      if (macros.containsKey(command)) {
        Macro macro = (Macro) macros.get(command);
        // recursively filter macros within macros
        if (null != content) {
          content = filter(content, snip);
        }
        macro.execute(buffer, params, content, snip);
      } else if (command.startsWith("!")) {
        // TODO including of other snips
        return;
      } else {
        buffer.append(result.group(0));
        return;
      }
    } catch (Exception e) {
      System.err.println("unable to format macro: " + result.group(1));
      e.printStackTrace();
      buffer.append("?" + result.group(1) + (result.length() > 1 ? ":" + result.group(2) : "") + "?");
      return;
    }
  }
}
