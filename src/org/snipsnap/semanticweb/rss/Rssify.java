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

package org.snipsnap.semanticweb.rss;

import org.apache.oro.text.regex.*;
import org.radeox.util.logging.Logger;
import org.snipsnap.snip.Snip;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Split snips into sub-units for e.g. RSS. By default header titles
 * are used as seperators.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class Rssify {
  public static List rssify(Snip snip) {
    return rssify(snip.getChildrenDateOrder());
  }

  public static List rssify(List snips) {
    List result = new ArrayList();
    PatternMatcher matcher = new Perl5Matcher();
    PatternCompiler compiler = new Perl5Compiler();
    Pattern pattern;
    PatternMatcherInput input;
    MatchResult matchResult;

    try {
      pattern = compiler.compile("^[[:space:]]*(1(\\.1)*)[[:space:]]+(.*?)$", Perl5Compiler.MULTILINE_MASK);
    } catch (MalformedPatternException e) {
      Logger.warn("Bad pattern.",e);
      return result;
    }

    Iterator iterator = snips.iterator();
    while (iterator.hasNext() && result.size() <= 10) {
      Snip snip = (Snip) iterator.next();

      String content = snip.getContent();

      input = new PatternMatcherInput(content);

      int start = 0;
      String title = "";
      while (matcher.contains(input, pattern)) {
        matchResult = matcher.getMatch();
        String post = content.substring(start, input.getMatchBeginOffset()).trim();
        if (!("".equals(title) && "".equals(post))) {
           add(result, snip, post, title);
        }
        start = input.getMatchEndOffset();
        title = matchResult.group(3).trim();
      }

      add(result, snip, content.substring(start).trim(), title);

    }
    return result;
  }

  private static void add(List list, Snip snip, String content, String title) {
    if (list.size() < 10) {
      list.add(createSnip(snip, content, title));
    }
  }

  private static Snip createSnip(Snip snip, String content, String title) {
    Snip rssSnip = null;
    if ("".equals(title)) {
      rssSnip = new RssSnip(snip, content);
    } else {
      // does the header contain an {anchor} macro? then use the url instead of the Snip url
      int anchorIndex = title.indexOf("{anchor:");
      if (anchorIndex != -1) {
        String url = title.substring(anchorIndex + 8, title.indexOf('}', anchorIndex));
        title = title.substring(0, anchorIndex).trim();
        rssSnip = new RssSnip(snip, content, title, url);
      } else {
        rssSnip = new RssSnip(snip, content, title);
      }
    }
    return rssSnip;
  }
}
